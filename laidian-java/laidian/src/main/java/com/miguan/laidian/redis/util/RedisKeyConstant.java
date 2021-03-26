package com.miguan.laidian.redis.util;

/**
 * @Author shixh
 * @Date 2019/9/4
 **/
public interface RedisKeyConstant {

    String defalut = "laidian:";//公共前缀KEY

    int defalut_seconds = 300;//默认5分钟

    //login token
    String USER_TOKEN_KEY = defalut + "token:user:";

    //@CacheAble
    String CACHE_ABLE_KEY = defalut + "cacheAble:";

    //@RequestCache
    String REQUEST_CACHE_KEY = defalut + "requestCache:";

    //MQ
    String UUID_MQ = "uuid_mq_";
    String _MQ_ = "_mq_";

    //user token
    String USER_TOKEN = defalut + "token:user:";

    int USER_TOKEN_SECONDS = 60 * 60 * 24 * 30;

    //user 视频设置
    String VIDEO_SETTING = defalut + "user:videoSetting:";

    //视频设置缓存时间
    int VIDEO_SETTING_SECONDS = 60 * 60 * 24;

    //user 视频设置
    String VIDEO_SETTING_PHONE = defalut + "user:videoSettingPhone:";

    //vivo鉴权
    String VIVO_TOKEN = defalut + "vivo:token";

    //push task
    String PUSH_TASK = defalut + "initPushTask";
    int PUSH_TASK_SECONDS = 1000 * 60 * 10;

    //广告错误redis保存
    String ADVERT_ERROR_LOG = defalut + "AdvertErrorLog:";

    //处理保存异常错误日志
    String SEARCH_AND_CLEAR_WRONG_DATAS_LOCK = defalut + "search_and_clear_wrong_datas_lock:";
    int SEARCH_AND_CLEAR_WRONG_DATAS_SECONDS = 1000 * 60 * 10;

    //广告错误redis保存
    String ADVERT_ERROR_LOG_ALL = defalut + "AdvertErrorLog:all";

    //签到
    String ACTIVITY_TASK_SIGN_IN = defalut + "activityTaskSignIn:";
    //成功设置来电秀
    String ACTIVITY_TASK_SETTING_LDX = defalut + "activityTaskSettingLdx:";
    //成功设置来电铃声
    String ACTIVITY_TASK_SETTING_LS = defalut + "activityTaskSettingLs:";
    //会员抽奖次数
    String ACTIVITY_USER_DRAW_NUM = defalut + "activityUserDrawNum:";
    //分享活动
    String ACTIVITY_TASK_SHARE = defalut + "activityTaskShare:";
    //观看视频
    String ACTIVITY_TASK_VIDEO = defalut + "activityTaskVideo:";

    //活动总参与次数
    String ACTIVITY_TOTAL_JOIN_NUM = defalut + "activityTotalJoinNum:";

    //删除广告错误日志数据
    String ADVERT_ERROR_LOG_DELETE = defalut + "advertErrorLogDelete";
    int ADVERT_ERROR_LOG_SECONDS = 30000 * 60;

    String VIDEO_EXPOSURE = defalut + "videoExposure:";
    int VIDEO_EXPOSURE_SECONDS = 604800;//7天（秒）

    String CREATE_TABLE_REDIS_LOCK = defalut + "createTableRedisLock";//创建表锁
    int CREATE_TABLE_REDIS_LOCK_TIME = 300000;//5分钟

    int EMPTY_VALUE_SECONDS = 300;

    /**
     * 自动推送同步distinctid
     */
    String START_SYN_DISTINCT_STATUS = "xld_syn_distinct_status";
    String START_SYN_DISTINCT_FLAG = "xld_syn_distinct_flag";

    String AUTO_PUSH_TASK = defalut + "autoPushTask";

    String KEY_CATCH = AUTO_PUSH_TASK + ":keyCatch";

    int PUSH_TOKEN_SECONDS = 10800;//3小时（秒）


    //成功设置来电秀
    String AUTO_PUSH_ACTICLE_7DAY = defalut + "autoPushActicle7Day";

    int AUTO_PUSH_ACTICLE_7DAY_SECONDS = 60 * 60 * 24 * 7;

    //今日签到推送标识
    String AUTO_PUSH_SIGN = defalut + "autoPushSign:";

    //今日推送总数
    String AUTO_PUSH_TOTAL_NUM = defalut + "autoPushTotalNum:";

    String DELETE_AUTO_PUSH_LOG = defalut + "deleteAutoPushLog";//删除7天前自动推送日志记录

    //感兴趣视频ID列表
    String INTEREST_ID_LIST = defalut + "interestIdList:";
    int INTEREST_ID_LIST_SECONDS = 86400;//1天（秒）

    String VIDEO_ID_COUNT = defalut + "videoIdCount:";

    String AUTO_PUSH_SCHEDULE_ONE = defalut + "autoPushScheduleOne";
}
