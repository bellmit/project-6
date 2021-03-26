package com.miguan.advert.domain.service;

import com.miguan.advert.config.redis.util.CacheConstant;
import com.miguan.advert.domain.vo.ChannelInfoVo;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

public interface ToolMofangService {

    /**
     * 获查询魔方渠道信息
     * @return
     */
    @Cacheable(value = CacheConstant.FIND_CHANNEL_INFO, unless = "#result == null || #result.size()==0")
    List<ChannelInfoVo> findChannelInfo();

    /**
     * 通过keys获查询魔方渠道信息
     * @return
     */
    public List<ChannelInfoVo> findChannelInfoByKeys(String channelIds);

    /**
     * 查询魔方后台是否禁用该渠道的广告:1禁用，0非禁用
     * @param param
     * @return
     */
    @Cacheable(value = CacheConstant.STOPPED_BY_MOFANG, unless = "#result == null")
    boolean stoppedByMofang(Map<String, Object> param);


    /**
     * 跨库查询魔方后台数据，根据渠道判断是否屏蔽广告
     */
    @Cacheable(value = CacheConstant.COUNT_FORBIDDEN_CHANNEL, unless = "#result == null")
    int countChannel(String postitionType, String channelId, String appPackage, String appVersion, int tagType);

    /**
     * 跨库查询魔方后台数据，根据版本判断是否屏蔽全部广告
     */
    @Cacheable(value = CacheConstant.COUNT_FORBIDDEN_VERSION, unless = "#result == null")
    int countVersion(String postitionType, String appVersion, String appPackage, int tagType);

    @Cacheable(value = CacheConstant.FIND_VERSION_INFO, unless = "#result == null || #result.size()==0")
    List<String> findVersionInfo(String appType);

    Integer searchAppId(String app_type);
}
