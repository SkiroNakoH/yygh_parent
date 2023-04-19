package com.atguigu.yygh.user.service;

import java.io.UnsupportedEncodingException;
import java.util.Map;

public interface WeiXinService {
    Map<String, Object> getQrParam() throws UnsupportedEncodingException;
}
