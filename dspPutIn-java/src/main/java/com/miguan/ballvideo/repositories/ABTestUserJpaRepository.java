package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.AbTestUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ABTestUserJpaRepository extends JpaRepository<AbTestUser, Long> {

    /**
     * 根据设备ID和AB测试配置ID查询AB测试用户
     * @param deviceId
     * @return
     */
    List<AbTestUser> findByDeviceIdAndAbTestConfigId(String deviceId,Long abTestConfigId);
}
