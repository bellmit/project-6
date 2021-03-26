package com.miguan.advert.common.util;

import lombok.extern.slf4j.Slf4j;
import tool.util.DateUtil;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

/**
 * @auth chenwf
 * @date 2019/10/16
 */
@Slf4j
public class DateUtils extends DateUtil {

    public static final String DATEFORMAT = "MMdd";
    public static final String DATEFORMAT_STR_002 = "MM月dd日 HH:mm";
    public static final String DATEFORMAT_STR_003 = "MM月dd日";
    public static final String DATEFORMAT_STR_004 = "HH:mm:ss";
    public static final String DATEFORMAT_STR_005 = "HH";
    public static final String DATEFORMAT_STR_006 = "yyyy-MM-dd HH";
    public static final String DATEFORMAT_STR_007 = "yyyy-MM-dd";
    public static final String DATEFORMAT_STR_008 = "HH:mm";
    public static final String DATEFORMAT_STR_009 = "yyyy年MM月dd日 HH:mm";
    public static final String DATEFORMAT_STR_010 = "yyyy-MM-dd HH:mm:ss";
    public static final String DATEFORMAT_STR_011 = "yyyyMMdd";
    /**
     * 格式化Date时间
     * @param time Date类型时间
     * @param timeFromat String类型格式
     * @return 格式化后的字符串
     */
    public static String parseDateToStr(Date time, String timeFromat){
        DateFormat dateFormat=new SimpleDateFormat(timeFromat);
        return dateFormat.format(time);
    }

