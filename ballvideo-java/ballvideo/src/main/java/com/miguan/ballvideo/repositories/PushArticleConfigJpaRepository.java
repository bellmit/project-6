package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.PushArticleConfig;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author shixh
 * @Date 2019/12/23
 **/
public interface PushArticleConfigJpaRepository extends JpaRepository<PushArticleConfig, Long> {

    PushArticleConfig findByPushChannelAndMobileTypeAndAppPackage(String pushChannel,String mobileType,String appPackage);
}
