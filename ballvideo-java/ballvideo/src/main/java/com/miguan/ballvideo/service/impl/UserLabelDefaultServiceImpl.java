package com.miguan.ballvideo.service.impl;

import com.miguan.ballvideo.entity.UserLabelDefault;
import com.miguan.ballvideo.redis.util.CacheConstant;
import com.miguan.ballvideo.repositories.UserLabelDefaultJpaRepository;
import com.miguan.ballvideo.service.UserLabelDefaultService;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author shixh
 * @Date 2019/10/22
 **/
@Service("UserLabelDefaultService")
public class UserLabelDefaultServiceImpl implements UserLabelDefaultService {

    public static final String DEFAULT_USER_LABEL = "default";

    @Resource
    private UserLabelDefaultJpaRepository userLabelDefaultJpaRepository;


    @Override
    @Cacheable(value = CacheConstant.USER_LABELD_EFAULT, unless = "#result == null")
    public UserLabelDefault getUserLabelDefault(String channelId) {
        if (!"xysp_guanwang".equals(channelId)){
            channelId = "%" + channelId + "%";
        }
        UserLabelDefault userLabelDefault = userLabelDefaultJpaRepository.getLabelDefaultByChannelId(channelId);
        if(userLabelDefault == null){
            userLabelDefault = userLabelDefaultJpaRepository.findTopByChannelId(DEFAULT_USER_LABEL);
        }
        return userLabelDefault;
    }
}
