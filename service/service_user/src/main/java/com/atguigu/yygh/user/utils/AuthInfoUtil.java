package com.atguigu.yygh.user.utils;

import javax.servlet.http.HttpServletRequest;

//获取当前用户信息工具类
public class AuthInfoUtil {
    //获取当前用户id
    public static Long getUserId(HttpServletRequest request) {
        //从header获取token
        String token = request.getHeader("token");
        //jwt从token获取userid
        Long userId = JwtUtil.getUserId(token);
        return userId;
    }
    //获取当前用户名称
    public static String getUserName(HttpServletRequest request) {
        //从header获取token
        String token = request.getHeader("token");
        //jwt从token获取userid
        String userName = JwtUtil.getUserName(token);
        return userName;
    }
}