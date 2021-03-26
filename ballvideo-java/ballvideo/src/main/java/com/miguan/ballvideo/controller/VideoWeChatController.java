package com.miguan.ballvideo.controller;

import com.miguan.ballvideo.common.aop.AbTestAdvParams;
import com.miguan.ballvideo.common.constants.Constant;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import com.miguan.ballvideo.dto.VideoParamsDto;
import com.miguan.ballvideo.dto.VideoWeChatParamsDto;
import com.miguan.ballvideo.service.FirstVideosService;
import com.miguan.ballvideo.vo.video.FirstVideos161Vo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * 小程序视频Controller
 *
 * @Author laiyd
 * @Date 2020/8/25
 **/
@Slf4j
@Api(value = "小程序首页视频", tags = {"小程序首页视频"})
@RestController
public class VideoWeChatController {

    @Resource
    private FirstVideosService firstVideosService;

    @ApiOperation(value = "小程序首页视频推荐列表")
    @PostMapping("/api/video/firstRecommendVideosList")
    public FirstVideos161Vo firstRecommendVideosList(@Validated VideoWeChatParamsDto dto,
                                                     @AbTestAdvParams AbTestAdvParamsVo queueVo) {
        dto.setMobileType(Constant.WeChat);
        dto.setAppVersion(Constant.APPVERSION_253);
        if (StringUtils.isEmpty(dto.getAppPackage())) {
            dto.setAppPackage(Constant.appPackageWeChat);
        }
        if (StringUtils.isEmpty(dto.getChannelId())) {
            dto.setChannelId(Constant.channelIdWeChat);
        }
        dto.setDeviceId(dto.getOpenId());
        VideoParamsDto videoParamsDto = new VideoParamsDto();
        BeanUtils.copyProperties(dto,videoParamsDto);
        FirstVideos161Vo firstVideos161Vo = firstVideosService.firstRecommendVideosList18(videoParamsDto, queueVo);
        return firstVideos161Vo;
    }

}
