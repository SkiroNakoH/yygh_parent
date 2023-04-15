package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.utils.MD5;

import com.atguigu.yygh.hosp.result.Result;
import com.atguigu.yygh.hosp.service.DepartmentService;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.model.hosp.Schedule;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "医院管理API接口")
@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    @Autowired
    private HospitalService hospitalService;

    @Autowired
    private DepartmentService departmentService;

    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation(value = "上传医院")
    @PostMapping("/saveHospital")
    public Result saveHospital(Hospital hospital) {
        //上传医院
        hospitalService.saveHospital(hospital);
        return Result.ok();
    }

    @ApiOperation("获取医院信息")
    @PostMapping("/hospital/show")
    public Result getByHoscode(HttpServletRequest request) {

        String hosCode = request.getParameter("hoscode");
        //非空校验
        if (StringUtils.isEmpty(hosCode)) {
            return Result.fail().message("hoscode不能为空");
        }

        Hospital hospital = hospitalService.getByHoscode(hosCode);
        return Result.ok(hospital);
    }

    @ApiOperation("上传科室")
    @PostMapping("/saveDepartment")
    public Result saveDepartment(Department department){
        departmentService.save(department);
        return Result.ok();
    }

    @ApiOperation("查询科室")
    @PostMapping("/department/list")
    public Result findDepartmentPage(HttpServletRequest request){
        String hosCode = request.getParameter("hoscode");
        Integer page = Integer.valueOf(request.getParameter("page"));
        Integer pageSize = Integer.valueOf(request.getParameter("limit"));
        //非空校验
        if (StringUtils.isEmpty(hosCode)) {
            return Result.fail().message("hoscode不能为空");
        }
        Page<Department> departmentPage =  departmentService.findPage(hosCode,page,pageSize);
        return Result.ok(departmentPage);
    }

    @ApiOperation("删除科室")
    @PostMapping("/department/remove")
    public Result removeDepartment(HttpServletRequest request){
        String hoscode = request.getParameter("hoscode");
        String depcode = request.getParameter("depcode");

        if (StringUtils.isEmpty(hoscode) || StringUtils.isEmpty(depcode)) {
            return Result.fail().message("医院和科室信息有误");
        }

        departmentService.remove(hoscode,depcode);
        return Result.ok();
    }

    //TODO String 2 Date
    @ApiOperation("上传排班")
    @PostMapping("/saveSchedule")
    public Result saveSchedule(Schedule schedule){
        scheduleService.save(schedule);
        return Result.ok();
    }

    @ApiOperation("查询排班")
    @PostMapping("/schedule/list")
    public Result findSchedulePage(HttpServletRequest request){
        String hoscode = request.getParameter("hoscode");
        Integer page = Integer.valueOf(request.getParameter("page"));
        Integer pageSize = Integer.valueOf(request.getParameter("limit"));

        //非空校验
        if (StringUtils.isEmpty(hoscode)) {
            return Result.fail().message("医院编码不能为空");
        }

        Page<Schedule> schedulePage = scheduleService.findPage(hoscode,page,pageSize);
        return Result.ok(schedulePage);
    }

    @ApiOperation("删除排班")
    @PostMapping("/schedule/remove")
    public Result removeSchedule(HttpServletRequest request){
        String hoscode =request.getParameter("hoscode");
        String hosScheduleId =request.getParameter("hosScheduleId");

        //非空校验
        if (StringUtils.isEmpty(hoscode) || StringUtils.isEmpty(hosScheduleId)) {
            return Result.fail().message("医院编码或排班id有误");
        }

        scheduleService.remove(hoscode,hosScheduleId);
        return Result.ok();
    }
}