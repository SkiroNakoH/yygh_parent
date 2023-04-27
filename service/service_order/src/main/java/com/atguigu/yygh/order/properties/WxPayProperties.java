package com.atguigu.yygh.order.properties;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class WxPayProperties implements InitializingBean {

    @Value("${weixin.pay.appid}")
    private String appid;

    @Value("${weixin.pay.partner}")
    private String partner;

    @Value("${weixin.pay.partnerkey}")
    private String partnerkey;

    @Value("${weixin.pay.notifyurl}")
    private String notifyurl;

    @Value("${weixin.pay.cert}")
    private String cert;

    public static String APPID;
    public static String PARTNER;
    public static String PARTNERKEY;
    public static String NOTIFYURL;
    public static String CERT;

    @Override
    public void afterPropertiesSet() throws Exception {
        APPID = appid;
        PARTNER = partner;
        PARTNERKEY = partnerkey;
        NOTIFYURL = notifyurl;
        CERT = cert;
    }
}

