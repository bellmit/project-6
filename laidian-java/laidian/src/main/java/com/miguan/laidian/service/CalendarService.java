package com.miguan.laidian.service;

import com.miguan.laidian.vo.CalendarVo;

import java.util.List;
import java.util.Map;

public interface CalendarService {

    void save(List<Map<String, String>> maps);

    CalendarVo queryCalendarToday();
}
