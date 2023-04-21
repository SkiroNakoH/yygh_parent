package com.atguigu.yygh.user.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.common.utils.ResultCode;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.user.service.WeiXinService;
import com.atguigu.yygh.user.utils.ConstantPropertiesUtil;
import com.atguigu.yygh.user.utils.HttpClientUtils;
import com.atguigu.yygh.user.utils.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

@Service
public class WeiXinServiceImpl implements WeiXinService {

    @Autowired
    private UserInfoService userInfoService;

    @Override
    public Map<String, Object> getQrParam(String state) throws UnsupportedEncodingException {
        String redirectUri = URLEncoder.encode(ConstantPropertiesUtil.WX_OPEN_REDIRECT_URL, "utf-8");

        Map<String, Object> map = new HashMap<>();
        map.put("appid", ConstantPropertiesUtil.WX_OPEN_APP_ID);
        map.put("scope", "snsapi_login");
        map.put("redirectUri", redirectUri);
        map.put("state", state);

        return map;
    }

    @Override
    public Map<String, String> redirectProcessor(String code) throws Exception {

        //1.使用code获取access_token
//  请求地址: https://api.weixin.qq.com/sns/oauth2/access_token?appid=APPID&secret=SECRET&code=CODE&grant_type=authorization_code
        StringBuffer accessTokenTemplate = new StringBuffer()
                .append("https://api.weixin.qq.com/sns/oauth2/access_token")
                .append("?appid=%s")
                .append("&secret=%s")
                .append("&code=%s")
                .append("&grant_type=authorization_code");
        //赋值
        String accessTokenUrl = String.format(accessTokenTemplate.toString(),
                ConstantPropertiesUtil.WX_OPEN_APP_ID,
                ConstantPropertiesUtil.WX_OPEN_APP_SECRET,
                code);
        //通过code加上appid和appseret换取access_token
        String accessTokenFromWeiXin = HttpClientUtils.get(accessTokenUrl);
        JSONObject accessTokenJson = JSONObject.parseObject(accessTokenFromWeiXin);

        //授权用户唯一标识
        String accessToken = accessTokenJson.getString("access_token");
        //授权用户唯一标识
        String openid = accessTokenJson.getString("openid");

        //查询数据库，是否存在该用户
        UserInfo userInfo = userInfoService.getByOpenId(openid);
        //用户账户被冻结
        if(userInfo != null && userInfo.getStatus() == 0)
            throw new YYGHException(ResultCode.ERROR,"该账户已冻结！");

        if (userInfo == null) {
            //数据库中没有该微信号的此用户
            //使用accessToken请求用户信息
            StringBuffer getUserInfoTemplate = new StringBuffer()
                    .append("https://api.weixin.qq.com/sns/userinfo")
                    .append("?access_token=%s")
                    .append("&openid=%s");
            //赋值
            String getUserInfoUrl = String.format(getUserInfoTemplate.toString(), accessToken, openid);
            //通过access_token和openid获取用户信息
            String userInfoFromWeiXin = HttpClientUtils.get(getUserInfoUrl);
            //转换json，取值
            JSONObject userInfoJSON = JSONObject.parseObject(userInfoFromWeiXin);

            // 查看手机号是否存在，如果存在，修改数据库；如果不存在，添加该用户--> userInfoService的save有此功能     -->     无法获取手机号-->舍弃
            //新增用户
            String nickname = userInfoJSON.getString("nickname");
            String headimgurl = userInfoJSON.getString("headimgurl");

            userInfo = new UserInfo();

            userInfo.setNickName(nickname);
            userInfo.setOpenid(openid);
            userInfo.setHeadimgurl(headimgurl);
            userInfo.setStatus(1);
            userInfoService.save(userInfo);
        }

        //回应数据
        String name = userInfo.getNickName();
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getName();
        }
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }

        Map<String, String> map = new HashMap<String, String>();

        map.put("token", JwtUtil.createToken(userInfo.getId(), name));
        map.put("openid", openid);
        map.put("name", name);
        map.put("headimgurl",userInfo.getHeadimgurl());
        return map;
    }
}
