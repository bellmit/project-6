package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.VideosReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface VideoReportDao extends JpaRepository<VideosReport,Long> {

    VideosReport findByVideoIdAndVideoType(Long videoId, Integer videoType);

    @Modifying
    @Query(value = "update videos_report r set r.report_count = IFNULL(r.report_count, 0) + 1,r.updated_at = now() where r.video_id = ?1 and r.video_type = ?2", nativeQuery = true)
    int updateVideosReportCnt(Long videoId, Integer videoType);

    @Modifying
    @Query(value = "update videos_report r set r.watch_count = IFNULL(r.watch_count, 0) + 1,r.updated_at = now() where r.video_id = ?1 and r.video_type = ?2", nativeQuery = true)
    int updateVideosWatchCnt(Long videoId, Integer videoType);

    @Query(value = "select * from videos_report where video_id in(?1) and video_type = 10", nativeQuery = true)
    List<VideosReport> findByVideoId(List<Long> ids);

    @Transactional
    @Modifying
    @Query(value = "update videos_report r set r.watch_count = IFNULL(r.watch_count, 0) + ?1,r.updated_at = now() where r.video_type = 10 and r.video_id in (?2)", nativeQuery = true)
    int updateVideosWatchCntBatch(Long updateNum, List<Long> videoIds);
}
