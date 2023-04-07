package com.atguigu.yygh.hosp.controller;


import com.atguigu.yygh.common.utils.Result;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
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


    @ApiOperation("分页条件查询")
    @GetMapping("/pageList/{page}/{size}")
    public Result pageList(@ApiParam("当前页数") @PathVariable Integer page,
                           @ApiParam("每页显示条数") @PathVariable Integer size,
                           @ApiParam("查询条件") HospitalSetQueryVo hospitalSetQueryVo){
        Page<HospitalSet> hospitalSetPage = new Page<>(page, size);

        //查询条件--根据医院名称模糊查询、根据医院code查询
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(!StringUtils.isEmpty(hospitalSetQueryVo.getHosname()), "hosname",hospitalSetQueryVo.getHosname());
        queryWrapper.eq(!StringUtils.isEmpty(hospitalSetQueryVo.getHoscode()),"hoscode",hospitalSetQueryVo.getHoscode());



        hospitalSetService.page(hospitalSetPage,queryWrapper);

        List<HospitalSet> hospitalSetList = hospitalSetPage.getRecords();
        long total = hospitalSetPage.getTotal();

        return Result.ok().data("hospitalSetList",hospitalSetList).data("total",total);
    }

}

