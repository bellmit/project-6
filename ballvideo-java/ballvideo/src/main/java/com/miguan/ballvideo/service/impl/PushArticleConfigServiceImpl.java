package com.miguan.ballvideo.service.impl;

import com.miguan.ballvideo.entity.PushArticleConfig;
import com.miguan.ballvideo.mapper.PushArticleConfigMapper;
import com.miguan.ballvideo.repositories.PushArticleConfigJpaRepository;
import com.miguan.ballvideo.service.PushArticleConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author shixh
 * @Date 2019/12/23
 **/
@Service
public class PushArticleConfigServiceImpl implements PushArticleConfigService {

    @Resource
    private PushArticleConfigJpaRepository pushArticleConfigJpaRepository;
    @Resource
    private PushArticleConfigMapper pushArticleConfigMapper;


    @Override
    public PushArticleConfig findPushArticleConfig(String pushChannel, String mobileType,String appPackage){
//        return pushArticleConfigJpaRepository.findByPushChannelAndMobileTypeAndAppPackage(pushChannel,mobileType,appPackage);
        return pushArticleConfigMapper.findByPushChannelAndMobileTypeAndAppPackage(pushChannel, mobileType, appPackage);
    }
}
