package com.miguan.xuanyuan.common.constant;



public interface RedisConstant {

    int DEFALUT_SECONDS = 180; //缓存默认3分钟过期

    int DAY_SECOND = 24 * 60 * 60; //一天时间

    String EMPTY_VALUE = "empty";//防止缓存击穿，设置value为empty

    //开关配置redis的可以前缀
    public static final String CONFIG_CODE_PRE = "SYS_CONFIG:";

    //用户登录token缓存
    public static final String USER_TOKEN_PREFIX = "user_token:";
    public static final int USER_TOKEN_EXPIRES = 3600 * 4;


    String SHIELD_CHANNEL = "shieldChannel";  //屏蔽渠道


    /**
     * 阶梯广告延迟请求毫秒key
     */
    String XY_LADDER_DELAY_MILLIS = "xy_ladder_delay_millis";

    /**
     * 通跑广告延迟请求毫秒数key
     */
    String XY_COMMON_DELAY_MILLIS = "xy_common_delay_millis";

    /**
     * 配置缓存时间
     */
    String CONFIGURE_CACHE_TIME = "configure_cache_time";


    String XY_SORT_MULTI_DATE = "xy_sort_multi_date"; //代码位多维度数据日期


    String XY_AB_TEST_DATA = "xy_ab_test_data:"; //AB测试数据

    String XY_AD_PROFIT = "xy_ad_profit"; //最近一天代码位收益


    String AD_MULTI_DATA = "xy_ad_multi_data:"; //代码位多维度数据（代码位自动排序）

    String AD_TOTAL_DATA = "xy_ad_total_data:"; //代码位的数据（代码位自动排序）

    String AD_DATA = "xy_ad_data:"; //代码位数据（代码位自动排序）

    String ADV_CODES = "xy_ad_list:";  //代码位列表

    String ADV_POSITION_INFO = "xy_position_info:";  //广告位信息

    String AD_POSITION = "xy_ad_position:";  //广告位列表


    public static final String REGISTER_VERIFI_CODE_CNT = "register_verifi_code_cnt:"; //注册验证码缓存
    public static final int REGISTER_VERIFI_CODE_CNT_EXPIRES = 600; //注册验证码缓存10分钟


    public static final String REGISTER_VERIFI_CODE = "register_verifi_code:"; //注册验证码缓存
    public static final int REGISTER_VERIFI_CODE_EXPIRES = 600; //注册验证码缓存10分钟


}
