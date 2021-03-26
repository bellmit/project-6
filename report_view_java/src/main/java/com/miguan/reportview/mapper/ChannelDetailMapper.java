package com.miguan.reportview.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.github.pagehelper.Page;
import com.miguan.reportview.dto.ChannelDetailDto;
import com.miguan.reportview.dto.ChannelRoiEstimateDto;
import com.miguan.reportview.dto.ChannelRoiPrognosisDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * 渠道明细
 */
public interface ChannelDetailMapper {

    /**
     * 聚合视频用户渠道明细数据
     * @param day 日期，格式：yyyy-MM-dd
     */
    @DS("clickhouse")
    void batchSaveChannelDetail(@Param("day") String day);

    /**
     * 删除指定日期的channel_detail表的数据
     * @param day
     */
    @DS("clickhouse")
    void deleteChannelDetail(@Param("day") String day);

    /**
     * 查询渠道明细数据
     * @param params
     * @return
     */
    @DS("clickhouse")
    Page<ChannelDetailDto> listChannelDetail(Map<String, Object> params);


    /**
     * 渠道ROI评估
     **/
    List<ChannelRoiEstimateDto> getRoiEstimate(Map<String, Object> params);

    /**
     * 渠道ROI评估-条数（分页用）
     **/
    int getRoiEstimateCount(Map<String, Object> params);

    /**
     * 渠道ROI评估-获取最大更新时间
     **/
    String getRoiEstimateDate();

    /**
     * 渠道ROI预测
     **/
    List<ChannelRoiPrognosisDto> getRoiPrognosis(Map<String, Object> params);

    /**
     * 渠道ROI预测-条数（分页用）
     **/
    int getRoiPrognosisCount(Map<String, Object> params);

    /**
     * 渠道ROI预测-获取最大更新时间
     **/
     String getRoiPrognosisDate();


}
