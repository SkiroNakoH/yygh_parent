package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.utils.MD5;

import com.atguigu.yygh.hosp.result.Result;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.HospitalSetService;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@Api(tags = "医院管理API接口")
@RestController
@RequestMapping("/api/hosp")
public class ApiController {

    //mongo db
    @Autowired
    private HospitalService hospitalService;

    //mysql db
    @Autowired
    private HospitalSetService hospitalSetService;

    //带sign校验的上传医院接口
    /*@ApiOperation(value = "上传医院")
    @PostMapping("/saveHospital")
    public Result saveHospital(HttpServletRequest request) {

        Map<String, Object> resultMap = new HashMap<>();
        Map<String, String[]> parameterMap = request.getParameterMap();
        for (Map.Entry<String, String[]> param : parameterMap.entrySet()) {
            resultMap.put(param.getKey(), param.getValue()[0]);
        }

        //根据hoscode获取密钥，校验密钥
        String hoscode = (String) resultMap.get("hoscode");
        //1.对hoscode进行非空校验
        if (StringUtils.isEmpty(hoscode)) {
            return Result.error().message("医院编码不能为空");
        }
        //2.  根据医院编码，查询密钥
        String signKey = hospitalSetService.getSignKeyByHosCode(hoscode);
        //3. 密钥加密
        String signKeyMD5 = MD5.encrypt(signKey);

        //4.比对医院前端传入的signkey
        String sign = (String) resultMap.get("sign");
        if (StringUtils.isEmpty(sign) || !sign.equals(signKeyMD5)) {
            return Result.error().message("医院密钥有误");
        }

        //上传医院
        hospitalService.save(resultMap);
        return Result.ok();
    }*/

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

        String hosCode = request.getParameterMap().get("hoscode")[0];
        //非空校验
        if (StringUtils.isEmpty(hosCode)) {
            return Result.fail().message("hoscode不能为空");
        }

        Hospital hospital = hospitalService.getByHoscode(hosCode);
        return Result.ok(hospital);
    }
}