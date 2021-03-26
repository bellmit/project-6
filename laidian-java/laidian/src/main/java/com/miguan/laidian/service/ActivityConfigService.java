package com.miguan.laidian.service;

import com.miguan.laidian.entity.ActActivityConfig;

import java.util.List;

/**
 * @author chenwf
 * @date 2020/5/22
 */
public interface ActivityConfigService {
    /**
     * 根据活动id获取奖品配置信息
     * @param activityId
     * @return
     */
    List<ActActivityConfig> getActConfigByActId(Long activityId);
}
