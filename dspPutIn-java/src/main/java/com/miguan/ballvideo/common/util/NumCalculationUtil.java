package com.miguan.ballvideo.common.util;

import java.math.BigDecimal;

/**计算工具
 * @author zhongli
 * @date 2020-06-18 
 *
 */
public class NumCalculationUtil {

    public final static Double zero = Double.valueOf(0);

    /**
     * 四舍五入
     * @param num
     * @return
     */
    public static String roundHalfUp(BigDecimal num) {
        return num.setScale(2, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
    }

    /**
     * 四舍五入
     * @param scale 小数位数
     * @param num
     * @return
     */
    public static String roundHalfUp(int scale, BigDecimal num) {
        return num.setScale(scale, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
    }

    /**
     * 四舍五入
     * @param num
     * @return
     */
    public static double roundHalfUpDouble(BigDecimal num) {
        return num.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 四舍五入
     * @param num
     * @return
     */
    public static double roundHalfUpDoubleFO(Double num) {
        if(num == null){
            return Double.NaN;
        }
        return roundHalfUpDouble(num);
    }
    /**
     * 四舍五入
     * @param num
     * @return
     */
    public static double roundHalfUpDouble(double num) {
        if (Double.isInfinite(num)) {
            return 0;
        }
        if (Double.isNaN(num)) {
            return 0;
        }
        return BigDecimal.valueOf(num).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 百分比四舍五入
     * @param num
     * @return
     */
    public static double percentageFO(Double num) {
        if (num == null) {
            return Double.NaN;
        }
        return percentage(num.doubleValue());
    }

    /**
     * 百分比四舍五入
     * @param num
     * @return
     */
    public static double percentage(double num) {
        if (Double.isInfinite(num)) {
            return 0;
        }
        if (Double.isNaN(num)) {
            return 0;
        }
        num = num * 100;
        return BigDecimal.valueOf(num).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 四舍五入
     * @param scale 保留小数位数
     * @param num
     * @return
     */
    public static double roundHalfUpDouble(int scale, double num) {
        if (Double.isInfinite(num)) {
            return 0;
        }
        if (Double.isNaN(num)) {
            return 0;
        }
        return BigDecimal.valueOf(num).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 四舍五入
     * @param scale 保留小数位数
     * @param numObj
     * @return
     */
    public static double roundHalfUpDouble(int scale, Integer numObj) {
        if (numObj == null) {
            return 0;
        }
        return roundHalfUpDouble(numObj.doubleValue());
    }

    /**
     * 把数字转出字符串，并且小数点保留2位
     *
     * @param number
     * @return
     */
    public static String decimalPoint(Double number) {
        if (number == null) {
            return "";
        }
        if (number.isInfinite()) {
            return "";
        }
        if (number.isNaN()) {
            return "";
        }
        return new BigDecimal(number).setScale(2, BigDecimal.ROUND_FLOOR).toString();
    }

    public static double divide(int num, int pNum, boolean isRate) {
        if (num == 0) {
            return 0;
        } else if (pNum == 0) {
            return Double.NaN;
        }
        num = isRate ? num * 100 : num;
        return new BigDecimal(num)
                .divide(new BigDecimal(pNum), isRate ? 2 : 4, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double divide(long num, long pNum, boolean isRate) {
        if (num == 0) {
            return 0;
        } else if (pNum == 0) {
            return Double.NaN;
        }
        num = isRate ? num * 100 : num;
        return new BigDecimal(num)
                .divide(new BigDecimal(pNum), isRate ? 2 : 4, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    public static double divide(double num, double pNum, boolean isRate) {
        if (Double.isNaN(num)) {
            return Double.NaN;
        }
        if (Double.isNaN(pNum)) {
            return Double.NaN;
        }
        if (num == 0) {
            return 0;
        } else if (pNum == 0) {
            return Double.NaN;
        }
        num = isRate ? num * 100 : num;
        return new BigDecimal(num)
                .divide(new BigDecimal(pNum), 2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }
}
