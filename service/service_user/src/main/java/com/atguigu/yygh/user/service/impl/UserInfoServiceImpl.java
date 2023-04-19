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

    @Override
    public Map<String, Object> login(LoginVo loginVo) {
        //1.判断账户和密码是否填写
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new YYGHException(ResultCode.ERROR, "账户或密码不能为空");
        }

        //TODO 密码验证

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
}
