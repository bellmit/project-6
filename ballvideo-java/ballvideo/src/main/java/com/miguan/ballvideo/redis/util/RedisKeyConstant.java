package com.miguan.ballvideo.redis.util;


import com.miguan.ballvideo.common.util.Global;

public interface RedisKeyConstant {

    String defalut = "ballVideos:";//公共前缀KEY
    int defalut_seconds = 300;//默认5分钟
    //user token
    String USER_TOKEN = defalut + "token:user:";
    int USER_TOKEN_SECONDS = 60 * 60 * 24 * 30;
    //user uuid，新创建的uuid保存24小时
    int UUID_SECONDS = 60 * 60 * 24;
    String UUID_KEY = defalut + "uuid:";

    /** push task*/
    String PUSH_TASK = defalut + "initPushTask";
    int PUSH_TASK_SECONDS = 1000 * 60 * 10;
    int AUTO_PUSH_TASK_SECONDS = 1000 * 60;

    //@RequestCache
    String REQUEST_CACHE_KEY = defalut + "requestCache:";

    //@CacheAble
    String CACHE_ABLE_KEY = defalut + "cacheAble:";

    //首页推荐1.8-用户标签
    String USERLABEL_KEY = defalut + "userLabe:";
    int USERLABELKEY_SECONDS = 604800;//有效期一周

    //首页推荐1.8-浏览记录ID
    String SHOWEDBYTEIDS_KEY = defalut + "showedByteIds:";
    String SHOWEDIDS_KEY = defalut + "showedByIds:";
    String RECO_DEVICE_SHOW_ID = "recoDeviceShowId:";
    int SHOWEDIDS_SECONDS = Global.getInt("user_showedIds_expiredTime");//有效期动态配置

    //首页推荐1.8-最近3天创建
    String CREATED3DAY = "created3Day";

    //首页推荐激励视频缓存
    String INCENTIVEVIDEO = "incentiveVideo";

    //视频缓存时间
    String VIDEO_CACHE = "videoCache";

    //待整合
    String INTERFACE = defalut + "interface:";

    String NEWFIRSTVIDEO161_KEY = INTERFACE + "newfirstVideo161:";
    int NEWFIRSTVIDEO161_SECONDS = 14400;//4h

    String NEWFIRSTVIDEO_KEY = INTERFACE + "newfirstVideo:";
    int NEWFIRSTVIDEO_SECONDS = 14400;//4h

    String ADVERTLIST161_KEY = INTERFACE + "advertList161:";
    int ADVERTLIST161_SECONDS = 120;//2m

    String NEWSMALLVIDEO17_KEY = INTERFACE + "newsmallVideo1.7:";
    int NEWSMALLVIDEO17_SECONDS = 14400;//4h

    String DEVICEID_KEY = CacheConstant.BURYINGPOINT_USER + "::ballVideos:cacheAble:findUserBuryingPointIsNew:";

    //vivo鉴权
    String VIVO_TOKEN = defalut + "vivo:token";

    //vivo限制
    String VIVO_LIMIT_TOKEN = defalut + "vivo:token:limit";

    //getBaiduHotWord
    String HOT_WORD = defalut + "getBaiduHotWord";
    int HOT_WORD_SECONDS = 300;

    //真实权重key前缀
    String REAL_WEIGHT_KEY = defalut + "realWeightVideoIds";
    //真实权重key前缀(锁)
    String REAL_WEIGHT_LOCK_KEY = defalut + "realWeight:redisLock";
    //真实权重锁时间（毫秒）
    int REAL_WEIGHT_LOCK_SECONDS = 3000;
    int REAL_WEIGHT_SECONDS = 60000;

    //文件预热
    String VIDEO_CACHE_PREFETCH = defalut + "videoCachePrefetch";
    int VIDEO_CACHE_PREFETCH_TIME = 300000;//5分钟

    //删除用户标签数据
    String USER_LABEL_DELETE = defalut + "userLabelDelete";
    int USER_LABEL_DELETE_SECONDS = 30000 * 60;

    //切片广告key
    String shangbaoKey = "shangbao:";
    String KEY_AD_RATE = shangbaoKey + "ad_rate";
    String KEY_AD_HIGH = "high";//V2.5.0新增
    String KEY_AD_LOW = "LOW";//V2.5.0新增

    //广告错误redis保存
    String ADVERT_ERROR_LOG = defalut + "AdvertErrorLog:";

    //广告错误日志信息，保存到Mongodb，保存失败再次保存
    String ADVERT_ERROR_LOG_MONGODB = defalut + "AdvertErrorLog:mongodb";

    String EMPTY_VALUE = "empty";//防止缓存击穿，设置value为empty

    int EMPTY_VALUE_SECONDS = 300;

    String CAT_IDS = "catIds:";//视频分类IDS

    int CAT_IDS_SECONDS = 14400;//4H

    //处理保存异常错误日志
    String SEARCH_AND_CLEAR_WRONG_DATAS_LOCK = defalut + "search_and_clear_wrong_datas_lock:";
    int SEARCH_AND_CLEAR_WRONG_DATAS_SECONDS = 1000 * 60 * 10;

