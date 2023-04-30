package com.atguigu.yygh.order.service.impl;

import com.atguigu.yygh.enums.PaymentTypeEnum;
import com.atguigu.yygh.enums.RefundStatusEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.RefundInfo;
import com.atguigu.yygh.order.mapper.RefundInfoMapper;
import com.atguigu.yygh.order.service.RefundInfoService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.joda.time.DateTime;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Map;

@Service
public class RefundInfoServiceImpl extends ServiceImpl<RefundInfoMapper,RefundInfo> implements RefundInfoService {
    //生成订单退款信息
    @Override
    public void createRefund(OrderInfo orderInfo, Map<String, String> resultMap) {
        RefundInfo refundInfo = new RefundInfo();
        refundInfo.setOutTradeNo(orderInfo.getOutTradeNo());//对外业务编号
        refundInfo.setOrderId(orderInfo.getId());
        refundInfo.setPaymentType(PaymentTypeEnum.WEIXIN.getStatus());//支付类型

        refundInfo.setTradeNo(resultMap.get("transaction_id"));//微信支付订单号
        refundInfo.setTotalAmount(new BigDecimal(resultMap.get("cash_refund_fee")).divide(new BigDecimal(100)));//退款金额

        Date reserveDate = orderInfo.getReserveDate(); //就诊日期
        String reserveDateString = new DateTime(reserveDate).toString("yyyy/MM/dd");
        String subject = reserveDateString + "就诊"+ orderInfo.getDepname();
        refundInfo.setSubject(subject); //交易内容

        refundInfo.setRefundStatus(RefundStatusEnum.REFUND.getStatus());    //退款状态 已退款
        refundInfo.setCallbackContent(resultMap.toString());   //回调信息
        refundInfo.setCallbackTime(new Date());

        baseMapper.insert(refundInfo);
    }
}
