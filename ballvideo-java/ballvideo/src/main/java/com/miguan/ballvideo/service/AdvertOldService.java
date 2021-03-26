package com.miguan.ballvideo.service;


import com.miguan.ballvideo.vo.AdvertVo;

import java.util.List;
import java.util.Map;

/**
 * 广告Service
 * @author laiyudan
 * @date 2019-09-09
 */
public interface AdvertOldService {

    /**
     * 查询广告类型
     * @param param
     * @return
     */
    List<AdvertVo> queryAdertList(Map<String, Object> param);

    public AdvertVo queryOneByRandom(Map<String, Object> param);

    public List<AdvertVo> queryByRandom(Map<String, Object> param, int num);

    List<AdvertVo> queryAdertListGame(Map<String, Object> param);

    public List<AdvertVo> queryVideoByRandom(Map<String, Object> param, int num);

    /**
     * 查询锁屏广告
     *
     * @param param
     * @return
     */
    Map<String,Object> lockScreenAdertList(Map<String, Object> param);

    Map<String, List<AdvertVo>> getAdversByPositionTypes(Map<String, Object> param);

    /**
     *  根据渠道查询广告
     *  1 如果为空，查询默认渠道广告；
     *  2 如果多个广告位置，循环1的判断逻辑，返回合集；
     * @param list
     * @param param
     * @return
     */
    List<AdvertVo> getAdvertsByChannel(List<AdvertVo> list, Map<String, Object> param);

    List<AdvertVo> getAdvertsByLadder(Map<String, Object> param);

    List<AdvertVo> getAdvertsBySection(Map<String, Object> param);

    List<AdvertVo> getBaseAdverts(Map<String, Object> param);

    List<AdvertVo> bannerInfo(Map<String, Object> param);

}
