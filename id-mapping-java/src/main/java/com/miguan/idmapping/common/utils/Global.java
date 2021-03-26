package com.miguan.idmapping.common.utils;


import tool.util.NumberUtil;
import tool.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 启动加载缓存类
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-08-08
 */
public class Global {

    public static volatile Map<String, Object> configMap;

    public static int getInt(String key) {
        return NumberUtil.getInt(StringUtil.isNull(configMap.get(key)));
    }

    public static double getDouble(String key) {
        return NumberUtil.getDouble(StringUtil.isNull(configMap.get(key)));
    }

    public static String getValue(String key) {
        return StringUtil.isNull(configMap.get(key));
    }

    public static Object getObject(String key) {
        return configMap.get(key);
    }

    public static void putConfigMapAll(Map<String, Object> configs) {
        if (configMap == null) {
            configMap = new HashMap<>();
        }
        configMap.putAll(configs);
    }

    //清理配置缓存
    public static void clearConfigMap() {
        configMap = null;
    }
}
