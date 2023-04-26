package com.atguigu.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.common.utils.ResultCode;
import com.atguigu.yygh.hosp.client.HospFeignClient;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.order.mapper.OrderInfoMapper;
import com.atguigu.yygh.order.service.OrderInfoService;
import com.atguigu.yygh.order.utils.HttpRequestHelper;
import com.atguigu.yygh.user.client.PatientFeignClient;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

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
    private HospFeignClient hospFeignClient;
    @Autowired
    private PatientFeignClient patientFeignClient;

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

        //todo: 记录信息到预约平台订单表order_info

        //todo：rabbitMq异步处理>>>> 更新排班数据, 修改剩余预约数量

        //todo: rabbitMq异步处理>>>> 发送预约成功的通知短信给就诊人

        return null;
    }
}