    /**
     * 分钟数添加
     * @param date
     * @param minCnt
     * @return
     */
    public static Date dateAddMins(Date date, int minCnt) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.MINUTE, minCnt);
        Date newTime = calendar.getTime();
        return newTime;
    }

    /**
     * 分钟数添加
     * @param date
     * @param minCnt
     * @return
     */
    public static Date dateAddSecond(Date date, int minCnt) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.SECOND, minCnt);
        Date newTime = calendar.getTime();
        return newTime;
    }

    /**
     * 得到指定之前的前后几天
     * @param day  把日期往后增加一天.整数往后推,负数往前移动
     * @param date
     * @return
     */
    @SuppressWarnings("static-access")
    public static String getDateStrBefore(int day,Date date,String type){
        SimpleDateFormat sdf = new SimpleDateFormat(type);
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, day);
        return sdf.format(c.getTime());
    }

    /**
     * 计算时间差,单位秒
     * @param date1 开始日期
     * @param date2 结束日期
     * @return
     */
    /*public static int secondBetween(Date date1, Date date2){
        DateFormat sdf=new SimpleDateFormat(DATEFORMAT_STR_001);
        Calendar cal = Calendar.getInstance();
        try {
            Date d1 = sdf.parse(com.miguan.advert.common.util.DateUtil.dateStr4(date1));
            Date d2 = sdf.parse(com.miguan.advert.common.util.DateUtil.dateStr4(date2));
            cal.setTime(d1);
            long time1 = cal.getTimeInMillis();
            cal.setTime(d2);
            long time2 = cal.getTimeInMillis();
            return Integer.parseInt(String.valueOf((time2 - time1) / 1000));
        } catch (ParseException e) {
            log.error(e.getMessage(),e);
        }
        return 0;
    }*/

    /**
     * 计算每天还剩多少秒
     */
    public static Integer caluRedisExpiredTime() {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 1);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        final double seconds = (cal.getTimeInMillis() - System.currentTimeMillis()) / 1000d;
        return (int) (seconds);
    }

    /**
     * 计算每天还剩多少时间
     * @param chronoUnit
     * @return
     */
    public static long calcRedisExpiredTime(ChronoUnit chronoUnit) {
        LocalDateTime start = LocalDate.now().plus(1, ChronoUnit.DAYS).atStartOfDay();
        return LocalDateTime.now().until(start, chronoUnit);
    }

    /**
     * 计算每天还剩多少秒
     */
    public static Integer caluRedisExpiredTimeWeek() {
        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.DAY_OF_MONTH, cal.get(Calendar.DAY_OF_MONTH) + 7);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        final double seconds = (cal.getTimeInMillis() - System.currentTimeMillis()) / 1000d;
        return (int) (seconds);
    }

    /**
     * 计算每天还剩多少时间，字符串
     */
    public static String caluExpiredTime() {
       Integer second =  caluRedisExpiredTime();
        Date date = new Date(second);
        return dateStr(date, DATEFORMAT_STR_004);
    }

    /**
     * 格式化日期
     * @param date
     * @param f
     * @return
     */
    public static String dateStr(Date date, String f) {
        if (date == null) {
            return "";
        } else {
            SimpleDateFormat format = new SimpleDateFormat(f);
            String str = format.format(date);
            return str;
        }
    }

    /**
     * 获取昨天的日期
     * @param date 日期
     * @param day 加减几天
     * @return
     */
    public static Date dateAddDay(Date date, int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.DAY_OF_MONTH, day);
        return calendar.getTime();
    }

    /**
     * 获得当天的某一个时间点
     */

    public  static Integer getHour(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        return hour;
    }

    public  static Integer getMinute(){
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.MINUTE);
        return hour;
    }

    public static Date getJoinDate(Date curDate, String prefix, String suffix) {
        String a = prefix+suffix;
        return strToDate(prefix +" "+ suffix,"yyyy-MM-dd HH:mm:ss");
    }

    public static Date strToDate(String str, String format) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = simpleDateFormat.parse(str);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return date;
    }

    //根据天数获取对应日期
    public static Date getDateByCalendar(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, days);
        return calendar.getTime();
    }

    //判断日期是否相等
    public static boolean sameDate(Date d1, Date d2){
        SimpleDateFormat fmt = new SimpleDateFormat("yyyyMMdd");
        return fmt.format(d1).equals(fmt.format(d2));
    }
    //计算两个时间的差值
    public static long caluRedisExpiredTime(Date beginDate, Date endDate) {
        return (endDate.getTime() -beginDate.getTime()) / 1000;
    }

    /**获取当前时间的整点小时时间
     * @param date
     * @return
     */
    public static String getCurrHourTime(Date date){
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        date = ca.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(date);
    }

    /**获取当前时间的整点小时时间 + 59分钟 + 59秒
     * @param date
     * @return
     */
    public static String getCurrHourTimeAddMins(Date date){
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.MINUTE, 59);
        ca.set(Calendar.SECOND, 59);
        date = ca.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        return sdf.format(date);
    }

    /**获取当前时间的整点小时时间 + 59秒
     * @param date
     * @return
     */
    public static String getCurrHourTimeAddSecond(Date date){
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.SECOND, 59);
        date = ca.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }


    /**获取当前时间的整点小时时间 + 59秒
     * @param date
     * @return
     */
    public static Date getCurrHourTimeAddSecondReturnDate(Date date){
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.SECOND, 59);
        date = ca.getTime();
        return date;
    }

    /**获取当前时间的整点小时时间
     * @param date
     * @return
     */
    public static String getCurrHourTimeSecond(Date date){
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.SECOND, 0);
        date = ca.getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(date);
    }

    /**初始化今天数据
     * @param date
     * @return
     */
    public static Date initializationDay(Date date){
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.HOUR_OF_DAY, 0);
        ca.set(Calendar.MINUTE, 0);
        ca.set(Calendar.SECOND, 0);
        date = ca.getTime();
        return date;
    }

    public static Date getNextMinuteTime(Date date){
        Calendar ca = Calendar.getInstance();
        ca.set(Calendar.SECOND, 0);
        ca.add(Calendar.MINUTE, 1);
        date = ca.getTime();
        return date;
    }

    public static int getHour(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar.get(Calendar.HOUR_OF_DAY);
    }

    /**获取自定义精准时间
     * @param date
     * @return
     */
    public static Date getCustomDate(Date date, Integer hours, Integer minute, Integer second){
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        if(hours != null){
            ca.set(Calendar.HOUR, hours);
        }
        if(minute != null){
            ca.set(Calendar.MINUTE, minute);
        }
        if(second != null){
            ca.set(Calendar.SECOND, second);
        }
        return ca.getTime();
    }

}