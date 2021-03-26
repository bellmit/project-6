package com.miguan.idmapping.common.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**日期工具类
 * @author zhongli
 * @date 2020-06-18 
 *
 */
public class DateUtil {


    public static String yyyy_MM_ddBHHMMSS() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern(tool.util.DateUtil.DATEFORMAT_STR_001));
    }

    /**
     * 获取当天日期 日期格式：'2011-12-03'.
     * @return
     */
    public static String yyyy_MM_dd() {
        return LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * 获取日期 日期格式：'2011-12-03'.
     * @return
     */
    public static String yyyy_MM_dd(LocalDate date) {
        return date.format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * 将字符串日期转换成LocalDate
     * @param date
     * @return
     */
    public static LocalDate yyyy_MM_dd(String date) {
        return LocalDate.parse(date, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * 获取当天日期 日期格式：'20111203'
     * @return
     */
    public static String yyyyMMdd() {
        return LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
    }


    /**
     * 获取前一天日期
     * @return 日期格式：'2011-12-03'
     */
    public static String yedyyyy_MM_dd() {
        return LocalDate.now().minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * 获取前一天日期
     * @return 日期格式：'2011-12-03'
     */
    public static String yedyyyy_MM_dd(LocalDate date) {
        return date.minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
    }


    /**
     * 获取前一天日期
     * @return 日期格式：'2011-12-03'
     */
    public static String yedyyyy_MM_dd(String date) {
        return yyyy_MM_dd(date).minusDays(1).format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * 获取前N天日期
     * @param date
     * @param daysToSubtract
     * @return 日期格式：'2011-12-03'
     */
    public static String subyyyy_MM_dd(LocalDate date, long daysToSubtract) {
        return date.minusDays(daysToSubtract).format(DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * 字符串生成日期
     * @param date
     * @param pattern
     * @return
     */
    public static LocalDate parse(String date, String pattern) {
        return LocalDate.parse(date, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 判断是不是2011-12-03格式的日期字符串
     * @param date
     * @return
     */
    public static boolean isYyyy_MM_dd(String date) {
        return isPattern(date, DateTimeFormatter.ISO_LOCAL_DATE);
    }

    /**
     * 判断是不是pattern格式的日期字符串
     * @param pattern
     * @return
     */
    public static boolean isPattern(String date, String pattern) {
        return isPattern(date, DateTimeFormatter.ofPattern(pattern));
    }

    /**
     * 判断是不是formatter格式的日期字符串
     * @param date
     * @param formatter
     * @return
     */
    public static boolean isPattern(String date, DateTimeFormatter formatter) {
        LocalDate localDate = null;
        try {
            localDate = LocalDate.parse(date, formatter);
        } catch (Exception e) {
            throw new RuntimeException(String.format("非法的日期字符,不符合目标格式 %s", formatter.toString()));
        }
        return localDate != null;
    }


    /**
     * 字符串转日期
     * @param day 日期
     * @param format 格式
     * @return
     */
    public static Date strToDate(String day, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        Date date = null;
        try {
            date = sdf.parse(day);
        } catch (ParseException e) {
            return null;
        }
        return date;
    }

    /**
     * 日期转字符串
     * @param time 日期字符串
     * @param format 格式
     * @return
     */
    public static String dateToStr(Date time, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return sdf.format(time);
    }

    /** The ISO-8601 definition, where a week starts on Monday and the first week
     * 根据日期字符串判断当年第几周
     * @param str
     * @return
     * @throws Exception
     */
    public static int getWeekIndexInYear(String str, String pattern) {
        return getWeekIndexInYear(parse(str, pattern));
    }

    public static int getWeekIndexInYear(LocalDate date) {
        return date.get(WeekFields.ISO.weekOfYear());
    }

    /** The ISO-8601 definition, where a week starts on Monday and the first week
     * 根据日期字符串判断是一周的第几天
     * @param str
     * @return
     * @throws Exception
     */
    public static int getDayIndexInWeek(String str, String pattern) {
        return getDayIndexInWeek(parse(str, pattern));
    }

    public static int getDayIndexInWeek(LocalDate date) {
        return date.get(WeekFields.ISO.dayOfWeek());
    }

    /**
     * 取得指定日期所在周的第一天
     *
     * @param date
     * @return
     */
    public static Date getFirstDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek()); // Monday
        return c.getTime();
    }

    /**
     * 取得指定日期所在周的最后一天
     *
     * @param date
     * @return
     */
    public static Date getLastDayOfWeek(Date date) {
        Calendar c = new GregorianCalendar();
        c.setFirstDayOfWeek(Calendar.MONDAY);
        c.setTime(date);
        c.set(Calendar.DAY_OF_WEEK, c.getFirstDayOfWeek() + 6); // Sunday
        return c.getTime();
    }

}
