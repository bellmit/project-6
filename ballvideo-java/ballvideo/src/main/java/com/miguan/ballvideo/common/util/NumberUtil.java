package com.miguan.ballvideo.common.util;

import java.math.BigDecimal;

/**
 * @Description 数值工具类
 * @Author zhangbinglin
 * @Date 2020/8/4 16:46
 **/
public class NumberUtil {

    /**
     * 如果数字是整数，把小数点和小数点后面的0去掉
     *
     * @param obj
     * @return
     */
    public static String numberFormat(Object obj) {
        if (obj == null) {
            return "0";
        }
        return new BigDecimal(String.valueOf(obj)).stripTrailingZeros().toPlainString();
    }

    /**
     * 把数字转出字符串，并且小数点保留2位
     *
     * @param number
     * @return
     */
    public static String decimalPoint(Double number) {
        if(number == null) {
            return "";
        }
        return new BigDecimal(number).setScale(2, BigDecimal.ROUND_FLOOR).toString();
    }
}
