package com.miguan.ballvideo.service;

import com.miguan.ballvideo.dto.VideoFeedbackDto;

/**
 * 视频反馈service
 */
public interface VideoFeedbackService {

    /**
     * 保存视频反馈信息
     * @param videoFeedbackDto
     */
    void saveVideoFeedback(VideoFeedbackDto videoFeedbackDto);
}
