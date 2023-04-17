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
    @GetMapping("/findScheduleDetail")
    public Result findScheduleDetail(ScheduleQueryVo scheduleQueryVo) {
       /* //测试代码
        try {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Date date = simpleDateFormat.parse("2021-04-22");
            scheduleQueryVo.setWorkDate(date);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }*/

        List<Schedule> list = scheduleService.findScheduleDetail(scheduleQueryVo);
        return Result.ok().data("list", list);
    }

}
