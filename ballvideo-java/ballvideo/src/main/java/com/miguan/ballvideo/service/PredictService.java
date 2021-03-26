package com.miguan.ballvideo.service;

import com.miguan.ballvideo.vo.video.FirstVideoParamsVo;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface PredictService {
    public Map<String, BigDecimal> predictPlayRate(List<Map<String,Object>> listFeature, boolean isABTest);

    List<String> getImmerseVideoIds(String catIds);

    /**
     * 获取大数据视频Id
     * @param paramsVo
     * @param type: 1-推荐，2-非推荐，3-详情列表
     * @return
     */
    String getRecommendVideoIds(FirstVideoParamsVo paramsVo, int type);

    /**
     * 查询用户激活日信息
     * @param packageName
     * @param distinctId
     * @return
     */
    String getActiveDate(String packageName, String distinctId);
}
