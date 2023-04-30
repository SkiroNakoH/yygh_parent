package com.atguigu.yygh.order.controller;

import com.atguigu.yygh.common.utils.Result;
import com.atguigu.yygh.order.service.OrderInfoService;
import com.atguigu.yygh.order.service.WxPayService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "微信支付接口")
@RestController
@RequestMapping("/admin/order/wxPay")
public class WxPayController {

    @Autowired
    private WxPayService wxPayService;
    @Autowired
    private OrderInfoService orderInfoService;

    @ApiOperation("微信支付链接")
    @GetMapping("/getCodeUrl/{orderId}")
    public Result getCodeUrl(@PathVariable Long orderId) throws Exception {
        String codeUrl = wxPayService.getCodeUrl(orderId);
        return Result.ok().data("codeUrl", codeUrl);
    }

    @ApiOperation("查看用户是否支付成功同时修改订单状态")
    @GetMapping("/hasPay/{orderId}")
    public Result hasPay(@PathVariable Long orderId) throws Exception {
        boolean flag = wxPayService.hasPay(orderId);
        return Result.ok().data("flag", flag);
    }
}
