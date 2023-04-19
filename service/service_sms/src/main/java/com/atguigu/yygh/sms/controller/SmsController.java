package com.atguigu.yygh.sms.controller;

import com.atguigu.yygh.common.utils.Result;
import com.atguigu.yygh.sms.service.SmsService;
import com.tencentcloudapi.common.exception.TencentCloudSDKException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "短信接口")
@RestController
@RequestMapping("/admin/sms")
public class SmsController {

    @Autowired
    private SmsService smsService;

    @ApiOperation("发送短信")
    @GetMapping("/sendShortMessage/{phone}")
    public Result sendShortMessage(@PathVariable String phone) throws TencentCloudSDKException {
        boolean flag = smsService.sendShortMessage(phone);

        if (flag)
            return Result.ok();

        return Result.error();
    }
}
