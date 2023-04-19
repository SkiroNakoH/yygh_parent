package com.atguigu.yygh.sms.service;

import com.tencentcloudapi.common.exception.TencentCloudSDKException;

public interface SmsService {
    boolean sendShortMessage(String phone) throws TencentCloudSDKException;
}
