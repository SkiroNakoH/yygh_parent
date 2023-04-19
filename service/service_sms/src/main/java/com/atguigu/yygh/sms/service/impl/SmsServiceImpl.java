package com.atguigu.yygh.sms.service.impl;

import com.atguigu.yygh.sms.service.SmsService;
import com.atguigu.yygh.sms.utils.RandomUtil;
import com.atguigu.yygh.sms.utils.SmsUtil;
import com.atguigu.yygh.vo.sms.SmsVo;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class SmsServiceImpl implements SmsService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean sendShortMessage(String phone) throws TencentCloudSDKException {
        //判断短信时效性是否失效----> 从redis中获取
        Object messageSurvival = redisTemplate.opsForValue().get(phone);
        if(messageSurvival != null){
            //短信验证码未失效
            return true;
        }

        String message = RandomUtil.getSixBitRandom(); //获取6位短信验证码
        int validityTime = 5;    //5分钟

        SmsVo smsVo = new SmsVo();
        smsVo.setPhone(phone);
        smsVo.setTemplateCode("1770872");
        smsVo.setParam(new String[]{message,Integer.toString(validityTime)});

        //发送短信
        boolean flag = SmsUtil.sendMsg(smsVo);
        if (flag){
            //发送成功，存入redis
            redisTemplate.opsForValue().set(phone,message,validityTime, TimeUnit.MINUTES);
            return true;
        }

        return false;
    }
}
