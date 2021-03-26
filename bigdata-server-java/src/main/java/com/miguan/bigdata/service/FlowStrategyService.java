package com.miguan.bigdata.service;

import java.util.List;

/**
 * 流量策略需要的数据service
 */
public interface FlowStrategyService {
    /**
     * 统计出需要入库的激励视频
     * @param limtNum 每个分类每日入库数
     * @return
     */
    List<Long> staInIncentiveVideos(Integer limtNum);

    List<Long> staOutIncentiveVideos(int retainNum);
}
