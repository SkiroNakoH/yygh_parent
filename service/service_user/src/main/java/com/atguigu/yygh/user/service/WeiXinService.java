package com.atguigu.yygh.user.service;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface WeiXinService {
    Map<String, Object> getQrParam(String state) throws UnsupportedEncodingException;

    Map<String, String> redirectProcessor(String code) throws Exception;
}
