package com.miguan.laidian.common.util;

import com.miguan.laidian.entity.Channel;
import com.miguan.laidian.redis.service.RedisService;
import org.apache.commons.lang3.StringUtils;

/**
 * 渠道工具类
 *
 * @Author hyl
 * @Date 2019年11月4日17:42:20
 **/

public class ChannelUtil {

    /**
     * 公共处理channel默认值
     *
     * @param channelId
     * @return
     * @Author shixh
     */
    public static String filter(String channelId) {
        //if channelId is null,set channelId = "xysp_guanwang";
        if (StringUtils.isBlank(channelId)) {
            return "xld_guanwang";
        } else {
            RedisService redisService = com.cgcg.context.SpringContextHolder.getBean("redisService");
            String value = redisService.hget(Channel.CHANNEL_REDIS, channelId);
            if (value == null) {
                return "xld_guanwang";
            }
            return value;
        }
    }

    /**
     * @param channelId
     * @param mobileType 1-ios，2：安卓
     * @return
     */
    public static String filter(String channelId, String mobileType) {
        //if channelId is null,set channelId = "xysp_guanwang";
        if (StringUtils.isBlank(channelId)) {
            if ("1".equals(mobileType)) return "xld_guanwang";
            return "xld_guanwang_ad";
        } else {
            return filter(channelId);
        }
    }
}
