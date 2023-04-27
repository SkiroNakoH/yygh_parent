package com.atguigu.yygh.order.service.impl;

import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.common.utils.ResultCode;
import com.atguigu.yygh.model.order.OrderInfo;
import com.atguigu.yygh.order.properties.WxPayProperties;
import com.atguigu.yygh.order.service.OrderInfoService;
import com.atguigu.yygh.order.service.WxPayService;
import com.atguigu.yygh.order.utils.HttpClient;
import com.github.wxpay.sdk.WXPayUtil;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Service
public class WxPayServiceImpl implements WxPayService {
    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public String getCodeUrl(Long orderId) throws Exception {
        //判断redis是否有改付款码链接
        Object codeUrlFromRedis = redisTemplate.opsForValue().get(orderId);
        if (codeUrlFromRedis != null)
            return (String) codeUrlFromRedis;

        //查询订单信息
        OrderInfo orderInfo = orderInfoService.getDetailById(orderId);

        //发送给 微信支付系统的统一下单接口 的参数>>>>>>>>>>>>>>>>>>>>>
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", WxPayProperties.APPID); //公众账号ID
        paramMap.put("mch_id", WxPayProperties.PARTNER); //商户号
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr()); //随机字符串--微信工具类提供

        Date reserveDate = orderInfo.getReserveDate(); //就诊日期
        String reserveDateString = new DateTime(reserveDate).toString("yyyy/MM/dd");
        String body = reserveDateString + "就诊" + orderInfo.getDepname();
//        String body = "今日疯狂星期四,V我50看看实力";
        paramMap.put("body", body); //商品描述
        paramMap.put("out_trade_no", orderInfo.getOutTradeNo()); //商户订单号

        //订单总金额，单位为分
        //paramMap.put("total_fee", orderInfo.getAmount().multiply(new BigDecimal("100")).longValue()+"");
        paramMap.put("total_fee", "1"); //测试数据，1分钱
//        paramMap.put("total_fee", "5000"); //测试数据，50大洋

        paramMap.put("spbill_create_ip", "127.0.0.1"); //用户的客户端IP
        paramMap.put("notify_url", WxPayProperties.NOTIFYURL); //通知url,必须为外网可访问的url,不能携带参数
        paramMap.put("trade_type", "NATIVE"); //Native支付-微信用户扫码支付

        //sign
        String generateSignedXml = WXPayUtil.generateSignedXml(paramMap, WxPayProperties.PARTNERKEY);

        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/unifiedorder");
        httpClient.setHttps(true);
        httpClient.setXmlParam(generateSignedXml);
        httpClient.post();

        //处理微信平台响应结果
        String content = httpClient.getContent();
        Map<String, String> resultMap = WXPayUtil.xmlToMap(content);

        if (!"SUCCESS".equals(resultMap.get("return_code")))
            throw new YYGHException(ResultCode.ERROR, "获取付款码错误：" + resultMap.get("return_msg"));

        if (!"SUCCESS".equals(resultMap.get("result_code")))
            throw new YYGHException(ResultCode.ERROR, "获取付款码错误：" + resultMap.get("err_code_des"));

        /*
            注意：code_url的值并非固定，使用时按照URL格式转成二维码即可。时效性为2小时
         */
        String codeUrl = resultMap.get("code_url");
        //存入redis，2小时有效
        redisTemplate.opsForValue().set(orderId, codeUrl, 2, TimeUnit.HOURS);
        return codeUrl;
    }

    @Override
    public boolean hasPay(Long orderId) throws Exception {
        //查询订单信息
        OrderInfo orderInfo = orderInfoService.getDetailById(orderId);

        //发送给 微信支付系统的统一下单接口 的参数>>>>>>>>>>>>>>>>>>>>>
        Map<String, String> paramMap = new HashMap<>();
        paramMap.put("appid", WxPayProperties.APPID); //公众账号ID
        paramMap.put("mch_id", WxPayProperties.PARTNER); //商户号
        paramMap.put("out_trade_no", orderInfo.getOutTradeNo()); //商户订单号
        paramMap.put("nonce_str", WXPayUtil.generateNonceStr()); //随机字符串--微信工具类提供

        //sign
        String generateSignedXml = WXPayUtil.generateSignedXml(paramMap, WxPayProperties.PARTNERKEY);

        HttpClient httpClient = new HttpClient("https://api.mch.weixin.qq.com/pay/orderquery");
        httpClient.setHttps(true);
        httpClient.setXmlParam(generateSignedXml);
        httpClient.post();

        //处理微信平台响应结果
        String content = httpClient.getContent();
        Map<String, String> resultMap = WXPayUtil.xmlToMap(content);

        if (!"SUCCESS".equals(resultMap.get("return_code")))
            throw new YYGHException(ResultCode.ERROR, "获取付款码错误：" + resultMap.get("return_msg"));

        if (!"SUCCESS".equals(resultMap.get("result_code")))
            throw new YYGHException(ResultCode.ERROR, "获取付款码错误：" + resultMap.get("err_code_des"));

        //判断支付状态--> SUCCESS--支付成功
        if ("SUCCESS".equals(resultMap.get("trade_state")))
            return true;

        return false;
    }
}
