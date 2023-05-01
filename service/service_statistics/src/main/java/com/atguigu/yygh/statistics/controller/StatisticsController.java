package com.atguigu.yygh.statistics.controller;

import com.atguigu.yygh.common.utils.Result;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/admin/statistics")
public class StatisticsController {
    @GetMapping("/orderCount")
    public Result orderCount(){

        return Result.ok();
    }
}
