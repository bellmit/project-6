package com.miguan.advert.domain.service;

import com.miguan.advert.domain.vo.result.AdvertCodeVo;
import java.util.List;
import java.util.Map;

/**
 * 流量变现平台Service
 * @author suhj
 * @date 2020-08-04
 */
public interface AdvertFlowService {

    /**
     * 查询广告信息
     * @param param
     * @return
     */
    List<AdvertCodeVo> commonSearch(Map<String, Object> param);

    /**
     * 判断有效性:appId,secretkey,poskey
     * @param param
     * @return
     */
    Map<String, Integer> judgeValidity(Map<String, Object> param);
}
