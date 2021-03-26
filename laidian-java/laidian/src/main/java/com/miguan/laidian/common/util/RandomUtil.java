package com.miguan.laidian.common.util;

import java.util.Random;

/**
 * @author chenwf
 * @date 2020/5/22
 */
public class RandomUtil extends tool.util.RandomUtil {
    public static String getRandomNum(int length) {
        String base = "0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < length; ++i) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }

        return sb.toString();
    }
}
