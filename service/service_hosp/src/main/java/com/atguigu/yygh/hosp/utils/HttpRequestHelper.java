package com.atguigu.yygh.hosp.utils;

import com.atguigu.yygh.common.utils.MD5;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

@Slf4j
public class HttpRequestHelper {

    /**
     * 请求数据获取签名
     */
    public static String getSign(Map<String, Object> paramMap, String signKey) {
        if(paramMap.containsKey("sign")) {
            paramMap.remove("sign");
        }

        if(paramMap.containsKey("logoData")){
            String newLogoData = paramMap.get("logoData").toString().replaceAll(" ", "+");
            paramMap.put("logoData", newLogoData);
        }

        TreeMap<String, Object> sorted = new TreeMap<>(paramMap);
        StringBuilder str = new StringBuilder();
        for (Map.Entry<String, Object> param : sorted.entrySet()) {
            str.append(param.getValue()).append("|");
        }
        str.append(signKey);
        log.info("加密前：" + str.toString());
        String md5Str = MD5.encrypt(str.toString());
        log.info("加密后：" + md5Str);
        return md5Str;
    }

    /**
     * 转换Map中的String[]为Object
     */
    public static Map<String, Object> switchMap(Map<String, String[]> paramMap) {
        Map<String, Object> resultMap = new HashMap<>();
        for (Map.Entry<String, String[]> param : paramMap.entrySet()) {
            resultMap.put(param.getKey(), param.getValue()[0]);
        }
        return resultMap;
    }
}
