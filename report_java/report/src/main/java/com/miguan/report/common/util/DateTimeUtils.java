package com.miguan.report.common.util;

public class DateTimeUtils {

    public static String secondsToTimeString(Integer seconds){
        int second = seconds % 60;
        int minute = (seconds / 60) % 60;
        int hour = (seconds / 3600);
        return hour + ":" + minute + ":" + second;
    }
}
