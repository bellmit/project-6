package com.miguan.ballvideo.common.util;

import java.util.BitSet;

/**
 * bit操作工具类哦
 * @author xujinbang
 * @date 2019/12/9.
 */
public class BitSetUtil {

    public static BitSet byteArray2BitSet(byte[] bytes) {
        BitSet bitSet = new BitSet(bytes.length * 8);
        int index = 0;
        for (int i = 0; i < bytes.length; i++) {
            for (int j = 7; j >= 0; j--) {
                bitSet.set(index++, (bytes[i] & (1 << j)) >> j == 1 ? true : false);
            }
        }
        return bitSet;
    }
}
