package com.miguan.ballvideo.service;

import com.miguan.ballvideo.entity.recommend.UserFeature;
import com.miguan.ballvideo.vo.video.FirstVideos161Vo;

import java.util.List;

public interface FeatureService {

    public void saveFeatureToRedis(UserFeature userFeature, FirstVideos161Vo videoVo);
}
