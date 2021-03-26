package com.miguan.laidian.repositories;

import com.miguan.laidian.entity.PushArticleSendResult;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PushArticleSendResultJpaRepository extends JpaRepository<PushArticleSendResult, Long> {

    PushArticleSendResult findByPushChannelAndBusinessId(String pushChannel, String businessId);

}
