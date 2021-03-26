package com.miguan.ballvideo.service;

import com.miguan.ballvideo.vo.mongodb.IncentiveVideoHotspot;

import java.util.List;

public interface FindJLVideosService {

    List<IncentiveVideoHotspot> getVideoId(String deviceId, String showedVideoIds, int catid, int count, List<Integer> excludeCatids, List<Integer> excludeCollectids);
    List<IncentiveVideoHotspot> getVideoId(String showedVideoIds, int catid, int count, List<Integer> excludeCatids, List<Integer> excludeCollectids);
}
