package com.atguigu.yygh.hosp.controller;


import com.atguigu.yygh.common.utils.Result;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 医院设置表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-04-07
 */
@Api(tags = "医院设置接口")
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    @ApiOperation("查询所有")
    @GetMapping("/findAll")
    public Result findAll(){
        List<HospitalSet> hospitalSetList = hospitalSetService.list();
        return Result.ok().data("hospitalSetList",hospitalSetList);
    }

    @ApiOperation("根据id删除")
    @DeleteMapping("/{id}")
    public Result deleteById(@ApiParam("医院id") @PathVariable Long id){

        if(hospitalSetService.removeById(id))
            return Result.ok();

        return Result.error();
    }


}

