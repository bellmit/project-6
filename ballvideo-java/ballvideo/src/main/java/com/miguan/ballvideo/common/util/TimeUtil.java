package com.miguan.ballvideo.common.util;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;

@Slf4j
public class TimeUtil {

    /**
     * 将mm:ss字符串转换成秒
     * @param minute
     * @return
     */
    public static Integer changeMinuteStringToSecond(String minute){
        if(StringUtils.isBlank(minute)){
            return 0;
        }
        String[] minuteArray = minute.split(":");
        if(minuteArray.length == 1){
            return Integer.parseInt(minuteArray[0]);
        } else {
            int second = 0;
            for(int i = 0; i < minuteArray.length; i++){
                second += new BigDecimal(minuteArray[i]).multiply(new BigDecimal(60).pow(minuteArray.length - i-1)).intValue();
            }
            return second;
        }
    }
}
