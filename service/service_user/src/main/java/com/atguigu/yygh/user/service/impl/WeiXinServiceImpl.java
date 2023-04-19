package com.atguigu.yygh.user.service.impl;

import com.atguigu.yygh.user.service.WeiXinService;
import com.atguigu.yygh.user.utils.ConstantPropertiesUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Service
public class WeiXinServiceImpl implements WeiXinService {


    @Override
    public Map<String, Object> getQrParam() throws UnsupportedEncodingException {
        String redirectUri = URLEncoder.encode(ConstantPropertiesUtil.WX_OPEN_REDIRECT_URL, "utf-8");

        Map<String, Object> map = new HashMap<>();
        map.put("appid",ConstantPropertiesUtil.WX_OPEN_APP_ID);
        map.put("scope","snsapi_login");
        map.put("redirectUri",redirectUri);
        map.put("state",Long.toString(System.currentTimeMillis()));

        return map;
    }
}
