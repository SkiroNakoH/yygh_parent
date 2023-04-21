package com.atguigu.yygh.user.controller;


import com.atguigu.yygh.common.utils.Result;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.user.utils.AuthInfoUtil;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * <p>
 * 用户表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-04-18
 */
@Api(tags = "用户信息接口")
@RestController
@RequestMapping("/admin/user/userInfo")
public class UserInfoController {
    @Autowired
    private UserInfoService userInfoService;


    @ApiOperation("用户登录")
    @PostMapping("login")
    public Result login(@RequestBody LoginVo loginVo){
        Map<String,Object> map = userInfoService.login(loginVo);
        return Result.ok().data(map);
    }

    @ApiOperation("用户认证")
    @PostMapping("/auth/saveUserAuth")
    public Result saveUserAuth(@RequestBody UserAuthVo userAuthVo, HttpServletRequest request){
        Long userId = AuthInfoUtil.getUserId(request);
        userInfoService.saveUserAuth(userId,userAuthVo);

        return Result.ok();
    }

    @ApiOperation("获取用户认证信息")
    @GetMapping("/auth/getUserInfo")
    public Result getUserInfo(HttpServletRequest request){
        UserInfo userInfo = userInfoService.getById(AuthInfoUtil.getUserId(request));
        return Result.ok().data("userInfo",userInfo);
    }

}

