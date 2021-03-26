package com.miguan.xuanyuan.common.util;

import org.springframework.util.DigestUtils;

import java.util.Map;
import java.util.UUID;

public class SignUtil {

    private static final String TIMESTAMP = "timestamp";
    private static final String NONCE = "nonce";
    private static final String SIGN = "sign";

    public static void createSignParam(Map<String,Object> map){
        long timestamp = System.currentTimeMillis();
        String nonce = UUID.randomUUID().toString().trim().toLowerCase().replaceAll("-","");
        map.put(TIMESTAMP,timestamp);
        map.put(NONCE,nonce);
        String paramStr = getOrderParam(map);
        String sign = DigestUtils.md5DigestAsHex(paramStr.getBytes());
        map.put(SIGN,sign);
    }

    //获取排序好的参数
    private static String getOrderParam(Map<String,Object> parameterMap) {
        //获取所有的请求参数
        Map<String, Object> map = MapUtil.sortByKey(parameterMap);
        StringBuilder sb = new StringBuilder();
        boolean first = true;
        for (Map.Entry<String, Object> entry:map.entrySet()) {
            if(first){
                sb.append(entry.getKey()+"="+entry.getValue());
                first = false;
            } else {
                sb.append("&" + entry.getKey()+"="+entry.getValue());
            }
        }
        return sb.toString();
    }

}
