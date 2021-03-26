package com.miguan.xuanyuan.common.util;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class RandomUtil {

    /**
     * 获取指定位数的随机数字串
     *
     * @param length
     * @return
     * @throws Exception
     */
    public static String getRandomNumber(int length) throws Exception {
        if (length < 0) {
            throw new Exception("length错误");
        }
        String[] numberArr = new String[] { "1","2", "3", "4", "5", "6", "7", "8", "9", "0" };

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int pos = (int)(Math.random() * 10);
            sb.append(numberArr[pos]);
        }
        return sb.toString();
    }
}
