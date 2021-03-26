package com.miguan.ballvideo.service;

import com.miguan.ballvideo.common.interceptor.argument.params.CommonParamsVo;
import com.miguan.ballvideo.vo.HotListConfVo;
import com.miguan.ballvideo.vo.video.HotListVo;

import java.util.List;

public interface TopRankingService {

    /**
     * 热榜数据存储到redis
     *
     * @param type
     */
    void setHotListToRedis(Integer type);

    /**
     * 获取榜单banner配置
     *
     * @return
     */
    HotListConfVo getHotListConf();

    /**
     * 查询热榜前三条数据
     *
     * @return
     */
    List<HotListVo> queryTopThreeHotList(CommonParamsVo params);

    /**
     * 根据热榜类型查询对应热榜数据
     *
     * @param params
     * @param type
     * @return
     */
    List<HotListVo>  queryHotListByType(CommonParamsVo params,Integer type);
}
