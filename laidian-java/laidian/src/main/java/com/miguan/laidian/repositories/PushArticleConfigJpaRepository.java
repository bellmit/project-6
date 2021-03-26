package com.miguan.laidian.repositories;

import com.miguan.laidian.entity.PushArticleConfig;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * @Author shixh
 * @Date 2019/12/23
 **/
public interface PushArticleConfigJpaRepository extends JpaRepository<PushArticleConfig, Long> {

    PushArticleConfig findByPushChannelAndMobileTypeAndAppType(String pushChannel, String mobileType, String AppType);
}
