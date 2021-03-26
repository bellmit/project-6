package com.miguan.recommend.service.recommend;

import com.miguan.recommend.bo.BaseDto;
import com.miguan.recommend.bo.PredictDto;
import com.miguan.recommend.entity.mongo.VideoHotspotVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PredictService {

    public Map<String, BigDecimal> getVideoListPlayRate(PredictDto paramDto, List<VideoHotspotVo> videoList);

    public Map<String, BigDecimal> getVideoListPlayRate(BaseDto baseDto, List<VideoHotspotVo> videoList);

    public Map<String, BigDecimal> predictPlayRate(List<Map<String, Object>> listFeature, boolean isABTest);

    public Map<String, BigDecimal> predictPlayRateCVR(List<Map<String, Object>> listFeature);

    public List<String> videoTopK(Map<String, BigDecimal> videoPlayRateMap, Map<String, Integer> videoCatMap, int needCount, int oneCatLimit, int limitMulti);

    public Map<Integer, List<String>> videoTopK(Map<String, BigDecimal> videoPlayRateMap, Map<String, Integer> videoCatMap);
}
