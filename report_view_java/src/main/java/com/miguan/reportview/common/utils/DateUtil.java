package com.miguan.reportview.common.utils;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.WeekFields;
import java.util.*;

/**日期工具类
 * @author zhongli
 * @date 2020-06-18 
 *
 */
public class DateUtil {

    public static final DateTimeFormatter YYYYMMDD_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final DateTimeFormatter YYYYMMDDHH_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMddHH");
    public static final DateTimeFormatter YYYY_MM_DDTHH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH");
    public static final DateTimeFormatter YYYY_MM_DDBHHMMSS_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");


    public static String currentTime(){
        return LocalDateTime.now().format(YYYY_MM_DDBHHMMSS_FORMATTER);
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
     * 将字符串日期转换成LocalDate
     * @param date
     * @return
     */
    public static String yyyy_MM_dd4yyyyMMdd(String date) {
        return date.substring(0, 4).concat("-").concat(date.substring(4, 6)).concat("-").concat(date.substring(6, 8));
    }

    /**
     * 获取当天日期 日期格式：'20111203'
     * @return
     */
    public static String yyyyMMdd() {
        return LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE);
    }
    /**
     * 获取当天日期 日期格式：'20111203'
     * @return
     */
    public static String yesyyyyMMdd() {
        return LocalDate.now().minusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE);
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

    public static LocalDate parse(String date, DateTimeFormatter pattern) {
        return LocalDate.parse(date, pattern);
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

    public static int nowInt() {
        String date = LocalDate.now().format(YYYYMMDD_FORMATTER);
        return Integer.parseInt(date);
    }
    public static int yesInt() {
        String date = LocalDate.now().minusDays(1).format(YYYYMMDD_FORMATTER);
        return Integer.parseInt(date);
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



    /**
     * 获取特定时间前后
     * @param specifiedDay
     * @param dayNum
     * @return
     */
    public static String getSpecifiedDay(String specifiedDay, int dayNum) {
        SimpleDateFormat ymd = new SimpleDateFormat("yyyyMMdd");

        Calendar c = Calendar.getInstance();
        Date date = null;
        try {
            date = ymd.parse(specifiedDay);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        c.setTime(date);
        int day = c.get(Calendar.DATE);
        c.set(Calendar.DATE, day + dayNum);

        String day1 = ymd.format(c.getTime());
        return day1;
    }

    public static List<String> getDateDetailList(String fromDate, String toDate)  {
        SimpleDateFormat ymd = new SimpleDateFormat("yyyy-MM-dd");

        Calendar cStart = Calendar.getInstance();

        try {
            List<String> dateList = new LinkedList<String>();
            cStart.setTime(ymd.parse(fromDate));
            int w = cStart.get(Calendar.DAY_OF_WEEK) - 1;
            if (w < 0)
                w = 0;
            // 别忘了，把起始日期加上

            dateList.add(ymd.format(ymd.parse(fromDate)));
            cStart.setTime(ymd.parse(fromDate));

            // 此日期是否在指定日期之后
            while (ymd.parse(toDate).after(cStart.getTime())) {
                // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
                cStart.add(Calendar.DAY_OF_MONTH, 1);
                Date date = cStart.getTime();
                cStart.setTime(date);
                w = cStart.get(Calendar.DAY_OF_WEEK) - 1;
                if (w < 0)
                    w = 0;
                dateList.add(ymd.format(date) );
            }
            return dateList;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static void main(String arg[]) throws Exception {
//        System.out.println(getDayIndexInWeek("2020-06-13", "yyyy-MM-dd"));
//        System.out.println(getDayIndexInWeek("2020-01-01", "yyyy-MM-dd"));
//
//        System.out.println(getFirstDayOfWeek(new Date()));
//        System.out.println(getLastDayOfWeek(new Date()));
//        System.out.println(getLastDayOfWeek(strToDate("2020-06-14", "yyyy-MM-dd")));
//        System.out.println(getLastDayOfWeek(strToDate("2020-06-8", "yyyy-MM-dd")));
       /* System.out.println(getDateDetailList("2020-11-05","2020-11-05"));
        for(String date:getDateDetailList("2020-11-05","2020-11-05")) {
            String last1Day=DateUtil.getSpecifiedDay(date.replace("-",""), -1);
            String last30Day=DateUtil.getSpecifiedDay(date.replace("-",""), -30);
            System.out.println(last1Day);
        }*/
    }

}
