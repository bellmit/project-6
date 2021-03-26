package com.miguan.recommend.common.util;

/**
 * @author zhongli
 * @date 2020-07-27 
 *
 */
public class IPUtil {
    /**
     * string ip to long ip
     *
     * @param    ip
     * @return long
     */
    public static long ip2long(String ip) {
        String[] p = ip.split("\\.");
        if (p.length != 4) {
            return 0;
        }
        int p1 = ((Integer.valueOf(p[0]) << 24) & 0xFF000000);
        int p2 = ((Integer.valueOf(p[1]) << 16) & 0x00FF0000);
        int p3 = ((Integer.valueOf(p[2]) << 8) & 0x0000FF00);
        int p4 = ((Integer.valueOf(p[3]) << 0) & 0x000000FF);

        return ((p1 | p2 | p3 | p4) & 0xFFFFFFFFL);
    }

    /**
     * int to ip string
     *
     * @param    ip
     * @return string
     */
    public static String long2ip(long ip) {
        StringBuilder sb = new StringBuilder();

        sb
                .append((ip >> 24) & 0xFF).append('.')
                .append((ip >> 16) & 0xFF).append('.')
                .append((ip >> 8) & 0xFF).append('.')
                .append((ip >> 0) & 0xFF);

        return sb.toString();
    }

    /**
     * check the validate of the specifeld ip address
     *
     * @param    ip
     * @return boolean
     */
    public static boolean isIpAddress(String ip) {
        String[] p = ip.split("\\.");
        if (p.length != 4) {
            return false;
        }
        for (String pp : p) {
            if (pp.length() > 3) return false;
            int val = Integer.valueOf(pp);
            if (val > 255) return false;
        }

        return true;
    }
}
