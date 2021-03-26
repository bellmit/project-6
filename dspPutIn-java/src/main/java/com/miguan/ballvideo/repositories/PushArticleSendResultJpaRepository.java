package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.PushArticleSendResult;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PushArticleSendResultJpaRepository extends JpaRepository<PushArticleSendResult, Long> {

    PushArticleSendResult findByPushChannelAndBusinessId(String pushChannel, String businessId);

}
