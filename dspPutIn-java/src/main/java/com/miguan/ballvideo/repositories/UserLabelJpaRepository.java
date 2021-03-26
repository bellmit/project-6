package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.UserLabel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;


public interface UserLabelJpaRepository extends JpaRepository<UserLabel, Long> {

    UserLabel findTopByDeviceId(String deviceId);

    @Modifying
    @Query(value = "update user_label com set com.cat_id1 = ?1,com.cat_id2 = ?2 where com.device_id = ?3",nativeQuery = true)
    UserLabel updateUserLabel(Long catId1, Long catId2, String deviceId);

    @Query(value = "select * from user_label com where com.cat_ids_sort is null",nativeQuery = true)
    List<UserLabel> getUserLabelinfo();
}
