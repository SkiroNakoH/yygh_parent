package com.atguigu.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.common.utils.ResultCode;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.enums.PaymentStatusEnum;
import com.atguigu.yygh.hosp.client.HospFeignClient;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.order.mapper.OrderInfoMapper;
import com.atguigu.yygh.order.service.OrderInfoService;
import com.atguigu.yygh.order.service.WxPayService;
import com.atguigu.yygh.order.utils.HttpRequestHelper;
import com.atguigu.yygh.redis.constants.MqConst;
import com.atguigu.yygh.user.client.PatientFeignClient;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import com.atguigu.yygh.vo.order.OrderCountVo;
import com.atguigu.yygh.vo.order.OrderMqVo;
import com.atguigu.yygh.vo.order.OrderQueryVo;
import com.atguigu.yygh.vo.sms.SmsVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 * 订单表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-04-25
 */
@Service
public class OrderInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private HospFeignClient hospFeignClient;
    @Autowired
    private PatientFeignClient patientFeignClient;
    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public Long subscribeOrder(String scheduleId, Long patientId) {
        //获取schedule信息
        ScheduleOrderVo scheduleOrderVo = hospFeignClient.getById4Feign(scheduleId);
        //查询patient信息
        Patient patient = patientFeignClient.getById4Feign(patientId);

        //封装数据
        Map<String, Object> paramMap = new HashMap<>();

        //排班相关
        paramMap.put("hoscode", scheduleOrderVo.getHoscode());
        paramMap.put("depcode", scheduleOrderVo.getDepcode());
        paramMap.put("hosScheduleId", scheduleOrderVo.getHosScheduleId());
        paramMap.put("reserveDate", new DateTime(scheduleOrderVo.getReserveDate()).toString("yyyy-MM-dd"));
        paramMap.put("reserveTime", scheduleOrderVo.getReserveTime());
        paramMap.put("amount", scheduleOrderVo.getAmount());

        //就诊人相关相关信息
        paramMap.put("name", patient.getName());
        paramMap.put("sex", patient.getSex());
        paramMap.put("birthdate", new DateTime(patient.getBirthdate()).toString("yyyy-MM-dd"));
        paramMap.put("phone", patient.getPhone());
        paramMap.put("isMarry", patient.getIsMarry());
        //就诊人 省市区code 详细地址
        paramMap.put("provinceCode", patient.getProvinceCode());
        paramMap.put("cityCode", patient.getCityCode());
        paramMap.put("districtCode", patient.getDistrictCode());
        paramMap.put("address", patient.getAddress());
        //就诊人绑定的联系人相关信息
        paramMap.put("contactsName", patient.getContactsName());
        paramMap.put("contactsCertificatesType", patient.getContactsCertificatesType());
        paramMap.put("contactsCertificatesNo", patient.getContactsCertificatesNo());
        paramMap.put("contactsPhone", patient.getContactsPhone());
        //是否有医保
        paramMap.put("isInsure", patient.getIsInsure());
        //时间戳
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp());

        HospitalSet hospitalSet = hospFeignClient.getByHosCode(scheduleOrderVo.getHoscode());

        //验签参数
        String signKey = HttpRequestHelper.getSign(paramMap, hospitalSet.getSignKey());
        paramMap.put("sign", signKey);

        //发送请求
        JSONObject respone = HttpRequestHelper.sendRequest(paramMap, hospitalSet.getApiUrl() + "/order/submitOrder");

        //用户挂号失败
        if (null == respone || 200 != respone.getIntValue("code"))
            throw new YYGHException(ResultCode.ERROR, "预约失败");


        //预约成功
        JSONObject jsonObject = respone.getJSONObject("data");

        String hosRecordId = jsonObject.getString("hosRecordId");//预约唯一标识
        int number = jsonObject.getIntValue("number");//预约序号
        int reservedNumber = jsonObject.getIntValue("reservedNumber"); //排班可预约数
        int availableNumber = jsonObject.getIntValue("availableNumber");  //排班剩余预约数
        String fetchTime = jsonObject.getString("fetchTime");//取号时间
        String fetchAddress = jsonObject.getString("fetchAddress");  //取号地址

        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setUserId(patient.getUserId());
        //雪花算法生成订单交易号
        orderInfo.setOutTradeNo(IdWorker.getIdStr());

        //填写科室相关信息
        BeanUtils.copyProperties(scheduleOrderVo, orderInfo);
        //就诊人相关信息
        orderInfo.setPatientId(patientId);
        orderInfo.setPatientName(patient.getName());
        orderInfo.setPatientPhone(patient.getPhone());
        //医院系统响应数据
        orderInfo.setHosRecordId(hosRecordId);
        orderInfo.setNumber(number);
        orderInfo.setFetchTime(fetchTime);
        orderInfo.setFetchAddress(fetchAddress);
        //设置订单状态 -->    0,"预约成功，待支付"
        orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());
        //创建订单
        baseMapper.insert(orderInfo);

        //rabbitMq异步处理-更新排班数据, 修改剩余预约数量和总预约数
        OrderMqVo orderMqVo = new OrderMqVo();
        orderMqVo.setReservedNumber(reservedNumber);
        orderMqVo.setAvailableNumber(availableNumber);
        orderMqVo.setScheduleId(scheduleId);
        //路由模式，发送消息
        rabbitTemplate.convertAndSend(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);

        //rabbitMq异步处理>>>> 发送预约成功的通知短信给就诊人
        SmsVo smsVo = new SmsVo();
        smsVo.setPhone(orderInfo.getPatientPhone());

        /*        HashMap<String, Object> map = new HashMap<>();
//        param.put("title", orderInfo.getHosname() + "|" + orderInfo.getDepname() + "|" + orderInfo.getTitle());    //排班信息
//        param.put("takeNumber", orderInfo.getFetchTime() + "在" + orderInfo.getFetchAddress() + "处取号");   //取号信息
        //医院相关信息
        map.put("hosname", orderInfo.getHosname());
        map.put("depname", orderInfo.getDepname());
        map.put("title", orderInfo.getTitle());    //医生职位
        //取号相关信息
        map.put("fetchTime", orderInfo.getFetchTime());//建议取号时间
        map.put("fetchAddress", orderInfo.getFetchAddress());//建议取号地址
        map.put("amount", orderInfo.getAmount());//费用
        map.put("patientName", orderInfo.getPatientName());//就诊人姓名
        map.put("quitTime", orderInfo.getQuitTime());  //退号时间*/

        //短信变量不能超过12个
       /* String[] param = {
                orderInfo.getPatientName(),
                orderInfo.getHosname() + "医院" + orderInfo.getDepname() + "科室的" + orderInfo.getTitle(),
                orderInfo.getAmount() + "元",
                orderInfo.getFetchTime(),
                new DateTime(orderInfo.getQuitTime()).toString("yyyy-MM-dd HH:mm"),
                (String) hospFeignClient.getHospByHoscode4Feign(orderInfo.getHoscode()).getParam().get("fullAddress")
        };*/

        String[] fetchTimeAgg = orderInfo.getFetchTime().split(" ");
        String[] param = {
                orderInfo.getHosname(),
                fetchTimeAgg[0], fetchTimeAgg[1],
                fetchAddress
        };
        smsVo.setParam(param);
        smsVo.setTemplateCode("1782355");
        /*1780301:
          您的预约已成功，请按时就诊。医院:{1}，取号日期:{2}，取号时间{3}，取号地址:{4}。
         */

       /*
        尊敬的{1}您好，您已成功预约{2},挂号费:{3},订单号:{4}
        请持医保卡或身份证在{5}完成取号。
        如不能及时就诊,请于就诊前{6}前取消预约。医院地址:{7}。*/

        //发送短信
        rabbitTemplate.convertAndSend(MqConst.EXCHANGE_DIRECT_SMS, MqConst.ROUTING_SMS_ITEM, smsVo);

        return orderInfo.getId();
    }

    @Override
    public Map<String, Object> findPage(Integer page, Integer size, OrderQueryVo orderQueryVo) {
        Long patientId = orderQueryVo.getPatientId();
        String orderStatus = orderQueryVo.getOrderStatus();

        Page<OrderInfo> infoPage = new Page<>(page, size);

        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", orderQueryVo.getUserId());
        queryWrapper.eq(patientId != null, "patient_id", patientId);
        queryWrapper.eq(!StringUtils.isEmpty(orderStatus), "order_status", orderStatus);

        baseMapper.selectPage(infoPage, queryWrapper);

        List<OrderInfo> list = infoPage.getRecords();
        list.forEach(this::packageStatus);

        Map<String, Object> map = new HashMap<>();
        map.put("total", infoPage.getTotal());
        map.put("list", list);
        return map;
    }

    @Override
    public OrderInfo getDetailById(Long id) {
        OrderInfo orderInfo = this.getById(id);
        packageStatus(orderInfo);
        return orderInfo;
    }

    //支付成功，通知医院
    @Override
    public void paySuccess2Hosp(String hoscode, String hosRecordId) {
        //获取医院设置信息，拿到apiUrl和密钥
        HospitalSet hospitalSet = hospFeignClient.getByHosCode(hoscode);

        //封装数据
        Map<String, Object> paramMap = new HashMap<>();

        //排班相关
        paramMap.put("hoscode", hoscode);
        paramMap.put("hosRecordId", hosRecordId);
        //时间戳
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp());

        //验签参数
        String signKey = HttpRequestHelper.getSign(paramMap, hospitalSet.getSignKey());
        paramMap.put("sign", signKey);

        //发送请求
        JSONObject respone = HttpRequestHelper.sendRequest(paramMap, hospitalSet.getApiUrl() + "/order/updatePayStatus");

        if (null == respone || 200 != respone.getIntValue("code")) {
            throw new YYGHException(ResultCode.ERROR, "更改医院支付信息失败: " + respone.getString("message"));
        }
    }

    //更新订单状态
    @Override
    public void updateOrderStatus(Long orderId, Integer status) {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setId(orderId);
        orderInfo.setOrderStatus(status);

        baseMapper.updateById(orderInfo);
    }

    @Override
    public boolean cancelOrder(Long orderId) throws Exception {
        //1.判断订单是否超时
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        DateTime quitTime = new DateTime(orderInfo.getQuitTime());
        //超时
        if (quitTime.isBeforeNow())
            throw new YYGHException(ResultCode.ERROR, "无法取消预约，已超过退号时间");

        //2.通知医院系统取消预约
        updateCancelStatus(orderInfo);

        //3.如果已支付，则微信退款
        //3.1.更新支付状态，payment_info表
        //3.2.保存退款信息，refund_info表
        if (orderInfo.getOrderStatus() == OrderStatusEnum.PAID.getStatus()) {
            wxPayService.refund(orderInfo);
        }

        //4.更新订单状态>取消预约，order_info表
        updateOrderStatus(orderId,OrderStatusEnum.CANCLE.getStatus());  //取消预约
        //5.更新排班>剩余预约数量+1>>>rabbitMq异步处理
        OrderMqVo orderMqVo = new OrderMqVo();
        orderMqVo.setScheduleId(orderInfo.getScheduleId());
        rabbitTemplate.convertAndSend(MqConst.EXCHANGE_DIRECT_ORDER,MqConst.ROUTING_ORDER,orderMqVo);

        //6.发送取消预约的短信给就诊人
        SmsVo smsVo = new SmsVo();
        smsVo.setPhone(orderInfo.getPatientPhone());
        smsVo.setTemplateCode("1782037");
        //您已成功取消{1}的门诊挂号。
        smsVo.setParam(new String[]{orderInfo.getHosname()});
        //异步请求，发送短信
        rabbitTemplate.convertAndSend(MqConst.EXCHANGE_DIRECT_SMS,MqConst.ROUTING_SMS_ITEM,smsVo);

        return true;
    }

    @Override
    public void remind() {
        QueryWrapper<OrderInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("reserve_date", new DateTime().toString("yyyy-MM-dd"));
        queryWrapper.eq("order_status", OrderStatusEnum.PAID.getStatus());//已支付

        List<OrderInfo> orderInfoList = baseMapper.selectList(queryWrapper);

        orderInfoList.forEach(orderInfo -> {
            SmsVo smsVo = new SmsVo();
            smsVo.setPhone(orderInfo.getPatientPhone());
            smsVo.setTemplateCode("1782203");
            /**
             * 您预约了今日就诊，请准时到达。医院:{1}，取号日期:{2}，取号时间:{3}，取号地址;{4}。
             */
            String[] fetchTimeAgg = orderInfo.getFetchTime().split(" ");
            String[] param = {
                    orderInfo.getHosname(),
                    fetchTimeAgg[0], fetchTimeAgg[1],
                    orderInfo.getFetchAddress()
            };
            smsVo.setParam(param);
            //异步请求，发送短信
            rabbitTemplate.convertAndSend(MqConst.EXCHANGE_DIRECT_SMS,MqConst.ROUTING_SMS_ITEM,smsVo);
        });
    }

    @Override
    public Map<String, Object> orderCount(OrderCountQueryVo orderCountQueryVo) {
        List<OrderCountVo> list = baseMapper.orderCount(orderCountQueryVo);

        List<String> dateList = list.stream().map(OrderCountVo::getReserveDate).collect(Collectors.toList());
        List<Integer> countList = list.stream().map(OrderCountVo::getCount).collect(Collectors.toList());

        //封装数据
        HashMap<String, Object> map = new HashMap<>();
        map.put("dateList",dateList);
        map.put("countList",countList);
        return map;
    }

    //通知医院平台取消预约
    private void updateCancelStatus(OrderInfo orderInfo) {
        //封装数据
        Map<String, Object> paramMap = new HashMap<>();

        //排班相关
        paramMap.put("hoscode", orderInfo.getHoscode());
        paramMap.put("hosRecordId", orderInfo.getHosRecordId());

        //时间戳
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp());

        HospitalSet hospitalSet = hospFeignClient.getByHosCode(orderInfo.getHoscode());

        //验签参数
        String signKey = HttpRequestHelper.getSign(paramMap, hospitalSet.getSignKey());
        paramMap.put("sign", signKey);

        //发送请求
        JSONObject respone = HttpRequestHelper.sendRequest(paramMap, hospitalSet.getApiUrl() + "/order/updateCancelStatus");
        //医院平台取消预约是白
        if (respone == null || respone.getInteger("code") != 200)
            throw new YYGHException(ResultCode.ERROR, "取消预约失败:" + respone.getString("message"));
    }

    //封装状态名
    private void packageStatus(OrderInfo orderInfo) {
        Integer orderStatus = orderInfo.getOrderStatus();

        String orderStatusString = OrderStatusEnum.getStatusNameByStatus(orderStatus);
        orderInfo.getParam().put("orderStatusString", orderStatusString);
    }
}
