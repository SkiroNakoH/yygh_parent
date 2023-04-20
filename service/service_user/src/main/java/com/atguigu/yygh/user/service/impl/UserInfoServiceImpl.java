package com.atguigu.yygh.user.service.impl;

import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.common.utils.ResultCode;
import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.user.mapper.UserInfoMapper;
import com.atguigu.yygh.user.service.UserInfoService;
import com.atguigu.yygh.user.utils.JwtUtil;
import com.atguigu.yygh.vo.user.LoginVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * 用户表 服务实现类
 * </p>
 *
 * @author atguigu
 * @since 2023-04-18
 */
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        UserInfo userInfo;
        //判断是否未微信登录
        if (!StringUtils.isEmpty(loginVo.getOpenid())) {
            userInfo = weiXinLogo(loginVo);
        } else {
            //手机验证码登录
            userInfo = messageLogin(loginVo);
        }

        //账户存在,不做处理
        //响应数据
        String name = userInfo.getNickName();
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getName();
        }
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }

        String token = JwtUtil.createToken(userInfo.getId(), name);
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("token", token);

        return map;
    }

    //手机验证码登录方式
    private UserInfo messageLogin(LoginVo loginVo) {
        //1.判断账户和密码是否填写
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new YYGHException(ResultCode.ERROR, "账户或验证码不能为空");
        }

        //从redis中获取短信
        Object shortMessage = redisTemplate.opsForValue().get(phone);
        if (!code.equals(shortMessage))
            throw new YYGHException(ResultCode.ERROR, "验证码有误!");

        //查询数据库
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("phone", phone);
        UserInfo userInfo = baseMapper.selectOne(queryWrapper);
        if (userInfo == null) {
            //新建用户
            userInfo = new UserInfo();
            userInfo.setPhone(phone);
            userInfo.setStatus(1);
            baseMapper.insert(userInfo);
        }

        //账户冻结
        if (userInfo.getStatus() == 0)
            throw new YYGHException(ResultCode.ERROR, "账户已冻结");
        return userInfo;
    }

    //微信扫码登录
    private UserInfo weiXinLogo(LoginVo loginVo) {
        //微信登录，判断是否绑定手机号
        UserInfo userInfo = getByOpenId(loginVo.getOpenid());
        //账户冻结
        if (userInfo.getStatus() == 0)
            throw new YYGHException(ResultCode.ERROR, "账户已冻结");
        //判断用户是否绑定手机
        if (StringUtils.isEmpty(userInfo.getPhone())) {
            String phone = loginVo.getPhone();
            String code = loginVo.getCode();
            //判断登录是否携带手机号
            if (StringUtils.isEmpty(phone))
                throw new YYGHException(ResultCode.ERROR, "第一次扫码登录请绑定手机号");

            if(StringUtils.isEmpty(code))
                throw new YYGHException(ResultCode.ERROR,"短信验证码不能为空");

            //携带手机号-->校验短信
            Object shortMessage = redisTemplate.opsForValue().get(phone);
            if (!code.equals(shortMessage))
                throw new YYGHException(ResultCode.ERROR, "验证码有误!");

            //向数据库中添加手机号
            userInfo.setPhone(loginVo.getPhone());
            userInfo.setStatus(1);
            baseMapper.updateById(userInfo);
        }
        return userInfo;
    }

    @Override
    public UserInfo getByOpenId(String openid) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);

        return baseMapper.selectOne(queryWrapper);
    }
}
