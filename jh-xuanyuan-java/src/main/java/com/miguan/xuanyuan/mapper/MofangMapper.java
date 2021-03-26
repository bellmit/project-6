package com.miguan.xuanyuan.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.xuanyuan.dto.ab.ChannelInfoVo;
import com.miguan.xuanyuan.vo.AgentUsersVo;
import com.miguan.xuanyuan.vo.VersionVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description 魔方mapper
 **/
@DS("mofang")
public interface MofangMapper {

    /**
     * 根据版本判断是否屏蔽全部广告
     * @param params
     * @return
     */
    int countVersion(Map<String, Object> params);

    /**
     * 非全部的屏蔽根据渠道查询是否屏蔽广告
     * @param params
     * @return
     */
    int countChannel(Map<String, Object> params);

    List<Long> findGroupByPackage(String appPackage);

    List<AgentUsersVo> findAgentUserByGroup(@Param("groupIds")List<Long> groupIds);

    List<VersionVo> getVersionList(String appPackage);

    public Integer searchAppId(@Param(value="appType") String appType);

    List<ChannelInfoVo> findChannelInfoByKeys(List<String> channelIds);
}

