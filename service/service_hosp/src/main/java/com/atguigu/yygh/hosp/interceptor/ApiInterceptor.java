package com.atguigu.yygh.hosp.interceptor;

import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.common.utils.ResultCode;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class ApiInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.判断时间戳是否超时
        //获取医院系统的时间戳
        Long timestamp = Long.valueOf(request.getParameter("timestamp"));
        //获取当前时间
        long currentTimeMillis = System.currentTimeMillis();
        if(currentTimeMillis - timestamp > 10000){
            throw new YYGHException(ResultCode.ERROR,"请求超时");
        }

        //2.校验sign



        return true;
    }
}
