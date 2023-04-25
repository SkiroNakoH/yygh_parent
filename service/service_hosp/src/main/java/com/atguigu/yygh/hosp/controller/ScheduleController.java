package com.atguigu.yygh.hosp.controller;

import com.atguigu.yygh.common.utils.Result;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.BookingScheduleRuleVo;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Api(tags = "排班接口")
@RestController
@RequestMapping("/admin/hosp/schedule")
public class ScheduleController {
    @Autowired
    private ScheduleService scheduleService;

    @ApiOperation("排班日期安排")
    @GetMapping("/arrangeDate/{page}/{size}")
    public Result arrangeDate(@PathVariable Integer page,
                              @PathVariable Integer size,
                              ScheduleQueryVo scheduleQueryVo) {


        Map<String, Object> map = scheduleService.arrangeDate(page, size, scheduleQueryVo);
        return Result.ok().data(map);
    }


    @ApiOperation("当天排班详情")
    @GetMapping("/findScheduleDetail/{hoscode}/{depcode}/{workDate}")
    public Result findScheduleDetail(@PathVariable String hoscode,
            @PathVariable String depcode,
            @PathVariable String workDate
           ) {
        List<Schedule> list = scheduleService.findScheduleDetail(hoscode,depcode,workDate);
        return Result.ok().data("list", list);
    }


    @ApiOperation("分页查询-对前端")
    @GetMapping("/getBookingScheduleRule/{page}/{size}/{hoscode}/{depcode}")
    public Result getBookingScheduleRule(
            @PathVariable Integer page,
            @PathVariable Integer size,
            @PathVariable String hoscode,
            @PathVariable String depcode
    ){
        Map<String,Object> map = scheduleService.getBookingScheduleRule(page,size,hoscode,depcode);
        return Result.ok().data(map);
    }

    @ApiOperation("根据id获取排班信息")
    @GetMapping("/getByScheduleId/{id}")
    public Result getByScheduleId(@PathVariable String id){
        Schedule schedule = scheduleService.getByScheduleId(id);
        return Result.ok().data("schedule",schedule);
    }

}
