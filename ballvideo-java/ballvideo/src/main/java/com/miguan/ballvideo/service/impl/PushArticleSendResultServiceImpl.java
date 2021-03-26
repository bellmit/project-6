package com.miguan.ballvideo.service.impl;

import com.miguan.ballvideo.entity.PushArticleSendResult;
import com.miguan.ballvideo.mapper.PushArticleSendResultMapper;
import com.miguan.ballvideo.repositories.PushArticleSendResultJpaRepository;
import com.miguan.ballvideo.service.PushArticleSendResultService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * @Author shixh
 * @Date 2019/12/20
 **/
@Slf4j
@Service
public class PushArticleSendResultServiceImpl implements PushArticleSendResultService {

    @Resource
    private PushArticleSendResultJpaRepository pushArticleSendResultJpaRepository;
    @Resource
    private PushArticleSendResultMapper pushArticleSendResultMapper;


    @Override
    public void saveSendResult(Long pushArticleId, String pushChannel, String businessId,String appPackage) {
//        PushArticleSendResult pushArticleSendResult = new PushArticleSendResult();
//        pushArticleSendResult.setBusinessId(businessId);
//        pushArticleSendResult.setPushArticleId(pushArticleId);
//        pushArticleSendResult.setPushChannel(pushChannel);
//        pushArticleSendResult.setAppPackage(appPackage);
//        pushArticleSendResult.setCreateDate(new Date());
//        pushArticleSendResultJpaRepository.save(pushArticleSendResult);
        pushArticleSendResultMapper.insertPushArticleSendResult(businessId, pushArticleId, pushChannel, appPackage);
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
