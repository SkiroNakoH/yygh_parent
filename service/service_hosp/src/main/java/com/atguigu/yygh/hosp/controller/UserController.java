package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.utils.Result;
import com.atguigu.yygh.model.acl.User;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

@Api(tags = "用户登录相关接口")
@RestController
@RequestMapping("/admin/hosp/user")
public class UserController {

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result login(@RequestBody(required = false) User user) {

        if(user == null)
            return Result.ok(); //解决前端跨域 预检

        String username = user.getUsername();
        String password = user.getPassword();

        if ("admin".equals(username) && "111111".equals(password))
            return Result.ok().data("token", "admin-token");

        return Result.error();
    }

    @ApiOperation("获取用户信息")
    @GetMapping("/info")
    public Result info(String token) {

        return Result.ok()
                .data("roles",new String[]{"admin"})
                .data("introduction","我是超级管理员")
                .data("avatar","https://img.soogif.com/GHooHa0QoDAROuhh1oKyFVqoKAUWX0Rn.gif")
                .data("name","超级管理员");
    }

    @ApiOperation("用户注销")
    @PostMapping("/logout")
    public Result logout() {

        return Result.ok();
    }

}
