    package com.miguan.idmapping.common.utils;

    import com.cgcg.context.SpringContextHolder;
    import com.miguan.idmapping.common.constants.RedisKeyConstant;
    import com.miguan.idmapping.service.RedisDB2Service;

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
