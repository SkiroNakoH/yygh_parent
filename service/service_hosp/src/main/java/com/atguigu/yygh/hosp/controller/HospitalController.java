package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.utils.Result;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@Api(tags = "医院接口")
@RestController
@RequestMapping("/admin/hosp/hospital")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @ApiOperation("分页查询")
    @GetMapping("/findPage/{page}/{size}")
    public Result findPage(@PathVariable Integer page, @PathVariable Integer size,
                          HospitalQueryVo hospitalQueryVo){

        Map<String, Object> map = hospitalService.findPage(page,size,hospitalQueryVo);

        return Result.ok().data(map);
    }

    @ApiOperation("医院上线或下线")
    @PutMapping("/updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable String id, @PathVariable Integer status){
        hospitalService.updateStatus(id,status);

        return Result.ok();
    }
}
