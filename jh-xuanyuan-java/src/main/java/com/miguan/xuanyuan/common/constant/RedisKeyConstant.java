package com.miguan.xuanyuan.common.constant;



public interface RedisKeyConstant {

    String defalut = "xuanyuan:";//公共前缀KEY
    int defalut_seconds = 300;//默认5分钟

    //开关配置redis的可以前缀
    String CONFIG_CODE = "XY_MAP_CONFIG:";


    //xy的logo
    String XY_LOGO = "xy_logo";
    //android的logo
    String ANDROID_LOGO = "android_logo";
    //android的logo
    String IOS_LOGO = "ios_logo";

    String CHANNAL_SEARCH_APPID = "channal_search_appid";
    int CHANNAL_SEARCH_APPID_SECONDS = 1000 * 60 * 30;

    String AB_APP_LIST_TOKEN = "ab_test_login_token";
    int AB_APP_LIST_TOKEN_SECONDS = 1000 * 60 * 30;

    String AB_TEST_LOGIN_TOKEN = "ab_test_login_token";
    int AB_TEST_LOGIN_TOKEN_SECONDS = 1000 * 60 * 30;

    String AB_TEST_AD_TAG = "ab_test_ad_tag";
    int AB_TEST_AD_TAG_SECONDS = 1000 * 60 * 30;

    String AB_TEST_APP_ID = "ab_test_app_id";
    int AB_TEST_APP_ID_SECONDS = 1000 * 60 * 30;

    String TASK_PUB_AB_EXP = "task_pub_ab_exp";

    String CREATIVE_INFO_CACHE = "creative_info_cache";
    int CREATIVE_INFO_CACHE_SECOND = 60 * 5;

}
