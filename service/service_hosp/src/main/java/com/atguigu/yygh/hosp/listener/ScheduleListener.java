package com.atguigu.yygh.hosp.listener;

import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.redis.constants.MqConst;
import com.atguigu.yygh.vo.order.OrderMqVo;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ScheduleListener {
    @Autowired
    private ScheduleService scheduleService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_ORDER),
            exchange = @Exchange(MqConst.EXCHANGE_DIRECT_ORDER),
            key = MqConst.ROUTING_ORDER
    ))
    public void updateSchedule(OrderMqVo orderMqVo) {
        if (orderMqVo.getAvailableNumber() != null) {
            scheduleService.updateSubscribe(orderMqVo);
        } else {
            //取消预约，排版+1
            scheduleService.plusSchedule(orderMqVo.getScheduleId());
        }
    }
}
