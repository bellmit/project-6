package com.miguan.reportview.service.impl;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.reportview.entity.RpTotalDay;
import com.miguan.reportview.entity.RpTotalHour;
import com.miguan.reportview.entity.RpTotalMinute;
import com.miguan.reportview.mapper.RpTotalDayMapper;
import com.miguan.reportview.mapper.RpTotalHourMapper;
import com.miguan.reportview.mapper.SyncRpTotalDataMapper;
import com.miguan.reportview.service.SyncRpTotalDataService;
import com.miguan.reportview.vo.LdRpTotalDay;
import com.miguan.reportview.vo.LdRpTotalHour;
import com.miguan.reportview.vo.LdRpTotalMinute;
import org.springframework.stereotype.Service;
import tool.util.DateUtil;

import javax.annotation.Resource;
import java.util.*;

/**
 * 同步rp_total_day数据 和 rp_total_hour数据
 */
@Service
public class SyncRpTotalDataServiceImpl implements SyncRpTotalDataService {
    @Resource
    private SyncRpTotalDataMapper syncRpTotalDataMapper;

    /**
     * 从clickhouse同步rp_total_hour的数据
     * @param startDh 开始小时，例如：2020082712
     * @param endDh 结束小时，例如：2020082712
     */
    public void syncRpTotalHour(int startDh, int endDh) {
        Map<String, Object> params = new HashMap<>();
        String startDay = DateUtil.dateStr2(DateUtil.valueOf(String.valueOf(startDh), "yyyyMMddHH"));  //开始日期
        String endDay = DateUtil.dateStr2(DateUtil.valueOf(String.valueOf(endDh), "yyyyMMddHH"));  //结束日期日期
        params.put("startDay", startDay);
        params.put("endDay", endDay);
        params.put("startDh", startDh);
        params.put("endDh", endDh);
        List<RpTotalHour> list = syncRpTotalDataMapper.listTotalHour(params);
        if(list == null || list.isEmpty()) {
            return;
        }
        syncRpTotalDataMapper.deleteTotalHour(startDh, endDh);
        syncRpTotalDataMapper.batchSaveTotalHour(list);
    }

    /**
     * 从clickhouse同步rp_total_day的数据
     * @param startDate 开始时间
     * @param endDate 结束时间
     */
    public void syncRpTotalDay(String startDate, String endDate) {
        List<RpTotalDay> list = syncRpTotalDataMapper.listTotalDay(startDate, endDate);
        if(list == null || list.isEmpty()) {
            return;
        }
        syncRpTotalDataMapper.deleteTotalDay(startDate, endDate);
        syncRpTotalDataMapper.batchSaveTotalDay(list);
    }

    /**
     * 从clickhouse同步rp_total_hour的数据
     * @param startDm 开始分钟，例如：202008271250
     * @param endDm 结束分钟，例如：202008271255
     */
    public void syncRpTotalMinute(Long startDm, Long endDm) {
        Map<String, Object> params = new HashMap<>();
        String startDay = DateUtil.dateStr2(DateUtil.valueOf(String.valueOf(startDm), "yyyyMMddHHmm"));  //开始日期
        String endDay = DateUtil.dateStr2(DateUtil.valueOf(String.valueOf(endDm), "yyyyMMddHHmm"));  //结束日期日期
        params.put("startDay", startDay);
        params.put("endDay", endDay);
        params.put("startDm", startDm);
        params.put("endDm", endDm);
        List<RpTotalMinute> list = syncRpTotalDataMapper.listTotalMinute(params);
        if(list == null || list.isEmpty()) {
            return;
        }
        syncRpTotalDataMapper.deleteTotalMinute(startDm, endDm);
        syncRpTotalDataMapper.batchSaveTotalMinute(list);
    }

    /**
     * 从clickhouse同步ld_rp_total_hour的数据
     * @param startDh 开始小时，例如：2020082712
     * @param endDh 结束小时，例如：2020082712
     */
    public void syncLdRpTotalHour(int startDh, int endDh) {
        List<LdRpTotalHour> list = syncRpTotalDataMapper.listLdTotalHour(startDh, endDh);
        if(list == null || list.isEmpty()) {
            return;
        }
        syncRpTotalDataMapper.deleteLdTotalHour(startDh, endDh);
        syncRpTotalDataMapper.batchSaveLdTotalHour(list);
    }

    /**
     * 从clickhouse同步ld_rp_total_day的数据
     * @param startDate 开始时间
     * @param endDate 结束时间
     */
    public void syncLdRpTotalDay(String startDate, String endDate) {
        List<LdRpTotalDay> list = syncRpTotalDataMapper.listLdTotalDay(startDate, endDate);
        if(list == null || list.isEmpty()) {
            return;
        }
        Date startTime = DateUtil.valueOf(startDate);
        Date endTime = DateUtil.valueOf(endDate);
//        List<Map<String, Object>> userRetention =new ArrayList<>();  //活跃用户留存率
//        List<Map<String, Object>> newUserRetention =new ArrayList<>();  //新增用户留存率
        Map<String, Object> userRetention = new HashMap<>();  //活跃用户留存率
        Map<String, Object> newUserRetention = new HashMap<>();  //新增用户留存率
        while(startTime.getTime() <= endTime.getTime()) {
            String nowDay = DateUtil.dateStr2(startTime);
            String yesDay = DateUtil.dateStr2(DateUtil.rollDay(startTime, -1));
            Map<String, Object> map1 = syncRpTotalDataMapper.countUserRetention(yesDay, nowDay);
            userRetention.put(nowDay, map1.get("userRetention"));

            Map<String, Object> map2 = syncRpTotalDataMapper.countNewUserRetention(yesDay, nowDay);
            newUserRetention.put(nowDay, map2.get("newUserRetention"));
            startTime = DateUtil.rollDay(startTime, 1);
        }

        for(int i=0;i<list.size();i++) {
            LdRpTotalDay day = list.get(i);
            String dd = day.getDd();
            day.setUserRetention(userRetention.get(dd) == null ? 0 : (double)userRetention.get(dd));
            day.setNewUserRetention(newUserRetention.get(dd) == null ? 0 : (double)newUserRetention.get(dd));
        }
        if(list == null) {
            return;
        }
        syncRpTotalDataMapper.deleteLdTotalDay(startDate, endDate);
        syncRpTotalDataMapper.batchSaveLdTotalDay(list);
    }

    /**
     * 从clickhouse同步ld_rp_total_hour的数据
     * @param startDm 开始分钟，例如：202008271250
     * @param endDm 结束分钟，例如：202008271255
     */
    public void syncLdRpTotalMinute(Long startDm, Long endDm) {
        Map<String, Object> params = new HashMap<>();
        String startDay = DateUtil.dateStr2(DateUtil.valueOf(String.valueOf(startDm), "yyyyMMddHHmm"));  //开始日期
        String endDay = DateUtil.dateStr2(DateUtil.valueOf(String.valueOf(endDm), "yyyyMMddHHmm"));  //结束日期日期
        params.put("startDay", startDay);
        params.put("endDay", endDay);
        params.put("startDm", startDm);
        params.put("endDm", endDm);
        List<LdRpTotalMinute> list = syncRpTotalDataMapper.listLdTotalMinute(params);
        if(list == null || list.isEmpty()) {
            return;
        }
        syncRpTotalDataMapper.deleteLdTotalMinute(startDm, endDm);
        syncRpTotalDataMapper.batchSaveLdTotalMinute(list);
    }
}
