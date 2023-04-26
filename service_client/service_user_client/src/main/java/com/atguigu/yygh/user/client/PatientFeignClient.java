package com.atguigu.yygh.user.client;

import com.atguigu.yygh.model.user.Patient;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;


@FeignClient("service-user")
public interface PatientFeignClient {

    @ApiOperation("服务调用-获取就诊人信息")
    @GetMapping("/admin/user/patient/feign/getById/{id}")
    public Patient getById4Feign(@PathVariable("id") Long id);
}
