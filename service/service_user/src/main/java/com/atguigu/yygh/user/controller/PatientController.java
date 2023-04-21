package com.atguigu.yygh.user.controller;


import com.atguigu.yygh.common.utils.Result;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.service.PatientService;
import com.atguigu.yygh.user.utils.AuthInfoUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * <p>
 * 就诊人表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-04-21
 */
@Api(tags = "就诊人相关接口")
@RestController
@RequestMapping("/admin/user/patient")
public class PatientController {
    @Autowired
    private PatientService patientService;

    @ApiOperation("通过userId获取就诊人列表")
    @GetMapping("/findAll")
    public Result findAll(HttpServletRequest request){
        Long userId = AuthInfoUtil.getUserId(request);
        List<Patient> list = patientService.findByUserId(userId);

        return Result.ok().data("list",list);
    }
}

