package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.VideoExposureTotalCount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface VideoExposureTotalCountDao extends JpaRepository<VideoExposureTotalCount,Long> {

    @Query(value = "select * from video_exposure_total_count where video_id in(?1) ", nativeQuery = true)
    List<VideoExposureTotalCount> findByVideoId(List<Long> ids);

    @Transactional
    @Modifying
    @Query(value = "update video_exposure_total_count r set r.total_count = IFNULL(r.total_count, 0) + ?1 where r.video_id in (?2)", nativeQuery = true)
    int updateVideosTotalCntBatch(Long updateNum, List<Long> videoIds);

}
