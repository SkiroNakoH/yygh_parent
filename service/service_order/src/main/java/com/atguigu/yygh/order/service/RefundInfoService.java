package com.atguigu.yygh.order.service;

import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.model.order.RefundInfo;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface RefundInfoService extends IService<RefundInfo> {

    void createRefund(OrderInfo orderInfo, Map<String, String> resultMap);
}
