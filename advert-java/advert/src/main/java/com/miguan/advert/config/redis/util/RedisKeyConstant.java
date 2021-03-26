package com.miguan.advert.config.redis.util;


public interface RedisKeyConstant {

    String defalut = "advert:";//公共前缀KEY
    int defalut_seconds = 300;//默认5分钟
    int defalut_milliseconds = 3000;//默认3秒
    //user token
    String USER_TOKEN = defalut + "token:user:";
    int ONE_DAY_SECONDS = 86400;//24小时（秒）
    int NEW_USER_CHECK_SECONDS = 604800;//7天（秒）
    String EMPTY_VALUE = "empty";//防止缓存击穿，设置value为empty
    int EMPTY_VALUE_SECONDS = 300;



    String DING_TALK_AD_MONITORY_HOUR = "hour_ding_talk_ad_monitory";
    String DING_TALK_AD_MONITORY_DAY = "day_ding_talk_ad_monitory";
    int DING_TALK_AD_MONITORY_SECONDS = 1000 * 60 * 5;

    String AB_TEST_LOGIN_TOKEN = "ab_test_login_token";
    int AB_TEST_LOGIN_TOKEN_SECONDS = 1000 * 60 * 30;
    String AB_TEST_AD_TAG = "ab_test_ad_tag";
    int AB_TEST_AD_TAG_SECONDS = 1000 * 60 * 30;


    String AB_APP_LIST_TOKEN = "ab_test_login_token";
    int AB_APP_LIST_TOKEN_SECONDS = 1000 * 60 * 30;


    String CHANNAL_SEARCH_APPID = "channal_search_appid";
    int CHANNAL_SEARCH_APPID_SECONDS = 1000 * 60 * 30;


    String TASK_PUB_AB_EXP = "task_pub_ab_exp";
}
