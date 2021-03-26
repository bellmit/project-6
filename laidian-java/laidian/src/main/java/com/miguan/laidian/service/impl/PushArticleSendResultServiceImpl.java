package com.miguan.laidian.service.impl;

import com.miguan.laidian.entity.PushArticleSendResult;
import com.miguan.laidian.repositories.PushArticleSendResultJpaRepository;
import com.miguan.laidian.service.PushArticleSendResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Author laiyd
 * @Date 2020/4/14
 **/
@Slf4j
@Service
public class PushArticleSendResultServiceImpl implements PushArticleSendResultService {

    @Resource
    private PushArticleSendResultJpaRepository pushArticleSendResultJpaRepository;


    @Override
    public void saveSendResult(Long pushArticleId, String pushChannel, String businessId,String appPackage) {
        PushArticleSendResult pushArticleSendResult = new PushArticleSendResult();
        pushArticleSendResult.setBusinessId(businessId);
        pushArticleSendResult.setPushArticleId(pushArticleId);
        pushArticleSendResult.setPushChannel(pushChannel);
        pushArticleSendResult.setAppType(appPackage);
        pushArticleSendResult.setCreateDate(new Date());
        pushArticleSendResultJpaRepository.save(pushArticleSendResult);
    }

    @Override
    public PushArticleSendResult findByPushChannelAndBusinessId(String pushChannel, String businessId) {
        return pushArticleSendResultJpaRepository.findByPushChannelAndBusinessId(pushChannel,businessId);
    }

    @Override
    public void save(PushArticleSendResult sendResult) {
        pushArticleSendResultJpaRepository.save(sendResult);
    }
}
