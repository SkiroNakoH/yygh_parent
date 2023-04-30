package com.atguigu.yygh.order.service;

import com.atguigu.yygh.model.order.OrderInfo;

public interface WxPayService {
    String getCodeUrl(Long orderId) throws Exception;

    boolean hasPay(Long orderId) throws Exception;

    void refund(OrderInfo orderInfo) throws Exception;
}
