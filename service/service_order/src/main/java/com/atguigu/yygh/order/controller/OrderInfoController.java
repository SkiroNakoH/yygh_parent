package com.atguigu.yygh.order.controller;


import com.atguigu.yygh.common.utils.JwtUtil;
import com.atguigu.yygh.common.utils.Result;
import com.atguigu.yygh.enums.OrderStatusEnum;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.order.service.OrderInfoService;
import com.atguigu.yygh.order.service.impl.OrderInfoServiceImpl;
import com.atguigu.yygh.vo.order.OrderQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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
@RequestMapping("/admin/order/orderInfo")
public class OrderInfoController {

    @Autowired
    private OrderInfoService orderInfoService;

    @ApiOperation("预约下单")
    @PostMapping("/subscribeOrder/{scheduleId}/{patientId}")
    public Result subscribeOrder(@PathVariable String scheduleId,@PathVariable Long patientId){
       Long orderId =  orderInfoService.subscribeOrder(scheduleId,patientId);
        return Result.ok().data("orderId",orderId);
    }

    @ApiOperation("状态列表")
    @GetMapping("/getStatusList")
    public Result getStatusList(){
        List<Map<String, Object>> list = OrderStatusEnum.getStatusList();
        return Result.ok().data("list",list);
    }

    @ApiOperation("订单列表")
    @GetMapping("/findPage/{page}/{size}")
    public Result findPage(@PathVariable Integer page, @PathVariable Integer size,
                          @RequestHeader String token,
                          OrderQueryVo orderQueryVo){

        Long userId = JwtUtil.getUserId(token);
        orderQueryVo.setUserId(userId);
        //分页查询
        Map<String,Object> map = orderInfoService.findPage(page,size,orderQueryVo);
        return Result.ok().data(map);
    }
    @ApiOperation("订单详情")
    @GetMapping("/getDetailById/{id}")
    public Result getDetailById(@PathVariable Long id){
      OrderInfo orderInfo = orderInfoService.getDetailById(id);
        return Result.ok().data("orderInfo",orderInfo);
    }

    @ApiOperation("取消订单")
    @PostMapping("/cancelOrder/{orderId}")
    public Result cancelOrder(@PathVariable Long orderId) throws Exception {
        boolean flag = orderInfoService.cancelOrder(orderId);
        return Result.ok().data("flag",flag);
    }

}

