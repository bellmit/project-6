package com.miguan.laidian.service.impl;

import com.miguan.laidian.entity.PushArticleConfig;
import com.miguan.laidian.repositories.PushArticleConfigJpaRepository;
import com.miguan.laidian.service.PushArticleConfigService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @Author laiyd
 * @Date 2020/4/14
 **/
@Service
public class PushArticleConfigServiceImpl implements PushArticleConfigService {

    @Resource
    private PushArticleConfigJpaRepository pushArticleConfigJpaRepository;


    @Override
    public PushArticleConfig findPushArticleConfig(String pushChannel, String mobileType, String appType){
        return pushArticleConfigJpaRepository.findByPushChannelAndMobileTypeAndAppType(pushChannel,mobileType,appType);
    }
}
