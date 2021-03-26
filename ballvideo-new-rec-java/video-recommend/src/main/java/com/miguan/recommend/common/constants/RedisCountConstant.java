package com.miguan.recommend.common.constants;

public class RedisCountConstant {

    public final static String bloom_c = "bgsUpBloomCA";
    public final static String bloom_p = "bgsUpBloomP1";

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
    public final static String count_transfer = "transfer";

    /**
     * 前缀
     */
    public static final String prefix = "bg_";

    /**
     * 用户兴趣分类池
     */
    public final static String USER_LIKE_CAT_POOL = prefix + "sUp";
    /**
     * 用户兴趣分类池（转化）
     */
    public final static String USER_LIKE_CAT_POOL_T = prefix + "sUpT";
    /**
     * 用户感兴趣的场景池
     */
    public static final String user_like_scene_pool = prefix + "scenePool";
    /**
     * 用户感兴趣的场景池(转化)
     */
    public static final String user_like_scene_pool_t = prefix + "scenePoolT";

    /**
     * 用户感兴趣的标签池
     */
    public static final String user_like_tag_pool = prefix + "tagPool:%s:%s";

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
    /**
     * 用户分类评分
     * %s -- 日
     * %s -- 用户ID
     */
    public static final String user_cat_score = "bg_use_" + "cat_score:%s:%s";

    //用户最感兴趣的视频分类
    public static final String user_top_type = "top_type:";

    /**
     * 用户向量（uuid）
     */
    public final static String user_embedding = prefix + "user_embedding_2:%s";
}
