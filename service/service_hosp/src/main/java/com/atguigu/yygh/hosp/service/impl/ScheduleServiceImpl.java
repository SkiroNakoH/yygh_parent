package com.atguigu.yygh.hosp.service.impl;

import com.atguigu.yygh.hosp.repository.ScheduleRepository;
import com.atguigu.yygh.hosp.service.HospitalService;
import com.atguigu.yygh.hosp.service.ScheduleService;
import com.atguigu.yygh.model.hosp.BookingRule;
import com.atguigu.yygh.model.hosp.Department;
import com.atguigu.yygh.model.hosp.Hospital;
import com.atguigu.yygh.model.hosp.Schedule;
import com.atguigu.yygh.vo.hosp.BookingScheduleRuleVo;
import com.atguigu.yygh.vo.hosp.ScheduleQueryVo;
import org.joda.time.DateTime;
import org.joda.time.DateTimeConstants;
import org.joda.time.format.DateTimeFormat;
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
    @Autowired
    private HospitalService hospitalService;

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
                Aggregation.sort(Sort.by(Sort.Order.asc("workDate"))),  //日期升序
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

    //获取可预约排班数据
    @Override

    public Map<String, Object> getBookingScheduleRule(Integer page, Integer size, String hoscode, String depcode) {
   /*   Deprecated: 分页数据需要自己创建
       //查看总数
        Query query = new Query(Criteria.where("hoscode").is(hoscode).and("depcode").is(depcode));
        long total = mongoTemplate.count(query, Schedule.class);
        map.put("total",total); */

        //查看医院信息，找出医院预约规则
        Hospital hospital = hospitalService.getByHoscode(hoscode);
        BookingRule bookingRule = hospital.getBookingRule();

        //1. 日期列表>>>>>>>>查看医院规则中的cycle
        Map<String, Object> dateListmap = createDateList(page, size, bookingRule);

        //TODO: 科室信息
        return null;
    }

    //根据预约规则创建日期列表，并分页
    private Map<String, Object> createDateList(Integer page, Integer size, BookingRule bookingRule) {
        //获取可预约的天数
        Integer cycle = bookingRule.getCycle();

        //判断当天是否超过停止放号时间
        DateTime stopTime = getStopDateTime(bookingRule);

        if (stopTime.isBefore(new DateTime()))
            cycle++;

        //医院规则放号总日期
        List<Date> dateList = new ArrayList<>();
        //循环cycle，造时间
        for (Integer i = 0; i < cycle; i++) {
            //时间仅需yyyy-MM-dd
            DateTime dateTime = new DateTime().plusDays(i);
            String dateString = dateTime.toString("yyyy-MM-dd");
            dateList.add(new DateTime(dateString).toDate());
        }

        //分页医院放号日期
        int start = (page - 1) * size; //开始下标
        int end = start + size - 1; //结束下标

        if(end > dateList.size() - 1)
            end = dateList.size() - 1;

        //分页日期
        ArrayList<Date> pageList = new ArrayList<>();
        for (int i = start; i < end; i++) {
            pageList.add(dateList.get(i));
        }

       Map<String, Object> map = new HashMap<>();
        map.put("total",dateList.size()); //总数
        map.put("list",pageList);   //分页后列表

        return map;
    }

    //判断当天是否超过停止放号时间
    private static DateTime getStopDateTime(BookingRule bookingRule) {
        //判断预约天数是否+1
        String stopTimeString = bookingRule.getStopTime();
        String stopDateString = new DateTime().toString("yyyy-MM-dd ") + stopTimeString;
        DateTime stopTime = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm").parseDateTime(stopDateString);
        return stopTime;
    }


    //根据日期获取周几数据
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
