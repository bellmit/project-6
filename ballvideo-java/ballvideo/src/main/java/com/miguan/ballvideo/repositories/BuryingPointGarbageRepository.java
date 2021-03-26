package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.UserBuryingPointGarbage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BuryingPointGarbageRepository extends JpaRepository<UserBuryingPointGarbage, Long> {

    /**
     * 根据设备表id查询埋点信息
     * @param deviceId 备表id
     * @return
     */
    UserBuryingPointGarbage findFirstByDeviceId(Long deviceId);

    @Query(value = "select * from xy_burying_point_garbage where TO_DAYS(updated_at) = TO_DAYS(NOW()) - 2 or TO_DAYS(updated_at) = TO_DAYS(NOW()) - 5  or entry_num = 0 limit ?1, 5000",nativeQuery = true)
    List<UserBuryingPointGarbage> findDeviceIdInfo(Integer index);

    @Query(value = "select count(device_id) from xy_burying_point_garbage where TO_DAYS(updated_at) = TO_DAYS(NOW()) - 2 or TO_DAYS(updated_at) = TO_DAYS(NOW()) - 5 or entry_num = 0",nativeQuery = true)
    Integer findDeviceIdCount();
}

