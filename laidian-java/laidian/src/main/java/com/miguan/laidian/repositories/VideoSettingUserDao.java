package com.miguan.laidian.repositories;

import com.miguan.laidian.entity.VideoSettingUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VideoSettingUserDao extends JpaRepository<VideoSettingUser, Long> {
    /**
     * 查询视频设置记录历史信息
     * @param userId
     * @param setType
     * @return
     */
    @Query(value = "select * from video_setting_user v where v.user_id = ?1 and v.set_type = ?2 order by updated_at desc limit 30", nativeQuery = true)
    List<VideoSettingUser> findVideoSettingUserInfo(Long userId, Integer setType);

    @Modifying
    @Query(value = "delete from video_setting_user where user_id = ?1 and video_id in (?2) and set_type = ?3", nativeQuery = true)
    int deleteVideoSettingUser(Long userId, List<Long> videoIds, Integer setType);

    @Query(value = "SELECT * from video_setting_user where user_id = ?1", nativeQuery = true)
    List<VideoSettingUser>  queryVideoSettingUserByUserId(Long userId);
}
