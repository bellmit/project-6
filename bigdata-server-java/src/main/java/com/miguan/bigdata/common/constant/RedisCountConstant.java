package com.miguan.bigdata.common.constant;

public class RedisCountConstant {

    public final static String bloom_c = "bgsUpBloomCA";
    public final static String bloom_p = "bgsUpBloomP1";

    public final static String bloom_npush_c = "bgNpushBloomC";
    public final static String bloom_npush_p = "bgNpushBloomP";

    /**
     * Redis统计信息字段名称
     */
    public final static String count_day = "day";
    public final static String count_show = "show";
    public final static String count_play = "play";
    public final static String count_vaild_play = "vaildPlay";
    public final static String count_play_time_r = "playTimeR";
    public final static String count_active = "active";
    public final static String count_new_user = "newUser";

    /**
     * 前缀
     */
    public static final String prefix = "bg_";

    /**
     * 用户兴趣分类池
     */
    public final static String USER_LIKE_CAT_POOL = prefix + "sUp";
    /**
     * 用户感兴趣的场景池
     */
    public static final String user_like_scene_pool = prefix + "scenePool";
    /**
     * 用户最近观看过的3个有效视频
     */
    public static final String user_video_near_play = prefix + "nearPlay";
    /**
     * 视频点击率统计信息
     * %s -- 日
     * %s -- 类型 show / play
     * %s -- 视频ID
     */
    public static final String video_click_rate_count = prefix + "videoClickRC:%s:%s:%s";

    /**
     * 视频的统计信息
     * %s -- 日
     * %s -- 视频ID
     */
    public static final String video_count_detail = prefix + "videoCount:%s:%s";

    /**
     * 所有视频的播放总数
     * %s -- 日
     */
    public static final String all_video_play = prefix + "videoCount:%s:allVp";
    /**
     * 所有视频的有效播放总数
     * %s -- 日
     */
    public static final String all_video_vaild_play = prefix + "videoCount:%s:allVVp";
    /**
     * 所有视频的有效播放总时长
     */
    public static final String all_video_play_time_r = prefix + "videoCount:%s:allVptr";
    /**
     * 用户类别的统计信息
     * %s -- 日
     * %s -- 用户ID
     */
    public static final String user_cat_count = prefix + "catCount:%s:%s";
}
