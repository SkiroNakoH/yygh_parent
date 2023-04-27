package com.atguigu.yygh.sms.listener;

import com.atguigu.yygh.redis.constants.MqConst;
import com.atguigu.yygh.sms.service.SmsService;
import com.atguigu.yygh.sms.utils.SmsUtil;
import com.atguigu.yygh.vo.sms.SmsVo;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SmsListener {

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_SMS_ITEM, durable = "true"),
            exchange = @Exchange(MqConst.EXCHANGE_DIRECT_SMS),
            key = MqConst.ROUTING_SMS_ITEM
    ))
    public void sendMessage(SmsVo smsVo) throws TencentCloudSDKException {
        SmsUtil.sendMsg(smsVo);
    }

}
