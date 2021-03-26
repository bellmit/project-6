package com.miguan.ballvideo.repositories;

import com.miguan.ballvideo.entity.BsyLog;
import com.miguan.ballvideo.entity.VideoFeedback;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VideoFeedbackRepository extends JpaRepository<VideoFeedback, Long> {

}
