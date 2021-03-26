package com.miguan.recommend.common.constants;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class ExistConstants {

    public final static int one_minutes_seconds = 60;
    public final static int one_and_a_half_minutes_seconds = 90;
    public final static int five_minutes_seconds = 300;
    public final static int ten_minutes_seconds = 600;
    public final static int thirty_minutes_seconds = 1800;

    public final static int one_hour_seconds = 3600;
    public final static int four_hour_seconds = 14400;

    public final static int one_day_seconds = 86400;
    public final static int two_days_seconds = 172800;
    public final static int three_days_seconds = 259200;
    public final static int thirty_days_seconds = 2592000;

    /**
     * 获取具体明天0点整，剩余的秒数
     * @return
     */
    public static int getTheRemainingSecondsOfToday(){
        LocalDateTime midnight = LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0).withNano(0);
        long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), midnight);
        return (int) seconds;
    }

    /**
     * 获取到明天hour点整，剩余的秒数
     * @return
     */
    public static int getTheRemainingSecondsOfTheHourForTomorrow(int hour){
        LocalDateTime midnight = LocalDateTime.now().plusDays(1).withHour(hour).withMinute(0).withSecond(0).withNano(0);
        long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), midnight);
        return (int) seconds;
    }

    /**
     * 获取具体明天3点整，剩余的秒数
     * @return
     */
    public static int getTheRemainingSecondsOfTheThreeHourForTomorrow(){
        LocalDateTime midnight = LocalDateTime.now().plusDays(1).withHour(3).withMinute(0).withSecond(0).withNano(0);
        long seconds = ChronoUnit.SECONDS.between(LocalDateTime.now(), midnight);
        return (int) seconds;
    }

}
