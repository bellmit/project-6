package com.miguan.recommend.common.constants;

public class RedisRecommendConstants {

    /**************************************************** 茜柚推荐key 开始****************************************************/
    public final static String prefix = "bg_rec_";
    public final static String all_catids = prefix + "all_catids:";
    /**
     * 视频标题
      */
    public final static String video_title = prefix + "video_title:";
    /**
     * 视频百度飞桨置信度前5标签ID，包含指定3个标签的相关视频
      */
    public final static String video_embedding_vector = prefix + "video_embedding_vector:";
    /**
     * 视频百度飞桨置信度前5标签ID，包含指定3个标签的相关视频
      */
    public final static String relevant_video_of_tag3_ids = prefix + "relevant_video_of_tag3_ids:";
    /**
     * 视频百度飞桨置信度前3标签ID
     */
    public final static String video_paddle_tag3_ids = prefix + "video_paddle_tag3_ids:";
    /**
     * 预估服务返回得视频播放率
     * %s -- uuid
     */
    public static final String user_flush_num = prefix + "u_flush_num:%s";
    /**
     * 预估服务返回得视频播放率
     * %s -- uuid
     */
    public static final String key_user_rec_video_list = prefix + "u_rec_li:%s";
    /**
     * 预估服务返回得视频播放率
     * %s -- uuid
     */
    public static final String key_user_rec_video_list_old = prefix + "u_rec_li:old:%s";
    /**
     * 推荐接口请求快照
     * %s -- UUID
     * %s -- 视频ID
     */
    public final static String video_snapshoot = prefix + "snapshoot:%s:%s";

    /**
     * 用户曝光过的激励视频
     * %s -- UUID
     */
    public final static String user_incentive_video_log = prefix + "user_jl_v_l:%s";

    /**
     * 相似场景的视频
     * %s -- 视频ID
     */
    public final static String video_scenario_similar = prefix + "video_scenario_similar:%s";

    /**
     * 标签首刷视频
     * %s -- 分类ID
     */
    public final static String first_flush_video = prefix + "first_flush_video:%s";

    /**
     * 用户是否已首刷标识
     * %s -- uuid
     */
    public final static String user_first_flush = prefix + "user_first_flush:%s";

    /**
     * 用户是否已首刷标识
     * %s -- uuid
     */
    public final static String user_cat_rec_flush = prefix + "user_cat_rec_flush:%s";
    /**
     * 昨日热门视频
     */
    public final static String yesterday_top_video = prefix + "yesterDayTopVideo:";
    /**************************************************** 茜柚推荐key 开始****************************************************/
}
