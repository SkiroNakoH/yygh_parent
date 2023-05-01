package com.atguigu.yygh.task.schedule;

import com.atguigu.yygh.redis.constants.MqConst;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ScheduleTask {
    @Autowired
    private RabbitTemplate rabbitTemplate;

//    @Scheduled(cron = "0 0/1 * * * ?")
    @Scheduled(cron = "0 0 8 * * ?")
    public void remind(){
        //检查当天就诊人
        //发短信
        rabbitTemplate.convertAndSend(MqConst.EXCHANGE_DIRECT_TASK,MqConst.ROUTING_TASK_8,"");
    }
}
