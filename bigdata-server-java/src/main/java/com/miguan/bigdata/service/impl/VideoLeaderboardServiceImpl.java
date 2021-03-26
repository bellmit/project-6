package com.miguan.bigdata.service.impl;

import com.miguan.bigdata.common.util.DateUtil;
import com.miguan.bigdata.dto.VideoLeaderboarDto;
import com.miguan.bigdata.mapper.VideoLeaderboardMapper;
import com.miguan.bigdata.service.IVideoLeaderboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class VideoLeaderboardServiceImpl implements IVideoLeaderboardService {
    @Resource
    private VideoLeaderboardMapper videoLeaderboardMapper;


    @Override
    public List<VideoLeaderboarDto> findViewLeaderboard(Map<String, Object> params) {

        List<Map<String, Object>> searchList = videoLeaderboardMapper.findViewLeaderboard(params);
        List<VideoLeaderboarDto> resultList = new ArrayList<>();

        searchList.stream().forEach(t -> {
            VideoLeaderboarDto dto = new VideoLeaderboarDto();
            dto.setVideo_id((Integer)t.get("video_id"));
            dto.setNumber_of_views((Integer)t.get("number_of_views"));
            resultList.add(dto);
        });
        return resultList;
    }

    @Override
    public Map<String, Object> findVideoView(Integer videoId) {
        Map<String, Object> dayParam = new HashMap<String, Object>();
        dayParam.put("dd", Integer.parseInt(DateUtil.yesyyyyMMdd()));
        dayParam.put("type", 2);
        dayParam.put("videoId", videoId);
        Map<String, Object> dayView = videoLeaderboardMapper.findViewNumberByVideoId(dayParam);
        int dayNumber = 0;
        if (dayView != null) {
            dayNumber = (Integer) dayView.getOrDefault("number", 0);
        }

        Map<String, Object> hourParam = new HashMap<String, Object>();
        hourParam.put("dh", Integer.parseInt(LocalDateTime.now().minusHours(1).format(DateUtil.YYYYMMDDHH_FORMATTER)));
        hourParam.put("type", 1);
        hourParam.put("videoId", videoId);
        Map<String, Object> hourView = videoLeaderboardMapper.findViewNumberByVideoId(hourParam);

        int hourNumber = 0;
        if (hourView != null) {
            hourNumber = (Integer) hourView.getOrDefault("number", 0);
        }

        Map<String, Object> resultMap = new HashMap<String, Object>();
        resultMap.put("video_id", videoId);
        resultMap.put("hour_number", hourNumber);
        resultMap.put("day_number", dayNumber);
        return resultMap;
    }

}
