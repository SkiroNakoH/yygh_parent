package com.atguigu.yygh.order.client;

import com.atguigu.yygh.vo.order.OrderCountQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

@FeignClient("service-order")
public interface OrderFeignClient {
    @ApiOperation("预约统计-远程feign")
    @PostMapping("/admin/order/orderInfo/orderCount")
    public Map<String,Object> orderCount(@RequestBody OrderCountQueryVo orderCountQueryVo);
}
