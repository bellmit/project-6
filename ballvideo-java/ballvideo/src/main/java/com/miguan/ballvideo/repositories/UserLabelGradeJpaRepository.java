package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.UserLabelGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserLabelGradeJpaRepository extends JpaRepository<UserLabelGrade, Long> {

    @Query(value = "select * from user_label_grade where device_id=?1 and cat_id=?2 limit 1", nativeQuery = true)
    UserLabelGrade getUserLabelGrade(String deviceId, Long catId);

    @Query(value = "select * from user_label_grade where device_id=?1 order by cat_grade desc",nativeQuery = true)
    List<UserLabelGrade> getUserLabelGradeList(String deviceId);

    @Transactional
    @Modifying
    @Query(value = "delete from user_label_grade where device_id='4f13322e9e48d4e0'",nativeQuery = true)
    int deleteUserInfo();
}
