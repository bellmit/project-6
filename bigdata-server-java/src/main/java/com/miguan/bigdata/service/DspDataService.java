package com.miguan.bigdata.service;

import java.util.LinkedHashMap;
import java.util.List;

/**
 * DSP系统数据service
 */
public interface DspDataService {
    /**
     * 统计近7天每个时间段的日活数占比
     * @return
     */
    LinkedHashMap<String, Double> getUserRatio();
}
