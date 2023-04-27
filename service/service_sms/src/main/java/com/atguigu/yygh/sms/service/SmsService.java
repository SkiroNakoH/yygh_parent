package com.atguigu.yygh.sms.service;

import com.atguigu.yygh.vo.sms.SmsVo;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;

public interface SmsService {
    boolean sendShortMessage(String phone) throws TencentCloudSDKException;
}