    //观看的短视频Id(统计观看数)
    String VIDEO_WATCH_IDS = defalut + "videoWatchIds:";

    //处理保存观看数错误
    String VIDEO_WATCH_CLEAR_WRONG_DATAS_LOCK = defalut + "video_watch_clear_wrong_datas_lock";
    int VIDEO_WATCH_CLEAR_WRONG_DATAS_SECONDS = 1000 * 60;

    //曝光视频总数临时存储
    String VIDEO_EXPOSURE_DATA = defalut + "exposureData:";

    //曝光视频redis保存1小时曝光数(历史记录)
    String VIDEO_EXPOSURE_ONE_HOUR = defalut + "exposureHistory:";
    int VIDEO_EXPOSURE_ONE_HOUR_SECONDS = 3600;//1小时（秒）

    //曝光视频1小时数量统计到数据库(待保存数据)
    String VIDEO_EXPOSURE_ONE_HOUR_DATA = defalut + "exposureSave:";

    //处理保存曝光数错误
    String VIDEO_EXPOSURE_CLEAR_WRONG_DATAS_LOCK = defalut + "exposureLock";
    int VIDEO_EXPOSURE_CLEAR_WRONG_DATAS_SECONDS = 1000 * 60;

    //创建集合和索引
    String ADVERT_ERROR_COUNT_LOG_MONGODB = defalut + "userLabelDelete";
    int ADVERT_ERROR_COUNT_LOG_MONGODB_SECONDS = 30000 * 60;

    //打印日志锁，防止频繁调用
    String PRINT_LOG_LOCK = defalut + "printLog";
    int PRINT_LOG_LOCK_SECONDS = 1000 * 10;

    //专辑修改时间戳
    String ALBUM_CHANGE_TIME = defalut + "albumChangeTime";

    //保存应用类别信息
    String APP_TYPE = "ballVideotask:appType:";

    //热词关联视频数据，每个分类前20条
    String HOT_WORD_VIDEO = defalut + "hotWordVideo:";
    int HOT_WORD_VIDEO_SECONDS = 3600 * 2;//默认30分钟
    //热词关联视频数据，每个分类备用20条
    String HOT_WORD_VIDEO_BAK = defalut + "hotWordVideoBak:";

    //实时热播前三条数据
    String TOP_THREE_HOT_LIST_KEY = defalut+"topThreeHotListKey";
    //实时热播数据
    String HOUR_HOT_LIST_KEY = defalut+"hourHotListKey";
    //24小时热播数据
    String DAY_HOT_LIST_KEY = defalut+"dayHotListKey";

    //定时器推送任务系统信息
    String TASK_PUSH_CLOCK = defalut + "taskPushClock:";

    //保存用户视频分类兴趣池
    String USER_LIKE_CAT_POOL = "bg_sUp";
    // 用户实时分类兴趣度
    String user_cat_fav =  "bg_userCatFav:%s";
    /**
     * 推荐接口请求快照
     * %s -- UUID
     * %s -- 视频ID
     */
    String video_snapshoot = "bg_snapshoot:%s:%s";

    String BLOOM_FILTER_FOR_MONTH_LOCK = "rec:bloom_filter_for_month_lock";
    String BLOOM_FILTER_FOR_DAY_LOCK = "rec:bloom_filter_for_day_lock";

    //自动推送
    String AUTO_PUSH_FOR_DAY_LOCK = "rec:auto_push_for_day_lock";
    String AUTO_PUSH_FOR_HALF_HOUR_LOCK = "rec:auto_push_for_half_hour_lock";
    int AUTO_PUSH_LOCK_SECONDS = 30000;


    String START_SYN_DISTINCT_STATUS = "start_syn_distinct_status";
    String START_SYN_DISTINCT_FLAG = "start_syn_distinct_flag";

    int PUSH_TOKEN_SECONDS = 10800;//3小时（秒）

    String AUTO_PUSH_TASK = defalut + "autoPushTask";

    String KEY_CATCH = AUTO_PUSH_TASK + ":keyCatch";

    //两天
    int KEY_CATCH_SECONDS = 2 * 60 * 60 * 24 * 30;

    //红包的key
    String DOUBLE_ELEVEN_POPUP = defalut + "doubleElevenPopup";
    String AUTO_PUSH_SCHEDULE_ONE = defalut + "autoPushScheduleOne";

    String MQ_PROCESS = defalut + "mqProcess";

    //刷新专辑信息
    String FLASH_VIDEO_ALBUM_INFO = defalut + "flashVideoAlbumInfo";

    //专辑视频Id信息
    String VIDEO_ALBUM_INFO = defalut + "videoAlbumInfo:";

    //更新视频Id所属专辑临时存储
    String UPDATE_VIDEO_ALBUM = defalut + "updateVideoAlbum";

    //视频Id所属专辑Id
    String VIDEO_ID_TO_ALBUM = defalut + "videoIdToAlbum";

    //appToken
    String APP_TOKEN = defalut + "appToken";
    //appToken保存24小时
    int APP_TOKEN_SECONDS = 60 * 60 * 24;

    String INTERESTLABEL_KEY = defalut + "interestLabel:";

    String SPECIAL_VIDEO_LIST = defalut + "specialVideoList:";
}
