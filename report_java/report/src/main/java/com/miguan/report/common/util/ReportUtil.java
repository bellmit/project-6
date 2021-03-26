package com.miguan.report.common.util;

import com.miguan.report.common.constant.RevenueConstant;
import org.apache.http.client.utils.DateUtils;

import java.util.Calendar;
import java.util.List;

/**
 * @Description 公用工具类
 * @Author zhangbinglin
 * @Date 2020/6/18 19:11
 **/
public class ReportUtil {

    /**
     * 把day存入dateList后，比较dateList的第0个和第1个时间，是否在同一个时间段内
     * @param dateList 存放间隔时间的list，只有2个值（开始时间存0位，结束时间存1位）
     * @param day 日期字符串，格式yyyy-MM-dd
     * @param timeType 时间类型：2=按周，3=按月
     * @return true：在同一个时间段，false：不在同一个时间段
     */
    public boolean isSameTimeZone(List<String> dateList, String day, Integer timeType) {
        if(dateList.isEmpty()) {
            dateList.add(day);
            return true;
        }

        boolean result = true;
        Calendar firstTime = Calendar.getInstance();
        firstTime.setTime(DateUtil.strToDate(dateList.get(0), "yyyy-MM-dd"));
        Calendar sencodTime = Calendar.getInstance();
        sencodTime.setTime(DateUtil.strToDate(day, "yyyy-MM-dd"));
        if(timeType == RevenueConstant.TIME_TYPE_WEEK) {
            //按周
            firstTime.setFirstDayOfWeek(Calendar.MONDAY);    //设置成自然周
            sencodTime.setFirstDayOfWeek(Calendar.MONDAY);   //设置成自然周
            result = (firstTime.get(Calendar.DAY_OF_WEEK) == sencodTime.get(Calendar.DAY_OF_WEEK));
        } else {
            //按月
            result = (firstTime.get(Calendar.MONTH) == sencodTime.get(Calendar.MONTH));
        }
        if(result == true) {
            if(dateList.size() > 1) {
                dateList.remove(1);
            }
            dateList.add(day);
        }
        return result;
    }

    public static void main(String[] args) {
        Calendar sencodTime = Calendar.getInstance();
        sencodTime.setTime(DateUtil.strToDate("2020-06-22", "yyyy-MM-dd"));
        sencodTime.setFirstDayOfWeek(Calendar.MONDAY);
        System.out.println(sencodTime.get(Calendar.WEEK_OF_YEAR));
    }
}
