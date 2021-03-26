package com.miguan.recommend.common.constants;

public class MongoConstants {

    public final static String cat_hotspot = "cat_hotspot";
    public final static String cat_hotspot_old = "cat_hotspot_old";
    public final static String incentive_video_hotspot = "incentive_video_hotspot";
    public final static String video_hotspot = "video_hotspot";
//    public final static String incentive_video_hotspot = "incentive_video_hotspot_copy1";
//    public final static String video_hotspot = "video_hotspot_copy1";
    public final static String user_incentive_log = "user_incentive_log";
    public final static String head_recommend_video = "head_recommend_video";


    public final static String user_raw_tags = "user_raw_tags_";
    public final static String userOffline_label = "userOffline_label_";
    public final static String test_user_raw_tags = "test_user_raw_tags";
    public final static String video_mca_result = "video_mca_result";
    public final static String scenario_video = "scenario_video";

    //视频百度飞桨标签
    public final static String video_paddle_tag = "video_paddle_tag";
    //视频向量接口调用log表
    public final static String video_embedding_log = "video_embedding_log";
    //用户向量接口调用log表
    public final static String user_embedding_log = "user_embedding_log";

    public final static int user_active_day = 10010;
    public final static int cat_fav = 10020;
    public final static int scene_fav = 10030;
    public final static int cat_fav_t = 10040;
    public final static int scene_fav_t = 10050;
    /**
     * 用户选择标签分类权重
     */
    public final static int USER_CAT_WEIGHT = 10080;
    /**
     * 用户播放分类池TF-IDF权重
     */
    public final static int USER_CAT_TFIDF_WEIGHT_4_PLAY = 10090;
    /**
     * 用户播放分类池TGI权重
     */
    public final static int USER_CAT_TGI_WEIGHT_4_PLAY = 10100;
    /**
     * 用户有效播放分类池TF-IDF权重
     */
    public final static int USER_CAT_TFIDF_WEIGHT_4_VALIDPLAY = 10110;
    /**
     * 用户有效播放分类池TGI权重
     */
    public final static int USER_CAT_TGI_WEIGHT_4_VALIDPLAY = 10120;
    /**
     * 用户完播分类池TF-IDF权重
     */
    public final static int USER_CAT_TFIDF_WEIGHT_4_OVERPLAY = 10130;
    /**
     * 用户完播分类池TGI权重
     */
    public final static int USER_CAT_TGI_WEIGHT_4_OVERPLAY = 10140;
    /**
     * 用户时长权重池
     */
    public final static int AVG_PLAY_WEIGHT = 10150;
    /**
     * 曝光数
     */
    public final static int SHOW_COUNT = 10160;
    /**
     * 播放数
     */
    public final static int PLAY_COUNT = 10170;
}
