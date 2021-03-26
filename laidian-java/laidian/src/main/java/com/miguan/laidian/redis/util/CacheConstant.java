package com.miguan.laidian.redis.util;

/**
 * @Author shixh
 * @Date 2019/12/13
 **/
public interface CacheConstant {
    //@Cacheable key
    String CALENDAR = "queryCalendarToday";
    String LDBURYINGPOINT = "selectByDeviceIdAndAppTypeOrderByCreateTimeAsc";
    String LDBURYINGPOINT_USER = "findUserBuryingPointIsNew";
    String QUERY_ADERT_LIST = "queryAdertList";
    String QUICKSETUPCALL_SHOWVIDEOS = "quickSetupCallShowVideos";
    String FIND_CLMENUCONFIGLIST = "findClMenuConfigList";
    String COUNT_FORBIDDEN_VERSION = "countForbiddenVersion";
    String COUNT_FORBIDDEN_CHANNEL = "countForbiddenChannel";
    //菜单栏屏蔽缓存
    String COUNT_VERSION_LIST = "countVersionList";
    String COUNT_CHANNEL_LIST = "countChannelList";

    String GET_ADVERSWITHCACHE = "getAdversWithCache";

    String GET_ADVERSPOSITION = "getAdversPosition";

    //用户联系人设置视频缓存
    String USER_PHONE_VIDEO_CACHE = "findAllByUserIdAndPhoneOrderByUpdateDateDesc";

    //获取活动信息
    String GET_CUR_ACTIVITY_INFO = "getCurActivityInfo";

    String FIND_All_Device_Tokens = "findAllDeviceTokens";

    String QUERY_VIDEOS_CHANNEL_RECO_INFO = "queryVideosChannelRecoInfo";

    String USER_INTEREST_TAG = "userInterestTag";

    String INTEREST_VIDEO_ID_LIST = "interestVideoIdList";

    String FIND_PUSH_CONTENT_LIST = "findPushContentList";

    String QUERY_ALL_BY_STATUS = "queryAllByStatus";

    String FIND_ACT_ACTIVITY = "findActActivity";

    String VIDEO_ID_COUNT = "videoIdCount";

    String USER_LABELD_EFAULT = "getUserLabelDefault";

}
