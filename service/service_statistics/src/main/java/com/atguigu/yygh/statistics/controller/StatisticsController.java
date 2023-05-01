package com.atguigu.yygh.statistics.controller;

import com.atguigu.yygh.common.utils.Result;
import com.atguigu.yygh.order.client.OrderFeignClient;
import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Api(tags = "统计接口")
@RestController
@RequestMapping("/admin/statistics")
public class StatisticsController {
    @Autowired
    private OrderFeignClient orderFeignClient;

    @ApiOperation("统计订单")
    @PostMapping("/orderCount")
    public Result orderCount(@RequestBody OrderCountQueryVo orderCountQueryVo) {
        Map<String, Object> map = orderFeignClient.orderCount(orderCountQueryVo);
        return Result.ok().data(map);
    }
}
