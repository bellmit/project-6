package com.miguan.xuanyuan.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import tool.util.DateUtil;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.Date;

@Slf4j
public class DateUtils extends DateUtil {


    /**
     * 获取日期的时间戳
     *
     * @param dateStr
     * @return
     * @throws ParseException
     */
    public static long getDateStrTimestamp(String dateStr) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date = df.parse(dateStr);
        return date.getTime();
    }

    /**
     * 获取当前日期
     *
     * @return
     */
    public static String getCurrentDateTimeStr() {
        DateTimeFormatter fmDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime  now = LocalDateTime .now();
        return now.format(fmDate);
    }

    /**
     * 获取当前时间戳
     * @return
     */
    public static Long getCurrentTimestamp() {
        LocalDateTime  localDate = LocalDateTime .now();
        Timestamp timestamp= Timestamp.valueOf(LocalDateTime.now());
        return timestamp.getTime();
    }

    /**
     * 获取距离今天相差的天数
     * @param dayNum
     * @return
     */
    public static String getDateStr(long dayNum) {
        DateTimeFormatter fmDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime  now = LocalDateTime .now();
        LocalDateTime date = now.plusDays(dayNum);
        return date.format(fmDate);
    }

    /**
     * 获取距离今天相差的天数
     * @param dayNum
     * @return
     */
    public static LocalDateTime getDate(long dayNum) {
        DateTimeFormatter fmDate = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime  now = LocalDateTime .now();
        LocalDateTime date = now.plusDays(dayNum);
        return date;
    }

    public static Date strToDate(String dateStr) throws ParseException {
        String pattern = "yyyy-MM-dd HH:mm:ss";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
        Date date = simpleDateFormat.parse(dateStr);
        return date;
    }

    /**
     * 时间戳转为日期格式
     *
     * @param timestamp
     * @return
     */
    public static String getDataStrFromTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdf.format(new Date(timestamp));
        return dateStr;
    }



    /**
     * 时间戳转为日期格式
     *
     * @param sourceTimeStamp
     * @param delayTime 毫秒为单位
     * @return true 表示延迟  false表示无延迟
     */
    public static boolean delayTime(String sourceTimeStamp,long delayTime) {
        if(StringUtils.isEmpty(sourceTimeStamp)){
            return true;
        }
        long source = Long.parseLong(sourceTimeStamp);
        long current = System.currentTimeMillis();
        if(source + delayTime < current){
            return true;
        } else {
            return false;
        }
    }

    public static String dayForWeek(Date now) {
        try {
            Calendar cal = Calendar.getInstance();
            String[] weekDays = { "7", "1", "2", "3", "4", "5", "6" };
            cal.setTime(now);
            int w = cal.get(Calendar.DAY_OF_WEEK) - 1; // 指示一个星期中的某天。
            if (w < 0)
                w = 0;
            return weekDays[w];
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}