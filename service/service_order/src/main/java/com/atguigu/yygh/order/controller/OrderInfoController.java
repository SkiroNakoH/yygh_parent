package com.atguigu.yygh.order.controller;


import com.atguigu.yygh.common.utils.Result;
import com.atguigu.yygh.order.service.OrderInfoService;
import com.atguigu.yygh.order.service.impl.OrderInfoServiceImpl;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * <p>
 * 订单表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-04-25
 */
@Api(tags = "订单接口")
@RestController
@RequestMapping("/api/order/orderInfo")
public class OrderInfoController {

    @Autowired
    private OrderInfoService orderInfoService;

    @ApiOperation("预约下单")
    @PostMapping("/subscribeOrder/{scheduleId}/{patientId}")
    public Result subscribeOrder(@PathVariable String scheduleId,@PathVariable Long patientId){
       Long orderId =  orderInfoService.subscribeOrder(scheduleId,patientId);
        return Result.ok().data("orderId",orderId);
    }

}

