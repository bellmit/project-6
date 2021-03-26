package com.miguan.recommend.service.recommend.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.miguan.recommend.bo.BaseDto;
import com.miguan.recommend.bo.PredictDto;
import com.miguan.recommend.common.config.PredictConfig;
import com.miguan.recommend.common.util.FeatureUtil;
import com.miguan.recommend.common.util.HttpUtil;
import com.miguan.recommend.entity.mongo.VideoHotspotVo;
import com.miguan.recommend.service.recommend.PredictService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.MapUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.collections.CollectionUtils.isEmpty;

@Slf4j
@Service
public class PredictServiceImpl implements PredictService {

    @Resource
    private PredictConfig predictConfig;
    @Autowired
    private FeatureUtil featureUtil;

    @Override
    public Map<String, BigDecimal> getVideoListPlayRate(PredictDto paramDto, List<VideoHotspotVo> videoList) {
        List<Map<String, Object>> listFeature = featureUtil.makeFeatureList(paramDto, videoList);
        Map<String, BigDecimal> videoPlayRateMap = this.predictPlayRate(listFeature, false);
        return videoPlayRateMap;
    }

    /**
     * 获取列表的预估播放率
     */
    @Override
    public Map<String, BigDecimal> getVideoListPlayRate(BaseDto baseDto, List<VideoHotspotVo> videoList) {
        boolean isCvr = !"1".equals(baseDto.getCvrGroup());
        BigDecimal lambda = null;
        if("2".equals(baseDto.getCvrGroup())){
            lambda = new BigDecimal(1);
        }else if("3".equals(baseDto.getCvrGroup())){
            lambda = new BigDecimal(2);
        }else if("4".equals(baseDto.getCvrGroup())){
            lambda = new BigDecimal(4);
        }else if("5".equals(baseDto.getCvrGroup())){
            lambda = new BigDecimal(100);
        }
        //CTR AB实验 1为旧2为新
        boolean isAbTest = "2".equals(baseDto.getCtrGroup());
        long pt1 = System.currentTimeMillis();
        List<Map<String, Object>> listFeature = featureUtil.makeFeatureList(baseDto, videoList, isAbTest, isCvr);
//        log.debug("推荐 特征请求数据>>{}", JSONObject.toJSONString(listFeature));
        long pt2 = System.currentTimeMillis();
        log.info("推荐构造实时特征时长：" + (pt2 - pt1));
        Map<String, BigDecimal> videoPlayRateMap = this.predictPlayRate(listFeature, isAbTest);
        Map<String, BigDecimal> resultMap = videoPlayRateMap;
        if(isCvr){
            Map<String, BigDecimal> videoPlayRateMapCVR = this.predictPlayRateCVR(listFeature);
            if("5".equals(baseDto.getCvrGroup())){
                resultMap  = videoPlayRateMapCVR;
            }else{
                //进行公式相加
                for(String key:videoPlayRateMap.keySet()){
                    if(videoPlayRateMapCVR.get(key)!=null){
                        //CTR+lambda*CVR
                        resultMap.put(key,videoPlayRateMap.get(key).add(videoPlayRateMapCVR.get(key).multiply(lambda)));
                    }
                }
            }

        }
        long pt3 = System.currentTimeMillis();
        log.warn("推荐 预估播放率时长：{}", (pt3 - pt2));
        if (resultMap.size() != listFeature.size()) {
            return null;
        }
        return resultMap;
    }

    @Override
    public Map<String, BigDecimal> predictPlayRateCVR(List<Map<String, Object>> listFeature) {

        try {
            String jsonData = JSON.toJSONString(listFeature);
            String predictUrl = predictConfig.getPredictTranApi();
            String rs = HttpUtil.doPost(predictUrl, jsonData);
            return (Map<String, BigDecimal>) JSON.parse(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Map<String, BigDecimal> predictPlayRate(List<Map<String, Object>> listFeature, boolean isABTest) {

        try {
            String jsonData = JSON.toJSONString(listFeature);
            String predictUrl = predictConfig.getPredictApi();
            if (isABTest) {
                predictUrl = predictConfig.getPredictApi3();
            }
            String rs = HttpUtil.doPost(predictUrl, jsonData);
            return (Map<String, BigDecimal>) JSON.parse(rs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    public List<String> videoTopK(Map<String, BigDecimal> videoPlayRateMap, Map<String, Integer> videoCatMap, int needCount, int oneCatLimit, int limitMulti) {
        Map<String, BigDecimal> sortedMap = sortVideoMap(videoPlayRateMap, needCount, limitMulti);
        if (sortedMap.size() <= needCount) {
            return new ArrayList<>(sortedMap.keySet());
        }

        // 间隔下发
        String printLine = "";
        List<String> recVideoList = new ArrayList<>();
        Map<Integer, Integer> catVideoCount = new LinkedHashMap<>();
        for (String vid : sortedMap.keySet()) {
            int tmpCatId = videoCatMap.get(vid);
            int catCountInList = MapUtils.getIntValue(catVideoCount, tmpCatId, 0);
            if (catCountInList >= oneCatLimit) {
                continue;
            }
            recVideoList.add(vid);
            catVideoCount.put(tmpCatId, catCountInList + 1);
            printLine += vid + ":" + sortedMap.get(vid) + ",";
            if (recVideoList.size() >= needCount) {
                break;
            }
        }
        log.warn("排序后的视频列表：" + printLine);
        if (recVideoList.size() < needCount) {
            // 补充完整
            for (String vid : sortedMap.keySet()) {
                if (recVideoList.contains(vid)) {
                    continue;
                }
                recVideoList.add(vid);
                if (recVideoList.size() >= needCount) {
                    break;
                }
            }
        }

        return recVideoList;
    }

    @Override
    public Map<Integer, List<String>> videoTopK(Map<String, BigDecimal> videoPlayRateMap, Map<String, Integer> videoCatMap) {
        Map<String, BigDecimal> sortedMap = new LinkedHashMap<>();
        videoPlayRateMap.entrySet().stream()
                .sorted((p1, p2) -> p2.getValue().compareTo(p1.getValue()))
                .collect(Collectors.toList())
                .forEach(ele -> sortedMap.put(ele.getKey(), ele.getValue()));
        if (sortedMap.isEmpty()) {
            return new HashMap<Integer, List<String>>();
        }

        Map<Integer, List<String>> resultMap = new HashMap<Integer, List<String>>();
        for (String vid : sortedMap.keySet()) {
            Integer catid = videoCatMap.get(vid);

            List<String> catVideoList = resultMap.get(catid);
            if(isEmpty(catVideoList)){
                catVideoList = new ArrayList<>();
                catVideoList.add(vid);
                resultMap.put(catid, catVideoList);
            } else{
                catVideoList.add(vid);
            }
        }
        return resultMap;
    }

    public Map<String, BigDecimal> sortVideoMap(Map<String, BigDecimal> videoPlayRateMap, int needCount, int limitMulti) {
        Map<String, BigDecimal> sortedMap = new LinkedHashMap<>();
        videoPlayRateMap.entrySet().stream()
                .sorted((p1, p2) -> p2.getValue().compareTo(p1.getValue()))
                .limit(needCount * limitMulti)
                .collect(Collectors.toList())
                .forEach(ele -> sortedMap.put(ele.getKey(), ele.getValue()));
        return sortedMap;
    }

}
