package com.atguigu.yygh.user.controller;


import com.atguigu.yygh.common.utils.Result;
import com.atguigu.yygh.enums.AuthStatusEnum;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.user.utils.JwtUtil;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
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
    public Result saveUserAuth(@RequestBody UserAuthVo userAuthVo, @RequestHeader String token){
        Long userId = JwtUtil.getUserId(token);
        userInfoService.saveUserAuth(userId,userAuthVo);

        return Result.ok();
    }

    @ApiOperation("获取用户认证信息")
    @GetMapping("/auth/getUserInfo")
    public Result getUserInfo(@RequestHeader String token){
        Long userId = JwtUtil.getUserId(token);
        UserInfo userInfo = userInfoService.getById(userId);
        //添加用户状态描述文章
        String authStatusString = AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus());
        userInfo.getParam().put("authStatusString",authStatusString);

        return Result.ok().data("userInfo",userInfo);
    }

    @ApiOperation("条件分页查询用户列表")
    @GetMapping("/findPage/{page}/{size}")
    public Result findPage(@PathVariable Integer page, @PathVariable Integer size,
                           UserInfoQueryVo userInfoQueryVo){

        Page<UserInfo> pageInfo = userInfoService.findPage(page,size,userInfoQueryVo);
        return Result.ok().data("pageInfo",pageInfo);
    }

}

