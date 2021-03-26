package com.miguan.laidian.repositories;

import com.miguan.laidian.entity.VideoSettingPhone;
import com.miguan.laidian.redis.util.CacheConstant;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface VideoSettingPhoneDao extends JpaRepository<VideoSettingPhone, Long> {

    List<VideoSettingPhone> findAllByUserIdAndPhoneOrderByUpdateDateAsc(Long valueOf, String phone);

    @Cacheable(value = CacheConstant.USER_PHONE_VIDEO_CACHE, unless = "#result == null")
    List<VideoSettingPhone> findAllByUserIdAndPhoneOrderByUpdateDateDesc(Long valueOf, String phone);

    @Modifying
    @Query(value = "delete from video_setting_phone where user_id = ?1 and video_id in (?2)  and phone = ?3", nativeQuery = true)
    int deleteVideoSettingPhone(Long userId, List<Long> videoIds, String phone);

    @Query(value = "SELECT * from video_setting_phone where user_id = ?1", nativeQuery = true)
    List<VideoSettingPhone> queryVideoSettingPhoneByUserId(Long userId);
}
