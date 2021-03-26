package com.miguan.bigdata.service;

import com.miguan.bigdata.dto.VideoLeaderboarDto;

import java.util.List;
import java.util.Map;

public interface IVideoLeaderboardService {

    public List<VideoLeaderboarDto> findViewLeaderboard(Map<String, Object> params);

    public Map<String, Object> findVideoView(Integer videoId);

}
