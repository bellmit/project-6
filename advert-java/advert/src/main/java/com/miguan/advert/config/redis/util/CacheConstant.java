package com.miguan.advert.config.redis.util;

public interface CacheConstant {
    //cacheAble key
    String defalut = "advert:";//公共前缀KEY

    String FIND_CHANNEL_INFO = defalut + "findChannelInfo";

    String STOPPED_BY_MOFANG = "stoppedByMofang";

    String GET_ADVERSWITHCACHE = "getAdversWithCache";

    String COUNT_FORBIDDEN_VERSION = "countForbiddenVersion";

    String COUNT_FORBIDDEN_CHANNEL = "countForbiddenChannel";

    String FIND_TABLE_INFO = "findTableInfo";

    String FIND_TABLE_COLUMN_COMMON = "findTableColumnCommon";

    String FIND_VERSION_INFO = "findVersionInfo";

}
