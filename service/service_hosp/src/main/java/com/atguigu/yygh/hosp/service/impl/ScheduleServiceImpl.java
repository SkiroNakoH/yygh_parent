package com.atguigu.yygh.hosp.service.impl;

import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.BookingScheduleRuleVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 排班服务
 */
@Service
public class ScheduleServiceImpl implements ScheduleService {
    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private MongoTemplate mongoTemplate;

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
    @Override
    public void remove(String hoscode, String hosScheduleId) {
     /*   //根据hoscode和hosScheduleId从MongoDB中查询数据
        Schedule schedule = scheduleRepository.findByHoscodeAndHosScheduleId(hoscode, hosScheduleId);
        //查到，且没被删除
        if (schedule != null && schedule.getIsDeleted() == 0){
            //逻辑删除
            schedule.setIsDeleted(1);
            scheduleRepository.save(schedule);
        }*/
        Query query = new Query(Criteria.where("hoscode").is(hoscode).and("hosScheduleId").is(hosScheduleId));

        Update update = new Update();
        update.set("isDeleted", 1);
        mongoTemplate.upsert(query, update, Schedule.class);
    }

    //排班日期安排
    @Override
    public Map<String, Object> arrangeDate(Integer page, Integer size, ScheduleQueryVo scheduleQueryVo) {
        Map<String, Object> map = new HashMap<>();

        //按日期分组查找总数 for 分页
        Aggregation totalAgg = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("hoscode").is(scheduleQueryVo.getHoscode())
                        .and("depcode").is(scheduleQueryVo.getDepcode())),
                Aggregation.group("workDate")
        );

        AggregationResults<BookingScheduleRuleVo> totalBookingScheduleRuleVo =
                mongoTemplate.aggregate(totalAgg, Schedule.class, BookingScheduleRuleVo.class);
        int total = totalBookingScheduleRuleVo.getMappedResults().size();
        map.put("total", total);

        //满条件分页查询
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.match(Criteria.where("hoscode").is(scheduleQueryVo.getHoscode())
                        .and("depcode").is(scheduleQueryVo.getDepcode())),
                Aggregation.group("workDate")
                        .first("workDate").as("workDate")   //时间分组
                        .sum("reservedNumber").as("reservedNumber") //当天总预约数
                        .sum("availableNumber").as("availableNumber"),  //当天可预约数
                Aggregation.skip((page - 1) * size),
                Aggregation.limit(size)
        );
        AggregationResults<BookingScheduleRuleVo> fullConditionBookingScheduleRuleVo =
                mongoTemplate.aggregate(aggregation, Schedule.class, BookingScheduleRuleVo.class);
        List<BookingScheduleRuleVo> bookingScheduleRuleVoList = fullConditionBookingScheduleRuleVo.getMappedResults();

        //日期计算星期
        bookingScheduleRuleVoList.forEach(bookingScheduleRuleVo -> {
            //Date转为joda-time的DateTime
            DateTime dateTime = new DateTime(bookingScheduleRuleVo.getWorkDate());
            String dayOfWeek = getDayOfWeek(dateTime);
            //设置周几
            bookingScheduleRuleVo.setDayOfWeek(dayOfWeek);
        });

        map.put("list", bookingScheduleRuleVoList);

        return map;
    }

    //当天排班详情
    @Override
    public List<Schedule> findScheduleDetail(String hoscode, String depcode, String workDate) {
        Date date = new DateTime(workDate).toDate();

        Query query = new Query(Criteria.where("hoscode").is(hoscode)
                .and("depcode").is(depcode)
                .and("workDate").is(date));

        return mongoTemplate.find(query, Schedule.class);
    }



    /**
     * 根据日期获取周几数据
     *
     * @param dateTime
     * @return
     */
    private String getDayOfWeek(DateTime dateTime) {
        String dayOfWeek = "";
        switch (dateTime.getDayOfWeek()) {
            case DateTimeConstants.SUNDAY:
                dayOfWeek = "周日";
                break;
            case DateTimeConstants.MONDAY:
                dayOfWeek = "周一";
                break;
            case DateTimeConstants.TUESDAY:
                dayOfWeek = "周二";
                break;
            case DateTimeConstants.WEDNESDAY:
                dayOfWeek = "周三";
                break;
            case DateTimeConstants.THURSDAY:
                dayOfWeek = "周四";
                break;
            case DateTimeConstants.FRIDAY:
                dayOfWeek = "周五";
                break;
            case DateTimeConstants.SATURDAY:
                dayOfWeek = "周六";
            default:
                break;
        }
        return dayOfWeek;
    }
}
