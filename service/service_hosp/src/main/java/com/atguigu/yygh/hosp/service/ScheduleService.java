package com.atguigu.yygh.hosp.service;

import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.data.domain.Page;

public interface ScheduleService {
    void save(Schedule schedule);

    Page<Schedule> findPage(String hoscode, Integer page, Integer pageSize);

    void remove(String hoscode, String hosScheduleId);
}
