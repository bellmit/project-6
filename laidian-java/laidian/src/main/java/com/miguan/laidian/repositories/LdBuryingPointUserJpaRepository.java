package com.miguan.laidian.repositories;

import com.miguan.laidian.entity.LdBuryingPointUser;
import com.miguan.laidian.redis.util.CacheConstant;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

public interface LdBuryingPointUserJpaRepository extends JpaRepository<LdBuryingPointUser, Long> {

    /**
     * 根据设备ID查询埋点用户表
     * param   deviceId   设备id
     **/
    @Cacheable(value = CacheConstant.LDBURYINGPOINT_USER, unless = "#result == null")
    @Query(value = "select * from ld_burying_point_user where device_id = ?1 and app_type =?2 ", nativeQuery = true)
    LdBuryingPointUser findUserBuryingPointIsNew(String deviceId, String AppType);

    @Transactional
    @Modifying
    @Query(value = "insert INTO ld_burying_point_user(device_id, app_type, create_time) VALUES (?1,?2,?3)", nativeQuery = true)
    int saveldBuryingPointUser(String deviceId, String appType, Date date);
}
