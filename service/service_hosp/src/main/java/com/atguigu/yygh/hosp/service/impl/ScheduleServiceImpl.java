package com.atguigu.yygh.hosp.service.impl;

import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * 排班服务
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    //排班新增或修改
    @Override
    public void save(Schedule schedule) {
        //查找是否存在Mongodb库中
        Schedule scheduleDB = scheduleRepository.findByHoscodeAndDepcodeAndHosScheduleId(schedule.getHoscode(), schedule.getDepcode(), schedule.getHosScheduleId());

        if (scheduleDB == null) {
            //新增
            schedule.setCreateTime(new Date());
            schedule.setIsDeleted(0);
        } else {
            //修改
            schedule.setId(scheduleDB.getId());
            schedule.setCreateTime(scheduleDB.getCreateTime());
            schedule.setIsDeleted(scheduleDB.getIsDeleted());
        }

        schedule.setUpdateTime(new Date());
        scheduleRepository.save(schedule);
    }

    //根据hoscode的排班分页查询
    @Override
    public Page<Schedule> findPage(String hoscode, Integer page, Integer pageSize) {

        Schedule schedule = new Schedule();
        schedule.setHoscode(hoscode);
        //查询逻辑未删除
        schedule.setIsDeleted(0);

        return scheduleRepository.findAll(Example.of(schedule), PageRequest.of(page - 1, pageSize, Sort.by(Sort.Order.desc("workDate"))));
    }

    //根据hoscode和hosScheduleId逻辑删除
    //TODO 使用mongoTemplate优化性能，只执行一次mongo语句
    @Override
    public void remove(String hoscode, String hosScheduleId) {
        //根据hoscode和hosScheduleId从MongoDB中查询数据
        Schedule schedule = scheduleRepository.findByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        //查到，且没被删除
        if (schedule != null && schedule.getIsDeleted() == 0){
            //逻辑删除
            schedule.setIsDeleted(1);
            scheduleRepository.save(schedule);
        }

    }
}
