package com.miguan.flow.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;

import java.util.Map;

/**
 * @Description 视频操作
 **/
@DS("ballvideo")
public interface FirstVideosMapper {

    /**
     * 查询激励视频的数量
     * @return
     */
    int countIncentiveVideo();

    /**
     * 更新激励视频状态
     * @param params
     * @return
     */
    int updateBatchIncentive(Map<String, Object> params);
}

