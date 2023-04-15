package com.atguigu.yygh.hosp.interceptor;

import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.common.utils.ResultCode;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.hosp.utils.HttpRequestHelper;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

@Component
public class ApiInterceptor implements HandlerInterceptor {
    @Autowired
    private HospitalSetService hospitalSetService;

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
        //获取医院系统的签名
        String sign = request.getParameter("sign");
        String hoscode = request.getParameter("hoscode");

        //获取mysqldb数据库中的签名
        String signKeyByHosCode = hospitalSetService.getSignKeyByHosCode(hoscode);

        Map<String, Object> stringObjectMap = HttpRequestHelper.switchMap(request.getParameterMap());
        String signDB = HttpRequestHelper.getSign(stringObjectMap, signKeyByHosCode);

        //校验sign
        if (!signDB.equals(sign)) {
            throw new YYGHException(ResultCode.ERROR,"密钥有误");
        }

        return true;
    }
}
