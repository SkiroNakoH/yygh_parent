package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.BookingScheduleRuleVo;
import com.atguigu.yygh.vo.hosp.ScheduleOrderVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
    void save(Schedule schedule);

    Page<Schedule> findPage(String hoscode, Integer page, Integer pageSize);

    void remove(String hoscode, String hosScheduleId);

    Map<String, Object> arrangeDate(Integer page, Integer size, ScheduleQueryVo scheduleQueryVo);


    List<Schedule> findScheduleDetail(String hoscode, String depcode, String workDate);

    Map<String, Object> getBookingScheduleRule(Integer page, Integer size, String hoscode, String depcode);

    Schedule getByScheduleId(String id);

    ScheduleOrderVo getById4Feign(String id);
}
