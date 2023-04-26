package com.atguigu.yygh.user.controller;


import com.atguigu.yygh.common.utils.Result;
import com.atguigu.yygh.model.user.Patient;
import com.atguigu.yygh.user.service.PatientService;
import com.atguigu.yygh.user.utils.JwtUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public Result findAll(@RequestHeader String token) {
        Long userId = JwtUtil.getUserId(token);
        List<Patient> list = patientService.findByUserId(userId);

        return Result.ok().data("list", list);
    }

    @ApiOperation("获取就诊人信息")
    @GetMapping("/getById/{id}")
    public Result getById(@PathVariable Long id) {

        Patient patient = patientService.getById(id);

        return Result.ok().data("patient", patient);
    }

    @ApiOperation("服务调用-获取就诊人信息")
    @GetMapping("/feign/getById/{id}")
    public Patient getById4Feign(@PathVariable Long id) {
        return patientService.getById(id);
    }

    @ApiOperation("新增就诊人")
    @PostMapping("/save")
    public Result save(@RequestBody Patient patient, @RequestHeader String token) {
        Long userId = JwtUtil.getUserId(token);
        patient.setUserId(userId);
        patientService.save(patient);

        return Result.ok();
    }

    @ApiOperation("修改就诊人")
    @PutMapping("/update")
    public Result update(@RequestBody Patient patient) {
        patientService.updateById(patient);
        return Result.ok();
    }

    @ApiOperation("删除就诊人")
    @DeleteMapping("/remove/{id}")
    public Result remove(@PathVariable Long id) {
        patientService.removeById(id);
        return Result.ok();
    }
}

