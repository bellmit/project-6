package com.miguan.ballvideo.redis.util;

/**
 * @Author shixh
 * @Date 2019/12/13
 **/
public interface CacheConstant {
    //cacheAble key
    String GET_CATIDS_BY_CHANNELID_AND_APPVERSION = "getCatIdsByChannelIdAndAppVersion";

    String GET_CATIDS_BY_CHANNELID_AND_APPVERSION_FromTeenager = "getCatIdsByChannelIdAndAppVersionFromTeenager";

    String QUERY_ADERT_LIST = "queryAdertList";

    String QUERY_LADDER_ADERT_LIST = "queryLadderAdertList";

    String GET_AD_POSITION_CONFIG_LIST = "getAdPositionConfigList";

    String QUERY_ADERT_LIST_ALL = "queryAdertListAll";

    String QUERY_AD_POSITION_ID = "queryAdPositionId";

    String CATID_BY_VIDEOID = "findCatIdByVideoId";

    String USER_LABELD_EFAULT = "getUserLabelDefault";

    String BURYINGPOINT_USER = "findUserBuryingPointIsNew";

    String FIND_CATIDS_NOTIN = "findCatIdsNotIn";

    String FIRSTVIDEOS_CATLIST = "firstVideosCatList";

    String FIND_ALL_BY_STATE = "findAllByState";

    String FIND_BY_GATHERID = "findByGatherId";

    String FIND_CATIDS_BYSTATE = "findCatIdsByState";

    String COUNT_BY_GATHERID = "countByGatherId";

    String GET_BY_GATHERID = "getByGatherId";

    String FIND_HOT_WORD_INFO = "findHotWordInfo";

    String FIND_HOT_WORD_INFO1 = "findHotWordInfo1";

    String POSITION_TYPE_GAME = "positionTypeGame";

    String FIND_ABTESTCONFIG = "findAbTestConfig";

    String FIND_CLMENUCONFIGLISTBYAPPPACKAGE = "findClMenuConfigListByAppPackage";

    String GET_ADVERSWITHCACHE = "getAdversWithCache";

    String FIND_All_Tokens = "findAllTokens";

    String FIND_All_Device_Tokens = "findAllDeviceTokens";

    String COUNT_FORBIDDEN_VERSION = "countForbiddenVersion";
    String COUNT_FORBIDDEN_CHANNEL = "countForbiddenChannel";

    String STOPPED_BY_MOFANG = "stoppedByMofang";

    String QUERY_POP_CONFIG_LIST = "queryPopConfigList";

    String GET_ADV_INFO_LST_BY_SQL = "getAdvInfoLstBySql";
}
