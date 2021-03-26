package com.miguan.laidian.service.impl;

import com.alibaba.fastjson.JSON;
import com.miguan.laidian.entity.Calendar;
import com.miguan.laidian.repositories.CalendarJpaRepository;
import com.miguan.laidian.service.CalendarService;
import com.miguan.laidian.vo.CalendarVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class CalendarServiceImpl implements CalendarService {

    @Resource
    CalendarJpaRepository calendarJpaRepository;

    @Override
    public void save(List<Map<String, String>> list) {
        //创建list
        List<Calendar> listCalendar = new ArrayList<>();
        //遍历List（map）转换成List（Calendar）
        for (int i =0 ;list.size()>i;i++){
            Map<String, String> stringStringMap = list.get(i);
            Calendar calendar = JSON.parseObject(JSON.toJSONString(stringStringMap), Calendar.class);
            listCalendar.add(calendar);
        }
        calendarJpaRepository.saveAll(listCalendar);
    }

    @Override
    public CalendarVo queryCalendarToday() {
        Calendar calendar = calendarJpaRepository.queryCalendarToday();
        CalendarVo calendarVo = new CalendarVo();
        BeanUtils.copyProperties(calendar,calendarVo);
        calendarVo.setVacation(priorityDisplay(calendar));
        return calendarVo;
    }

    //特殊节日大于公历大于农历大于节气
    public String priorityDisplay(Calendar calendar) {
        //赋值特殊节日
        if(StringUtils.isNotEmpty(calendar.getSpecialFestival())){
            return calendar.getSpecialFestival();
        }
        //赋值公历节日
        if(StringUtils.isNotEmpty(calendar.getGregorianFestival())){
            return calendar.getGregorianFestival();
        }
        //赋值农历节日
        if(StringUtils.isNotEmpty(calendar.getLunarFestival())){
            return calendar.getLunarFestival();
        }
        //赋值节气
        if(StringUtils.isNotEmpty(calendar.getSolarTerms())){
            return calendar.getSolarTerms();
        }
        return "";
    }
}
