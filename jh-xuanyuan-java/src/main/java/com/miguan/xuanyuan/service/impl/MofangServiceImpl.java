package com.miguan.xuanyuan.service.impl;
import com.google.common.collect.Lists;
import com.miguan.xuanyuan.common.constant.RedisConstant;
import com.miguan.xuanyuan.common.constant.RedisKeyConstant;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.exception.ServiceException;
import com.miguan.xuanyuan.dto.ab.ChannelInfoVo;
import com.miguan.xuanyuan.mapper.MofangMapper;
import com.miguan.xuanyuan.service.MofangService;
import com.miguan.xuanyuan.service.common.RedisService;
import com.miguan.xuanyuan.vo.AgentUsersVo;
import com.miguan.xuanyuan.vo.VersionVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * @Description 魔方serviceImpl
 **/
@Service
public class MofangServiceImpl implements MofangService {

    @Resource
    private MofangMapper mofangMapper;
    @Resource
    private RedisService redisService;


    /**
     * 查询魔方后台是否禁用该渠道的广告:1禁用，0非禁用
     * @param param
     * @return
     */
    public boolean stoppedByMofang(Map<String, Object> param) {
        String key = RedisConstant.SHIELD_CHANNEL + param.toString();
        String value = redisService.get(key);
        if(StringUtils.isNotBlank(value)) {
            return Boolean.valueOf(value);
        } else {
            String mobileType = param.get("mobileType") + "";
            int tagType = XyConstant.IOS.equals(mobileType) ? 2 : 1;
            param.put("tagType", tagType);
            //当没有传入app版本.默认为不需要校验
            if(param.get("appVersion") != null && StringUtils.isNotEmpty(param.get("appVersion").toString())){
                int count1 = mofangMapper.countVersion(param);
                //根据版本判断是否屏蔽全部广告
                if (count1 > 0) {
                    redisService.set(key, String.valueOf(true), RedisConstant.DEFALUT_SECONDS);
                    return true;
                }
            }
            //当没有传入channelId.默认为不需要校验
            if(param.get("channelId") != null && StringUtils.isNotEmpty(param.get("channelId").toString())){
                //非全部的屏蔽根据渠道查询是否屏蔽广告
                int count2 = mofangMapper.countChannel(param);
                if (count2 > 0) {
                    redisService.set(key, String.valueOf(true), RedisConstant.DEFALUT_SECONDS);
                    return true;
                }
            }
            redisService.set(key, String.valueOf(false), RedisConstant.DEFALUT_SECONDS);
            return false;
        }
    }

    @Override
    public List<AgentUsersVo> findChannelList(String appPackage) {
        List<Long> groupIds = mofangMapper.findGroupByPackage(appPackage);
        if(CollectionUtils.isEmpty(groupIds)){
            return Lists.newArrayList();
        }
        List<AgentUsersVo> userVos = mofangMapper.findAgentUserByGroup(groupIds);
        return userVos == null ? Lists.newArrayList() : userVos;
    }

    public List<VersionVo> getVersionList(String appPackage) {
        return  mofangMapper.getVersionList(appPackage);
    }

    public Integer searchAppId(String appType) throws ServiceException {
        Integer appId = null;
        String appIdCache = redisService.get(RedisKeyConstant.CHANNAL_SEARCH_APPID + appType);
        if (StringUtils.isEmpty(appIdCache)) {
            try {
                appId = mofangMapper.searchAppId(appType);
            } catch ( EmptyResultDataAccessException e){
                throw new ServiceException("请检查app_type在channel_group表有没有对应上！并且有app_id");
            }
            if(appId != null){
                redisService.set(RedisKeyConstant.CHANNAL_SEARCH_APPID + appType, appId,RedisKeyConstant.CHANNAL_SEARCH_APPID_SECONDS);
            }
        } else {
            appId = Integer.parseInt(appIdCache);
        }

        if(appId == null){
            throw new ServiceException("请检查app_type在channel_group表有没有对应上！并且有app_id");
        }
        return appId;
    }

    public List<ChannelInfoVo> findChannelInfoByKeys(List<String> channelIds) {
        return mofangMapper.findChannelInfoByKeys(channelIds);
    }

}
