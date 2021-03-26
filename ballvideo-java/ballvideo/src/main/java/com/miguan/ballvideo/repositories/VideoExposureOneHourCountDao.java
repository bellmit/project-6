package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.VideoExposureOneHourCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface VideoExposureOneHourCountDao extends JpaRepository<VideoExposureOneHourCount,Long> {

    /**
     * 删除1小时前的数据
     * @return
     */
    @Modifying
    @Query(value = "delete from video_exposure_one_hour_count where create_time < unix_timestamp(date_sub(now(), interval 1 hour))", nativeQuery = true)
    int delVideosExposureCnt();

}
