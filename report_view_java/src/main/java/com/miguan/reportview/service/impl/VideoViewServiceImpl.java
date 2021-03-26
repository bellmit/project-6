package com.miguan.reportview.service.impl;

import com.miguan.reportview.common.utils.DateUtil;
import com.miguan.reportview.dto.VideoLeaderboarDto;
import com.miguan.reportview.mapper.DwVideoActionsAggregationMapper;
import com.miguan.reportview.mapper.VideoLeaderboardMapper;
import com.miguan.reportview.service.IVideoViewService;
import com.miguan.reportview.vo.VideoHotspotVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.MapUtils;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class VideoViewServiceImpl implements IVideoViewService {
    @Resource
    private DwVideoActionsAggregationMapper dwVideoActionsAggregationMapper;
    @Resource
    private VideoLeaderboardMapper videoLeaderboardMapper;
    @Resource(name = "logMongoTemplate")
    private MongoTemplate mongoTemplate;

    @Override
    public int countData(Map<String, Object> params) {
        return dwVideoActionsAggregationMapper.countOfData(params);
    }

    @Override
    public List<Map<String, Object>> selectVideoViewData(Map<String, Object> params) {
        log.info("selectVideoViewData>>{}", params);
        return dwVideoActionsAggregationMapper.getData(params);
    }

    @Override
    public List<VideoLeaderboarDto> findViewLeaderboard(Map<String, Object> params) {
//    public List<Map<String, Object>> findViewLeaderboard(Map<String, Object> params) {

        List<Map<String, Object>>  searchList = videoLeaderboardMapper.findViewLeaderboard(params);
        List<VideoLeaderboarDto> resultList = new ArrayList<>();

        searchList.stream().forEach(t -> {
            VideoLeaderboarDto dto = new VideoLeaderboarDto();
            dto.setVideo_id(MapUtils.getLongValue(t, "video_id"));
            dto.setNumber_of_views(MapUtils.getLongValue(t, "number_of_views"));
            resultList.add(dto);
        });

        // 如果返回的数据不足200条，从mongo按播放率倒序补足
        final int returnSize = 200;
        if(resultList.size() < returnSize){

            Query query = new Query();
            query.with(Sort.by(Sort.Order.desc("weights")));
            query.limit(returnSize);
            List<VideoHotspotVo> list = mongoTemplate.find(query, VideoHotspotVo.class, "video_hotspot");

            List<VideoLeaderboarDto> mongoList = new ArrayList<VideoLeaderboarDto>();
            list.stream().forEach(m -> {
                VideoLeaderboarDto dto = new VideoLeaderboarDto();
                dto.setVideo_id(Long.valueOf(m.getVideo_id()));
                dto.setNumber_of_views(1L);
                mongoList.add(dto);
            });

            mongoList.removeAll(resultList);

            int addSize = returnSize - resultList.size();
            while(mongoList.size() > addSize){
                mongoList.remove(mongoList.size()-1);
            }
            resultList.addAll(mongoList);
        }
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

    @Override
    public int insertBatch(List<Map<String, Object>> dataList) {
        return videoLeaderboardMapper.insertBatch(dataList);
    }

    @Override
    public int deleteData(Map<String, Object> params) {
        return videoLeaderboardMapper.deleteData(params);
    }
}
