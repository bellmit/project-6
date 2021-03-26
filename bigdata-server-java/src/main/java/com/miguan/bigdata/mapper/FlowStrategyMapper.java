package com.miguan.bigdata.mapper;


import com.miguan.bigdata.vo.IncentiveVideoVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 流量策略相关mapper
 */
public interface FlowStrategyMapper {

    /**
     * 统计入库的激励视频基础数据
     * @param startDay 开始时间，格式：yyyy-MM-dd
     * @param endDay 结束时间，格式：yyyy-MM-dd
     * @return
     */
    List<IncentiveVideoVo> staInIncentiveVideos(@Param("unInVideoList")List<Long> unInVideoList, @Param("startDay") Integer startDay, @Param("endDay") Integer endDay);

    /**
     * 统计出库库的激励视频基础数据
     * @param startDay
     * @param endDay
     * @return
     */
    List<IncentiveVideoVo> staOutIncentiveVideos(@Param("startDay") Integer startDay, @Param("endDay") Integer endDay);

    List<IncentiveVideoVo> getIncentiveVideoList();
}
