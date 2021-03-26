package com.miguan.ballvideo.service.impl;

import com.miguan.ballvideo.dto.VideoFeedbackDto;
import com.miguan.ballvideo.entity.VideoFeedback;
import com.miguan.ballvideo.repositories.VideoFeedbackRepository;
import com.miguan.ballvideo.service.VideoFeedbackService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Date;

/**
 * 视频反馈ServiceImpl
 */
@Slf4j
@Service
public class VideoFeedbackServiceImpl implements VideoFeedbackService {

    @Resource
    private VideoFeedbackRepository videoFeedbackRepository;

    /**
     * 保存视频反馈信息
     * @param videoFeedbackDto
     */
    public void saveVideoFeedback(VideoFeedbackDto videoFeedbackDto) {
        VideoFeedback videoFeedback = new VideoFeedback();
        BeanUtils.copyProperties(videoFeedbackDto, videoFeedback);
        videoFeedback.setCreateTime(System.currentTimeMillis()/1000);
        videoFeedback.setUpdateTime(System.currentTimeMillis()/1000);
        videoFeedback.setFeedbackDate(new Date());  //反馈时间
        videoFeedbackRepository.save(videoFeedback);
    }

}
