package com.miguan.ballvideo.controller;

import com.miguan.ballvideo.common.enums.FeedBackDetailEnum;
import com.miguan.ballvideo.common.enums.FeedBackTypeEnum;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.dto.VideoFeedbackDto;
import com.miguan.ballvideo.dto.VideoFeedbackItemDto;
import com.miguan.ballvideo.service.VideoFeedbackService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(value="视频反馈接口",tags={"视频反馈接口"})
@RestController
@RequestMapping("/api/video/feedback")
public class VideoFeedbackController {

    @Resource
    private VideoFeedbackService videoFeedbackService;

    @ApiOperation("查询视频反馈类型和明细的具体项")
    @PostMapping("/getFeedbackItems")
    public ResultMap<VideoFeedbackItemDto> getFeedbackItems() {
        VideoFeedbackItemDto videoFeedbackItemDto = new VideoFeedbackItemDto();
        videoFeedbackItemDto.setFeedBackType(FeedBackTypeEnum.getList());
        videoFeedbackItemDto.setFeedBackDetail(FeedBackDetailEnum.getList());
        return ResultMap.success(videoFeedbackItemDto);
    }

    @ApiOperation("提交视频反馈信息")
    @PostMapping("/commitVideoFeedback")
    public ResultMap commitVideoFeedback(VideoFeedbackDto videoFeedbackDto) {
        this.videoFeedbackService.saveVideoFeedback(videoFeedbackDto);
        return ResultMap.success();
    }
}
