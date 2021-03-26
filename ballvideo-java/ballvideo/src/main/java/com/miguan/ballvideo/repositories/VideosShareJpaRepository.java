package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.VideosShare;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

public interface VideosShareJpaRepository extends JpaRepository<VideosShare,Long> {

    VideosShare findByVideoIdAndShareType(Long videoId, Integer shareType);

    @Modifying
    @Query(value = "update videos_share set share_count = IFNULL(share_count, 0) + 1,updated_at = now() where video_id = ?1 and share_type = ?2", nativeQuery = true)
    int updateVideosShareCnt(Long videoId, Integer shareType);
}
