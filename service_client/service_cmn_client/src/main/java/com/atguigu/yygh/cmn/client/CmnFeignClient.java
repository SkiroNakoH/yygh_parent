package com.atguigu.yygh.cmn.client;

import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@FeignClient("service-cmn")
public interface CmnFeignClient {

    @GetMapping("/admin/cmn/dict/feign/getNameByValue/{value}")
    public String getNameByValue(@PathVariable("value") String value) ;

    /**
     * 查询医院等级
     * @param parentCode 查询医院等级的id
     * @param value 在医院等级中，根据value查询出对应的等级
     * @return
     */
    @GetMapping("/admin/cmn/dict/feign/getNameByParentCodeAndValue/{parentCode}/{value}")
    public String getNameByParentCodeAndValue(@PathVariable("parentCode") String parentCode,
                                              @PathVariable("value") String value) ;
}
