package com.atguigu.yygh.order.service;

public interface WxPayService {
    String getCodeUrl(Long orderId) throws Exception;

    boolean hasPay(Long orderId) throws Exception;
}
