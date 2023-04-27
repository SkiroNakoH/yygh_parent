package com.atguigu.yygh.hosp.client;

import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.HospitalSet;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-hosp")
public interface HospFeignClient {

    /**
     * @param id 排班主键
     * @return  param 包含 signKey
     */
    @ApiOperation("服务调用-根据id获取排班信息")
    @GetMapping("/admin/hosp/schedule/getById4Feign/{id}")
    public ScheduleOrderVo getById4Feign(@PathVariable("id") String id);

    @ApiOperation("根据hoscode查询医院")
    @GetMapping("/admin/hosp/hospitalSet/getByHosCode/{hoscode}")
    public HospitalSet getByHosCode(@PathVariable("hoscode") String hoscode);

    @ApiOperation("服务调用-获取医院详细地址")
    @GetMapping("/admin/hosp/hospital/getHospByHoscode4Feign/{hoscode}")
    public Hospital getHospByHoscode4Feign(@PathVariable("hoscode") String hoscode);
}
