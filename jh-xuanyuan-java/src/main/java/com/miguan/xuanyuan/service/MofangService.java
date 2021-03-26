package com.miguan.xuanyuan.service;

import com.miguan.xuanyuan.common.exception.ServiceException;
import com.miguan.xuanyuan.dto.ab.ChannelInfoVo;
import com.miguan.xuanyuan.vo.AgentUsersVo;
import com.miguan.xuanyuan.vo.VersionVo;

import java.util.List;
import java.util.Map;

/**
 * @Description 魔方service
 **/
public interface MofangService {
    /**
     * 查询魔方后台是否禁用该渠道的广告:1禁用，0非禁用
     * @param param
     * @return
     */
    boolean stoppedByMofang(Map<String, Object> param);

    List<AgentUsersVo> findChannelList(String appPackage);

    List<VersionVo> getVersionList(String appPackage);

    public Integer searchAppId(String appType) throws ServiceException;


    List<ChannelInfoVo> findChannelInfoByKeys(List<String> channelIds);


}
