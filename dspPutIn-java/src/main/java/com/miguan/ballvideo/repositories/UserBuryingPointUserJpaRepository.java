package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.UserBuryingPointUser;
import com.miguan.ballvideo.redis.util.CacheConstant;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;


public interface UserBuryingPointUserJpaRepository extends JpaRepository<UserBuryingPointUser, Long> {

    /**
     * 根据设备ID查询埋点用户表（新老用户判断，缓存保存到当天24点 add shixh0306）
     * param   deviceId   设备id
     **/
    @Cacheable(value = CacheConstant.BURYINGPOINT_USER, unless = "#result == null")
    @Query(value = "select create_time from xy_burying_point_user where device_id = ?1 ", nativeQuery = true)
    Date findUserBuryingPointIsNew(String deviceId);

    /**
     * 根据设备ID删除数据
     * param   deviceId   设备id
     **/
    @Transactional
    int deleteByDeviceId(String deviceId);
}
