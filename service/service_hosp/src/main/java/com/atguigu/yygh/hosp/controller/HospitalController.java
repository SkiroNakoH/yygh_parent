package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.utils.Result;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.vo.hosp.HospitalQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(tags = "医院接口")
@RestController
@RequestMapping("/admin/hosp/hospital")
public class HospitalController {

    @Autowired
    private HospitalService hospitalService;

    @ApiOperation("分页查询")
    @GetMapping("/findPage/{page}/{size}")
    public Result findPage(@PathVariable Integer page, @PathVariable Integer size,
                           HospitalQueryVo hospitalQueryVo) {

        Map<String, Object> map = hospitalService.findPage(page, size, hospitalQueryVo);

        return Result.ok().data(map);
    }

    @ApiOperation("医院上线或下线")
    @PutMapping("/updateStatus/{id}/{status}")
    public Result updateStatus(@PathVariable String id, @PathVariable Integer status) {
        hospitalService.updateStatus(id, status);

        return Result.ok();
    }

    @ApiOperation("根据Id医院详情")
    @GetMapping("/getHospById/{id}")
    public Result getHospById(@PathVariable String id) {
        Hospital hospital = hospitalService.getById(id);

        return Result.ok().data("hospital", hospital);
    }

    @ApiOperation("根据医院名称模糊查询医院列表")
    @GetMapping("/findHospListByHosname/{hosname}")
    public Result findHospListByHosname(@PathVariable String hosname) {
        List<Hospital> list = hospitalService.findHospListByHosname(hosname);

        return Result.ok().data("list", list);
    }

    /**
     * 参数需求:   hostype, distrioctCode,status,hosname模糊
     * @param hospitalQueryVo
     * @return
     */
    @ApiOperation("用户界面查询")
    @GetMapping("/findQuery4Site")
    public Result findQuery4Site(HospitalQueryVo hospitalQueryVo) {
        List<Hospital>  list = hospitalService.findQuery4Site(hospitalQueryVo);

        return Result.ok().data("list",list);
    }

    @ApiOperation("根据医院编码查看医院详情")
    @GetMapping("/getHospByHoscode/{hoscode}")
    public Result getHospByHoscode(@PathVariable String hoscode) {
        Hospital hospital = hospitalService.getHospByHoscode(hoscode);

        return Result.ok().data("hospital", hospital);
    }

    @ApiOperation("服务调用-获取医院详细地址")
    @GetMapping("/getHospByHoscode4Feign/{hoscode}")
    public Hospital getHospByHoscode4Feign(@PathVariable String hoscode) {
        return hospitalService.getHospByHoscode(hoscode);
    }

}
