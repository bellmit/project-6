package com.miguan.advert.config.redis.util;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;

/**
 * @Author shixh
 * @Date 2019/10/11
 **/
public class IPUtils {

//    public static String getPort() {
//        MBeanServer beanServer = ManagementFactory.getPlatformMBeanServer();
//        Set<ObjectName> objectNames = null;
//        try {
//            objectNames = beanServer.queryNames(new ObjectName("*:type=Connector,*"), Query.match(Query.attr("protocol"), Query.value("HTTP/1.1")));
//            String port = objectNames.iterator().next().getKeyProperty("port");
//            return port;
//        } catch (MalformedObjectNameException e) {
//            e.printStackTrace();
//        }
//        return "8080";
//    }
//
//    public static String getHost(){
//        try {
//            InetAddress address = InetAddress.getLocalHost();
//            String ip = address.getHostAddress();
//            return ip;
//        } catch (UnknownHostException e) {
//            e.printStackTrace();
//        }
//        return "127.0.0.1";
//    }

//    public static String getHostAndPort(){
//        return getHost()+":"+getPort();
//    }


    public static String getRequestIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (!checkIP(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (!checkIP(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (!checkIP(ip)) {
            ip = request.getRemoteAddr();
        }
        if (ip != null && ip.length() > 15) {
            if (ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
        }
        // 解决请求和响应的IP一致且通过浏览器请求时，request.getRemoteAddr()为"0:0:0:0:0:0:0:1"
        if("0:0:0:0:0:0:0:1".equals(ip)){
            ip = getHostAddress();
        }
        return ip;
    }
    public static String getHostAddress(){
        InetAddress inet = null;
        try {
            inet = InetAddress.getLocalHost();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return inet.getHostAddress();
    }

    private static boolean checkIP(String ip) {
        if (ip == null || ip.length() == 0 || "unkown".equalsIgnoreCase(ip)
                || ip.split(".").length != 4) {
            return false;
        }
        return true;
    }
}
