package com.miguan.ballvideo.service;

import com.miguan.ballvideo.dto.VideoParamsDto;
import com.miguan.ballvideo.vo.video.Videos161Vo;

import java.util.List;

public interface FeatureParamService {

    public void snapshotToRedis(VideoParamsDto videoParamsDto, List<Videos161Vo> videoList);
}
