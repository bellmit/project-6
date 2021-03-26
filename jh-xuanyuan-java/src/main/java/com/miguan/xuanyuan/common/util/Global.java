package com.miguan.xuanyuan.common.util;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.cgcg.context.SpringContextHolder;
import com.cgcg.context.util.StringUtils;
import com.miguan.xuanyuan.common.constant.RedisKeyConstant;
import com.miguan.xuanyuan.service.common.RedisService;

/**
 * @Description 根据key获取参数配置表（参数配置表）的value值
 * @Author zhangbinglin
 * @Date 2021/3/8 15:31
 **/
public class Global {

    private static RedisService redisService = SpringContextHolder.getBean("redisService");

    public static String getValue(String key) {
        return redisService.get(RedisKeyConstant.CONFIG_CODE + key);
    }

    public static Integer getInt(String key) {
        String value = getValue(key);
        return StringUtils.isBlank(value) ? null : Integer.parseInt(value);
    }

    public static Double getDouble(String key) {
        String value = getValue(key);
        return StringUtils.isBlank(value) ? null : Double.parseDouble(value);
    }

    public static Long getLong(String key) {
        String value = getValue(key);
        return StringUtils.isBlank(value) ? null : Long.parseLong(value);
    }

    public static JSONObject getJsonObject(String key) {
        try {
            String value = getValue(key);
            return JSONObject.parseObject(value);
        } catch (Exception e) {
            return new JSONObject();
        }
    }

    public static JSONArray getJsonArray(String key) {
        try {
            String value = getValue(key);
            return JSONArray.parseArray(value);
        } catch (Exception e) {
            return new JSONArray();
        }
    }
}
