package com.miguan.bigdata.mapper;


import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.bigdata.dto.EarlyWarningDto;
import com.miguan.bigdata.vo.DspPlanVo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 获取广告行为数据mapper
 */
public interface AdDataMapper {

    /**
     * 查询出小于广告配置代码位自动排序阀值的代码位
     * @param map
     * @return
     */
    List<String> listAdIdShowThreshold(Map<String, Object> map);

    List<EarlyWarningDto> findEarlyWarnList(Map<String, Object> params);

    /**
     * 查询DSP广告计划的有效点击数，有效曝光数等指标数据
     * @return
     */
    List<DspPlanVo> findDspPlanList(@Param("startDay") Integer startDay, @Param("endDay") Integer endDay);

    /**
     * 查询DSP广告计划的有效点击数，有效曝光数等指标数据
     * @return
     */
    BigDecimal findDspPlanConsumption(@Param("startDay") String startDay, @Param("endDay") String endDay, @Param("planId") String planId);

    /**
     * 删除ballvideoadv库ad_multi_dimensional_data指定日期数据，或历史数据
     * @param params
     */
    @DS("advert")
    void deleteAdMultiDimensionalData(Map<String, Object> params);

    @DS("advert")
    void optimizeMulti();

    /**
     * 从ck统计数据同步到allvideoadv库ad_multi_dimensional_data的表
     * @param params
     */
    void syncAdMultiDimensionalData(Map<String, Object> params);
}
