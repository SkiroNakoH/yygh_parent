package com.atguigu.yygh.hosp.controller;


import com.atguigu.yygh.common.exception.YYGHException;
import com.atguigu.yygh.common.utils.MD5;
import com.atguigu.yygh.common.utils.Result;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.HospitalSetQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

/**
 * <p>
 * 医院设置表 前端控制器
 * </p>
 *
 * @author atguigu
 * @since 2023-04-07
 */
@Api(tags = "医院设置接口")
@CrossOrigin //解决前端跨域问题
@RestController
@RequestMapping("/admin/hosp/hospitalSet")
public class HospitalSetController {

    @Autowired
    private HospitalSetService hospitalSetService;

    @ApiOperation("查询所有")
    @GetMapping("/findAll")
    public Result findAll() {
        List<HospitalSet> hospitalSetList = hospitalSetService.list();
        return Result.ok().data("hospitalSetList", hospitalSetList);
    }

    @ApiOperation("根据id删除")
    @DeleteMapping("/{id}")
    public Result deleteById(@ApiParam("医院id") @PathVariable Long id) {

        if (hospitalSetService.removeById(id))
            return Result.ok();

        return Result.error();
    }


    @ApiOperation("分页条件查询")
    @GetMapping("/pageList/{page}/{size}")
    public Result pageList(@ApiParam("当前页数") @PathVariable Integer page,
                           @ApiParam("每页显示条数") @PathVariable Integer size,
                           @ApiParam("查询条件") HospitalSetQueryVo hospitalSetQueryVo) {
        Page<HospitalSet> hospitalSetPage = new Page<>(page, size);

        //查询条件--根据医院名称模糊查询、根据医院code查询
        QueryWrapper<HospitalSet> queryWrapper = new QueryWrapper<>();
        queryWrapper.like(!StringUtils.isEmpty(hospitalSetQueryVo.getHosname()), "hosname", hospitalSetQueryVo.getHosname());
        queryWrapper.eq(!StringUtils.isEmpty(hospitalSetQueryVo.getHoscode()), "hoscode", hospitalSetQueryVo.getHoscode());


        hospitalSetService.page(hospitalSetPage, queryWrapper);

        List<HospitalSet> hospitalSetList = hospitalSetPage.getRecords();
        long total = hospitalSetPage.getTotal();

        return Result.ok().data("hospitalSetList", hospitalSetList).data("total", total);
    }


    @ApiOperation("新增医院")
    @PostMapping("/insert")
    public Result insert(@ApiParam("医院信息") @RequestBody HospitalSet hospitalSet) {
        hospitalSet.setSignKey(MD5.encrypt(UUID.randomUUID().toString()));


        boolean save = hospitalSetService.save(hospitalSet);
        if (save)
            return Result.ok();
        return Result.error();
    }

    @ApiOperation("根据id查询")
    @GetMapping("/{id}")
    public Result getById(@ApiParam("医院id") @PathVariable Long id) {
        if (id < 0) {
            throw new YYGHException(9999, "id不能小于0");
        }
        HospitalSet hospitalSet = hospitalSetService.getById(id);
        return Result.ok().data("hospitalSet", hospitalSet);
    }

    @ApiOperation("根据id修改")
    @PutMapping("/updateById")
    public Result updateById(@RequestBody HospitalSet hospitalSet) {

        boolean update = hospitalSetService.updateById(hospitalSet);
        if (update)
            return Result.ok();
        return Result.error();
    }

    @ApiOperation("根据id批量删除医院")
    @DeleteMapping("/batchDeleteByIds")
    public Result batchDeleteByIds(@ApiParam("需要删除的医院id列表") @RequestBody List<Long> ids) {
        boolean remove = hospitalSetService.removeByIds(ids);
        if (remove)
            return Result.ok();
        return Result.error();
    }

    @ApiOperation("医院上市和下市")
    @PutMapping("/updateStatus/{id}/{status}")
    public Result updateStatus(@ApiParam("医院id") @PathVariable Long id,
                               @ApiParam("医院状态： 0未上市 1上市") @PathVariable Integer status) {
        HospitalSet hospitalSet = new HospitalSet();
        hospitalSet.setId(id);
        hospitalSet.setStatus(status);

        boolean update = hospitalSetService.updateById(hospitalSet);
        if (update)
            return Result.ok();
        return Result.error();
    }

}

