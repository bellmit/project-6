package com.miguan.bigdata.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 获取广告行为数据service
 */
public interface AdDataService {

    /**
     * 查询出广告配置代码位自动排序阀值的代码位
     * @param dd 日期，格式：yyyy-MM-dd
     * @param showThreshold 展现量阀值
     * @param type 类型，1:未达到阀值，2：已达到阀值
     * @param adIds 代码位id
     * @return
     */
    List<String> listAdIdShowThreshold(String dd, Integer showThreshold, Integer type, String adIds);

    /**
     * 钉钉-广告展示/广告库存比值预警
     * @Param warnType 类型：0-每半小时预警，1-每天10点预警前24小时
     * @return
     */
    String findEarlyWarnList(int warnType);

    /**
     * 从大数据 同步数据到dsp的idea_advert_report表（存储计划的有效曝光数，有效点击数等指标）
     * @param startDay 开始日期，格式：yyyyMMdd
     * @param endDay 结束日期, 格式：yyyyMMdd
     * @return
     */
    void syncDspPlan(Integer startDay, Integer endDay);

    BigDecimal findPlanConsumption(Long planId);

    /**
     * 从ck统计数据同步到allvideoadv库ad_multi_dimensional_data的表
     * @param dd 统计日期，格式：yyyy-MM-dd
     */
    void syncAdMultiDimensionalData(String dd);
}
