package com.miguan.ballvideo.service;

import com.miguan.ballvideo.common.interceptor.argument.params.CommonParamsVo;
import com.miguan.ballvideo.vo.ChannelGroup;
import com.miguan.ballvideo.entity4.MessageDictionary;
import com.miguan.ballvideo.entity4.MessageModel;
import com.miguan.ballvideo.redis.util.CacheConstant;
import com.miguan.ballvideo.vo.SysVersionVo;
import org.springframework.cache.annotation.Cacheable;

import java.util.List;
import java.util.Map;

public interface ToolMofangService {

    /**
     * 获取系统版本配置信息
     * @param appPackage
     * @param appVersion
     * @return
     */
    List<SysVersionVo> findUpdateVersionSet(String appPackage, String appVersion);

    /**
     * 上报版本更新人数
     * @param commonParams
     * @return
     */
    int updateSysVersionInfo(CommonParamsVo commonParams);

    /**
     * 根据包名查询短信签名
     * @param appPackage
     * @return
     */
    @Cacheable(value = CacheConstant.GET_CHANNEL_GROUPS, unless = "#result == null")
    List<ChannelGroup> getChannelGroups(String appPackage);

    /**
     * 跨库查询魔方后台数据，根据版本判断是否屏蔽全部广告
     */
    @Cacheable(value = CacheConstant.COUNT_FORBIDDEN_VERSION, unless = "#result == null")
    int countVersion(String postitionType, String appVersion, String appPackage, int tagType);

    /**
     * 跨库查询魔方后台数据，根据渠道判断是否屏蔽广告
     */
    @Cacheable(value = CacheConstant.COUNT_FORBIDDEN_CHANNEL, unless = "#result == null")
    int countChannel(String postitionType, String channelId, String appPackage, String appVersion, int tagType);

    /**
     * 查询魔方后台是否禁用该渠道的广告:1禁用，0非禁用
     * @param param
     * @return
     */
    @Cacheable(value = CacheConstant.STOPPED_BY_MOFANG, unless = "#result == null")
    boolean stoppedByMofang(Map<String, Object> param);

    /**
     * 跨库查询魔方后台数据，获取渠道id和供应商
     */
    void ChannelInit();

    /**
     * 跨库查询魔方后台数据，模板表
     *  @param messageModelId
     *   @return
     */
    public List<MessageModel> getMessageModelByModelId(String messageModelId);

    /**
     * 跨库查询魔方后台数据，字典表
     *  @param messageModelId
     *  @return
     */
    public  List<MessageDictionary> getMessageDictionaryByModelId(String messageModelId);

}
