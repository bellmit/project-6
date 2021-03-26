package com.miguan.report.common.util;

import java.math.BigDecimal;
import java.util.regex.Pattern;

/**计算工具
 * @author zhongli
 * @date 2020-06-18 
 *
 */
public class NumCalculationUtil {

    /**
     * 环比或同比计算
     * @param num
     * @param pNum
     * @return
     */
    public static String calMomOrYoy(BigDecimal num, BigDecimal pNum) {
        num = num == null ? BigDecimal.ZERO : num;
        pNum = pNum == null ? BigDecimal.ZERO : pNum;
        if (num.compareTo(BigDecimal.ZERO) == 0 && pNum.compareTo(BigDecimal.ZERO) == 0) {
            return "0";
        } else if (num.compareTo(BigDecimal.ZERO) == 0) {
            return "-1";
        } else if (pNum.compareTo(BigDecimal.ZERO) == 0) {
            return "1";
        }
        return num.subtract(pNum)
                .divide(pNum, 4, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
    }

    /**
     * 环比或同比计算
     * @param num
     * @param pNum
     * @return
     */
    public static double calMomOrYoyDouble(BigDecimal num, BigDecimal pNum) {
        num = num == null ? BigDecimal.ZERO : num;
        pNum = pNum == null ? BigDecimal.ZERO : pNum;
        if (num.compareTo(BigDecimal.ZERO) == 0 && pNum.compareTo(BigDecimal.ZERO) == 0) {
            return 0;
        } else if (num.compareTo(BigDecimal.ZERO) == 0) {
            return -1;
        } else if (pNum.compareTo(BigDecimal.ZERO) == 0) {
            return 1;
        }
        return num.subtract(pNum)
                .divide(pNum, 4, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 环比或同比计算
     * @param num 如果为0 则环比/同比为-100%
     * @param pNum 如果为0 则环比/同比为100%
     * @return
     */
    public static String calMomOrYoy(double num, double pNum) {
        if (num == 0 && pNum == 0) {
            return "0";
        } else if (num == 0) {
            return "-1";
        } else if (pNum == 0) {
            return "1";
        }
        BigDecimal pbd = BigDecimal.valueOf(pNum);
        return BigDecimal.valueOf(num).subtract(pbd)
                .divide(pbd, 4, BigDecimal.ROUND_HALF_UP).stripTrailingZeros().toPlainString();
    }

    /**
     * 环比或同比计算
     * @param num 如果为0 则环比/同比为-100%
     * @param pNum 如果为0 则环比/同比为100%
     * @return
     */
    public static double calMomOrYoyDouble(double num, double pNum) {
        if (num == 0 && pNum == 0) {
            return 0;
        } else if (num == 0) {
            return -1;
        } else if (pNum == 0) {
            return 1;
        }
        BigDecimal pbd = BigDecimal.valueOf(pNum);
        return BigDecimal.valueOf(num).subtract(pbd)
                .divide(pbd, 4, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

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
    public static double roundHalfUpDouble(double num) {
        if (Double.isInfinite(num)) {
            return 0;
        }
        if(Double.isNaN(num)){
            return 0;
        }
        return BigDecimal.valueOf(num).setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
    }

    /**
     * 四舍五入
     * @param num 保留小数位数
     * @return
     */
    public static double roundHalfUpDouble(int scale, double num) {
        if (Double.isInfinite(num)) {
            return 0;
        }
        if(Double.isNaN(num)){
            return 0;
        }
        return BigDecimal.valueOf(num).setScale(scale, BigDecimal.ROUND_HALF_UP).doubleValue();
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
        if(number.isNaN()){
            return "";
        }
        return new BigDecimal(number).setScale(2, BigDecimal.ROUND_FLOOR).toString();
    }


    /**
     * 判断一个字符串是否为数字
     * @param str
     * @return
     */
    public static boolean isInteger(String str) {
        Pattern pattern = Pattern.compile("^[-\\+]?[\\d]*$");
        return pattern.matcher(str).matches();
    }
}
