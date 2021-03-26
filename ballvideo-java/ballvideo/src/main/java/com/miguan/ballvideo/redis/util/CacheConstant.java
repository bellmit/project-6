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

    String QUERY_ADERT_LIST_ALL = "queryAdertListAll";

    String CATID_BY_VIDEOID = "findCatIdByVideoId";

    String USER_LABELD_EFAULT = "getUserLabelDefault";

    String BURYINGPOINT_USER = "findUserBuryingPointIsNew";

    String FIND_CATIDS_NOTIN = "findCatIdsNotIn";

    String FIRSTVIDEOS_CATLIST = "firstVideosCatList";

    String FIND_ALL_BY_STATE = "findAllByState";

    String FIND_CATIDS_BYSTATE = "findCatIdsByState";

    String GET_BY_GATHERID = "getByGatherId";

    String FIND_HOT_WORD_INFO = "findHotWordInfo";

    String FIND_HOT_WORD_INFO1 = "findHotWordInfo1";

    String FIND_HOT_WORD_INFO2 = "findHotWordInfo2";

    String FIND_HOT_WORD_INFO3 = "findHotWordInfo3";

    String FIND_HOT_WORD_INFO4 = "findHotWordInfo4";

    String POSITION_TYPE_GAME = "positionTypeGame";

    String FIND_ABTESTCONFIG = "findAbTestConfig";

    String FIND_CLMENUCONFIGLISTBYAPPPACKAGE = "findClMenuConfigListByAppPackage";

    String GET_ADVERSWITHCACHE = "getAdversWithCache";

    String GET_ABTEXTADVERSBYRULE = "getABTextAdversByRule";

    String QUERY_OPENSTATUSBYABTESTID = "queryOpenStatusByAbTestId";

    String FIND_All_Tokens = "findAllTokens";

    String COUNT_FORBIDDEN_VERSION = "countForbiddenVersion";
    String COUNT_FORBIDDEN_CHANNEL = "countForbiddenChannel";

    String STOPPED_BY_MOFANG = "stoppedByMofang";

    String QUERY_POP_CONFIG_LIST = "queryPopConfigList";

    String QUERY_CATIDS_LIST = "queryCatIdsList";

    String QUERY_VIDEOS_CAT_SORT_LIST = "queryVideosCatSortList";

    String QUERY_HOT_WORD_VIDEO = "queryHotWordVideo";

    String QUERY_HOT_WORD_VIDEO_ID = "queryHotWordVideoId";

    String QUERY_ALL_BY_STATUS = "queryAllByStatus";

    String IMMERSE_VIDEO_LIST = "immerseVideoList";

    String FIND_ALBUM_TITLE = "findAlbumTitle";

    String GET_CHANNEL_GROUPS = "getChannelGroups";
}
