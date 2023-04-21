package com.atguigu.yygh.user.controller;


import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.common.utils.Result;
import com.atguigu.yygh.common.utils.ResultCode;
import com.atguigu.yygh.user.service.WeiXinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Api(tags = "微信接口")
@Controller
@RequestMapping("/admin/user/wx")
public class WeiXinController {

    @Autowired
    private WeiXinService weiXinService;
    @Autowired
    private RedisTemplate redisTemplate;

    @ApiOperation("微信二维码信息")
    @ResponseBody
    @GetMapping("/getQrParam")
    public Result getQrParam(HttpSession session) throws UnsupportedEncodingException {
        String state = System.currentTimeMillis() + session.getId();

        //存入redis中，防止csrf攻击（跨站请求伪造攻击）
        redisTemplate.opsForValue().set(state,"",5, TimeUnit.MINUTES);

        Map<String, Object> map = weiXinService.getQrParam(state);

        return Result.ok().data(map);
    }

    /**
     * @param code  通过code获取access_token
     * @param state 该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验
     * @return
     */
    @ApiOperation("微信扫码回调")
    @GetMapping("/redirectProcessor")
    public String redirectProcessor(String code, String state) throws Exception {
        //防止csrf攻击（跨站请求伪造攻击）
        if(StringUtils.isEmpty(code) || StringUtils.isEmpty(state))
            throw new YYGHException(ResultCode.ERROR,"非法请求");

        //校验state--> 从redis中取出state
        if (!redisTemplate.hasKey(state))
            throw new YYGHException(ResultCode.ERROR,"状态异常");

        Map<String, String> map = weiXinService.redirectProcessor(code);
        return "redirect:http://localhost:3000/weixin/redirect" +
                "?token=" + map.get("token")
                + "&openid=" + map.get("openid")
                + "&name=" + URLEncoder.encode(map.get("name"), "utf-8");
    }
}
