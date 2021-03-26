package com.miguan.ballvideo.common.util;

import com.cgcg.context.SpringContextHolder;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.RedisDB2Service;

/**
 * 应用类别工具类
 * @Author laiyd
 * @Date 2020/7/9
 **/

public class AppPackageUtil {

    /**
     * 从Redis获取应用类别
     *
     * @param appPackage
     * @return
     */
    public static String getAppType(String appPackage) {
        RedisDB2Service redisDB2Service = SpringContextHolder.getBean("redisDB2Service");
        String key = RedisKeyConstant.APP_TYPE + appPackage;
        return redisDB2Service.get(key);
    }
}
