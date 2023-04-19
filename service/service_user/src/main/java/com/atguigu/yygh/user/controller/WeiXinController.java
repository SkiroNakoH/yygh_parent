package com.atguigu.yygh.user.controller;


import com.atguigu.yygh.common.utils.Result;
import com.atguigu.yygh.user.service.WeiXinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.UnsupportedEncodingException;
import java.util.Map;

@Api(tags = "微信接口")
@RestController
@RequestMapping("/admin/user/wx")
public class WeiXinController {

    @Autowired
    private WeiXinService weiXinService;

    @ApiOperation("微信二维码信息")
    @GetMapping("/getQrParam")
    public Result getQrParam() throws UnsupportedEncodingException {
        Map<String, Object> map = weiXinService.getQrParam();

        return Result.ok().data(map);
    }

    @ApiOperation("微信扫码回调")
    @GetMapping("/redirect")
    public Result redirect(String code,String state){
        System.out.println("code = " + code);
        System.out.println("state = " + state);

        return Result.ok();
    }
}
