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
                .data("avater","https://i0.hdslb.com/bfs/article/dc7f556defc5b8218f843c7a06cda5a70c64f0a3.gif@240w_221h_progressive.webp")
                .data("name","超级管理员");
    }

    @ApiOperation("用户注销")
    @PostMapping("/logout")
    public Result logout() {

        return Result.ok();
    }

}
