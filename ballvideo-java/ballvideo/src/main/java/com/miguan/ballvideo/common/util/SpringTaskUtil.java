package com.miguan.ballvideo.common.util;

import lombok.extern.slf4j.Slf4j;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @Author shixh
 * @Date 2019/9/19
 **/
@Slf4j
public class SpringTaskUtil {


    /**
     * 根据分钟转对应的Cron
     * {秒}：取值范围(0-59)
     * {分}：取值范围(0-59)
     * {时}：取值范围(0-23)
     * {日}：取值范围(1-31)
     * {月}：取值范围(1-12或JAN-DEC)
     * {年}：取值范围(1970-2099)
     *
     * @param minute
     * @return
     */
    public static String getCronExpression(long minute) {
        if (0 < minute && minute < 60) {
            return "0 0/" + minute + " * * * ?";
        } else if (60 <= minute && minute < (60 * 24)) {
            long h = minute / 60;
            return " 0 * " + h + " * * ?";
        } else if (60 * 24 <= minute && minute < (60 * 24 * 31)) {
            long d = minute / (60 * 24);
            return " 0 * * " + d + " * ?";
        } else if ((60 * 24 * 31) <= minute && minute < (60 * 24 * 31 * 12)) {
            long m = minute / (60 * 24 * 31);
            return " 0 * * * " + m + " ?";
        } else {
            minute = minute - (60 * 24 * 31 * 12);
            return getCronExpression(minute);
        }
    }


    /**
     * 根据时间获取cron表达式
     *
     * @param dateStr
     * @return
     * @Author xy.chen
     */
    public static String getCron(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String formatTimeStr = null;
        try {
            Date parse = formatter.parse(dateStr);
            SimpleDateFormat sdf = new SimpleDateFormat("ss mm HH dd MM ?");
            if (parse != null) {
                formatTimeStr = sdf.format(parse);
            }
        } catch (ParseException e) {
            log.error("时间转换异常,[{}]", e.getMessage());
        }
        return formatTimeStr;
    }

    /**
     * 根据时间获取cron表达式 HH:mm:ss
     *
     * @param dateStr
     * @return
     * @Author xy.chen
     */
    public static String getTimeCron(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
        String formatTimeStr = null;
        try {
            Date parse = formatter.parse(dateStr);
            SimpleDateFormat sdf = new SimpleDateFormat("ss mm HH * * ?");
            if (parse != null) {
                formatTimeStr = sdf.format(parse);
            }
        } catch (ParseException e) {
            log.error("时间转换异常,[{}]", e.getMessage());
        }
        return formatTimeStr;
    }

    public static Long getMillisecond(String dateStr) {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        long time = 0L;
        try {
            Date parse = formatter.parse(dateStr);
            time = parse.getTime();
        } catch (ParseException e) {
            log.error("时间转换异常,[{}]", e.getMessage());
        }
        return time;
    }
}
