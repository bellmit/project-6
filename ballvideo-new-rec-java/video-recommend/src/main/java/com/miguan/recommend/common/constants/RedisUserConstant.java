package com.miguan.recommend.common.constants;

public class RedisUserConstant {

    /**************************************************** 茜柚推荐key 开始****************************************************/
    /**
     * 前缀
     */
    public static final String prefix = "bg_use_";
    /**
     * 用户分类得分
     * %s -- dayOfMonth
     * %s -- uuid
     */
    public static final String user_cat_score = prefix + "cat_score:%s:%s";
    /**
     * 用户选择的分类池
     * %s -- 日期格式：'2011-12-03'
     * %s -- uuid 用户ID
     */
    public static final String user_interest_weight_pool = prefix + "interestWeight:%s:%s";

    /**
     * 当日用户播放权重TF-IDF
     * %s -- 日期
     * %s -- 用户ID
     */
    public static final String user_play_weight_tfidf_pool = prefix + "playWeightTf:%s:%s";

    /**
     * 当日用户有效播放权重TF-IDF
     * %s -- 日期
     * %s -- 用户ID
     */
    public static final String user_valid_play_weight_tfidf_pool = prefix + "validPlayWeightTf:%s:%s";

    /**
     * 当日用户完播权重TF-IDF
     * %s -- 日期
     * %s -- 用户ID
     */
    public static final String user_over_play_weight_tfidf_pool = prefix + "OverPlayWeightTf:%s:%s";

    /**
     * 当日用户播放权重TGI
     * %s -- 日期
     * %s -- 用户ID
     */
    public static final String user_play_weight_tgi_pool = prefix + "playWeightTgi:%s:%s";

    /**
     * 当日用户有效播放权重TGI
     * %s -- 日期
     * %s -- 用户ID
     */
    public static final String user_valid_play_weight_tgi_pool = prefix + "validPlayWeightTgi:%s:%s";

    /**
     * 当日用户完播权重TGI
     * %s -- 日期
     * %s -- 用户ID
     */
    public static final String user_over_play_weight_tgi_pool = prefix + "OverPlayWeightTgi:%s:%s";

    /**
     * 当日用户平均播放权重
     * %s -- 日期
     * %s -- 用户ID
     */
    public static final String user_avg_play_weight_pool = prefix + "avgPlayWeight:%s:%s";

    /**
     * 用户近14日视频总播放统计
     */
    public static final String user_play_count = prefix + "videoPlayCount:";

    /**
     * 用户近14日视频总曝光统计
     */
    public static final String user_show_count = prefix + "videoShowCount:";

    /**************************************************** 茜柚推荐key 结束****************************************************/



    /**************************************************** 通用推荐key 开始****************************************************/

    public static final String n_prefix = "n_bg_user:";
    /**
     * 用户感兴趣的分类池
     */
    public static final String n_user_cat_pool = n_prefix + "cat_pool:";
    /**************************************************** 通用推荐key 结束****************************************************/
}
