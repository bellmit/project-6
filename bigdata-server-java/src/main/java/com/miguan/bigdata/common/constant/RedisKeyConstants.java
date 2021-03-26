package com.miguan.bigdata.common.constant;

public class RedisKeyConstants {

    private final static String prifex = "bg_s_";

    public final static String push_search_index = prifex + "push_search_index";

    public final static String npush_channel_lock = prifex + "npush_channel_lock";
    public final static String npush_iteration_lock = prifex + "npush_iteration_lock";
    public final static String npush_bloom_init_lock = prifex + "npush_bloom_init_lock";

    //激励视频入库缓存前缀
    public static final String IN_INCENTIVE_CACHE_PREFIX = "in_incentive_cache:";
    public static final int IN_INCENTIVE_CACHE_EXPIRES = 86400;


}