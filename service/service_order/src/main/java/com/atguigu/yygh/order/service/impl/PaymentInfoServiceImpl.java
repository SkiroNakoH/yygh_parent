package com.atguigu.yygh.order.service.impl;

import com.atguigu.yygh.enums.PaymentStatusEnum;
import com.atguigu.yygh.enums.PaymentTypeEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.PaymentInfo;
import com.atguigu.yygh.order.mapper.PaymentInfoMapper;
import com.atguigu.yygh.order.service.PaymentInfoService;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;

@Service
public class PaymentInfoServiceImpl extends ServiceImpl<PaymentInfoMapper,PaymentInfo> implements PaymentInfoService {

    //生成订单
    @Override
    public void add(OrderInfo orderInfo, Map<String, String> resultMap) {
        PaymentInfo paymentInfo = new PaymentInfo();

        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setPaymentType(PaymentTypeEnum.WEIXIN.getStatus());
        paymentInfo.setTradeNo(resultMap.get("transaction_id")); //微信支付系统的订单号
        paymentInfo.setTotalAmount(orderInfo.getAmount());

        Date reserveDate = orderInfo.getReserveDate(); //就诊日期
        String reserveDateString = new DateTime(reserveDate).toString("yyyy/MM/dd");
        String subject = reserveDateString + "就诊"+ orderInfo.getDepname();
        paymentInfo.setSubject(subject); //交易内容

        paymentInfo.setPaymentStatus(PaymentStatusEnum.PAID.getStatus());
        paymentInfo.setCallbackTime(new DateTime().toDate());
        paymentInfo.setCallbackContent(resultMap.toString());

        //新增支付订单
        baseMapper.insert(paymentInfo);
    }

    //更新支付状态
    @Override
    public void updateStatus(OrderInfo orderInfo, Integer status) {
        UpdateWrapper<PaymentInfo> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq("order_id",orderInfo.getId());
        updateWrapper.set("payment_status",status);

        baseMapper.update( new PaymentInfo(), updateWrapper);
    }
}
