package com.miguan.laidian.common.util;

import lombok.extern.slf4j.Slf4j;
import tool.util.NumberUtil;
import tool.util.StringUtil;

import java.util.Map;

/**
 * 启动加载缓存类
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-06-20 10:48:24
 */
@Slf4j
public class Global {

    public static Map<String, Object> configMap;

    public static Map<String, Object> msg_template_Map;

    public static int getInt(String key, String appType) {
        key = key + "_" + appType;
        return NumberUtil.getInt(StringUtil.isNull(configMap.get(key)));
    }

    public static double getDouble(String key, String appType) {
        key = key + "_" + appType;
        return NumberUtil.getDouble(StringUtil.isNull(configMap.get(key)));
    }

    public static String getValue(String key, String appType) {
        key = key + "_" + appType;
        return StringUtil.isNull(configMap.get(key));
    }

    public static Object getObject(String key, String appType) {
        key = key + "_" + appType;
        return configMap.get(key);
    }

    public static String getMsgTempLate(String key, String appType) {
        key = key + "_" + appType;
        return StringUtil.isNull(msg_template_Map.get(key));
    }

}
