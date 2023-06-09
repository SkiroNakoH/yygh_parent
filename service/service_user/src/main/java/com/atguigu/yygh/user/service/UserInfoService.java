package com.atguigu.yygh.user.service;

import com.atguigu.yygh.model.user.UserInfo;
import com.atguigu.yygh.vo.user.LoginVo;
import com.atguigu.yygh.vo.user.UserAuthVo;
import com.atguigu.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

/**
 * <p>
 * 用户表 服务类
 * </p>
 *
 * @author atguigu
 * @since 2023-04-18
 */
public interface UserInfoService extends IService<UserInfo> {

    Map<String, Object> login(LoginVo loginVo);

    UserInfo getByOpenId(String openid);

    void saveUserAuth(Long userId, UserAuthVo userAuthVo);

    Page<UserInfo>  findPage(Integer page, Integer size, UserInfoQueryVo userInfoQueryVo);

    void updateStatus(Long id, Integer status);

    void approval(Long id, Integer authStatus);
}
