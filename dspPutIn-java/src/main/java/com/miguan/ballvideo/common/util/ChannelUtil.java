package com.miguan.ballvideo.common.util;

import com.cgcg.context.SpringContextHolder;
import com.miguan.ballvideo.entity.common.Channel;
import com.miguan.ballvideo.service.RedisService;
import org.apache.commons.lang3.StringUtils;

/**
 * 渠道工具类
 * @Author shixh
 * @Date 2019/9/27
 **/

public class ChannelUtil {

    /**
     * 从Redis获取渠道
     *
     * @param channelId
     * @return
     */
    public static String filterByRedis(String channelId) {
        RedisService redisService = SpringContextHolder.getBean("redisService");
        String value = redisService.hget(Channel.CHANNEL_REDIS, channelId);
        if (StringUtil.isEmpty(value)) {
            return "xysp_guanwang";
        }
        return value;
    }

    /**
     * 根据channelId返回对应渠道
     * @Author shixh
     * @param channelId
     * @return
     */
    public static String filter(String channelId) {
        //ios直接返回xysp_guanwang渠道
        if ("xysp_guanwang".equals(channelId)){
            return channelId;
        }
        //安卓根据开关是否走默认渠道
        /*String channelSwitch = Global.getValue("adv_channel_switch");
        if ("1".equals(channelSwitch)){
            return Global.getValue("adv_default_channel");
        }*/
        if (StringUtils.isBlank(channelId)) {
            return "xysp_guanwang";
        }
        //通过redis获取渠道
        return filterByRedis(channelId);
    }

    /**
     * 根据手机类型，channelId返回对应渠道
     * @param channelId
     * @param mobileType 1-ios，2：安卓
     * @return
     */
    public static String filter(String channelId, String mobileType) {
        //ios直接返回xysp_guanwang渠道
        if ("xysp_guanwang".equals(channelId)){
            return channelId;
        }
        //安卓根据开关是否走默认渠道
        /*String channelSwitch = Global.getValue("adv_channel_switch");
        if ("1".equals(channelSwitch)){
            return Global.getValue("adv_default_channel");
        }*/
        if (StringUtils.isBlank(channelId)) {
            if ("1".equals(mobileType)) return "xysp_guanwang";
            return "xysp_guanwang_ad";
        } else {
            return filter(channelId);
        }
    }

    /**
     * 不需要经过开关配置的渠道过滤
     *
     * @param channelId
     * @return
     */
    public static String filterChannelId(String channelId) {
        if (StringUtils.isBlank(channelId)) {
            return "xysp_guanwang";
        }
        return filterByRedis(channelId);
    }

    /**
     * 当渠道为空的时候，根据手机类型返回默认渠道，否则直接返回渠道
     * @param channelId
     * @param mobileType 1-ios，2：安卓
     * @return
     */
    public static String getChannelId(String channelId, String mobileType) {
        if (StringUtils.isBlank(channelId)) {
            if ("1".equals(mobileType)) return "xysp_guanwang";
            return "xysp_guanwang_ad";
        }
        return channelId;
    }

}
