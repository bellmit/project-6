package com.miguan.reportview.service;

import com.miguan.reportview.dto.VideoLeaderboarDto;

import java.util.List;
import java.util.Map;

public interface IVideoViewService {

    public int countData(Map<String, Object> params);

    public List<Map<String, Object>> selectVideoViewData(Map<String, Object> params);

    //public List<Map<String, Object>> findViewLeaderboard(Map<String, Object> params);
    public List<VideoLeaderboarDto> findViewLeaderboard(Map<String, Object> params);

    public Map<String, Object> findVideoView(Integer videoId);

    public int insertBatch(List<Map<String, Object>> dataList);

    public int deleteData(Map<String, Object> params);
}
