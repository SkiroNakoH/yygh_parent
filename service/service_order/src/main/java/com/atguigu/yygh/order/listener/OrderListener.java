package com.atguigu.yygh.order.listener;

import com.atguigu.yygh.order.service.OrderInfoService;
import com.atguigu.yygh.redis.constants.MqConst;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class OrderListener {
    @Autowired
    private OrderInfoService orderInfoService;

    @RabbitListener(bindings = @QueueBinding(value = @Queue(MqConst.QUEUE_TASK_8),
            exchange = @Exchange(MqConst.EXCHANGE_DIRECT_TASK),
            key = MqConst.ROUTING_TASK_8))
    public void remind() {
        //查询当天就诊人，发送短信
        orderInfoService.remind();
    }
}
