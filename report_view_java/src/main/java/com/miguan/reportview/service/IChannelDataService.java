package com.miguan.reportview.service;

import com.github.pagehelper.PageInfo;
import com.miguan.reportview.dto.*;
import com.miguan.reportview.vo.AdClickNumVo;

import java.util.List;
import java.util.Map;

/**
 * @author zhongli
 * @date 2020-08-05
 */
public interface IChannelDataService {

    List<ChannelDataDto> getData(Boolean isNew,
                                 List<String> appPackages,
                                 List<String> appVersions,
                                 List<String> pChannelIds,
                                 List<String> channelIds,
                                 List<String> groups,
                                 String startDate,
                                 String endDate);

    List<LdChannelDataDto> getLdData(List<String> appVersions,
                                     Boolean isNewApp,
                                     List<String> pChannelIds,
                                     List<String> channelIds,
                                     List<String> groups,
                                     String startDate,
                                     String endDate);

    /**
     * 视频渠道明细数据
     *
     * @param appPackages  应用
     * @param appVersions  版本
     * @param channelIds   子渠道
     * @param orderByField 排序字段
     * @param startDate    开始时间
     * @param endDate      结束时间
     * @param pageNum 页码
     * @param pageSize 每页记录数
     * @param startRow 分页起始记录数
     * @param endRow 分页结束记录数(startRow和endRow参数，只能与pageNum和pageSize两参数任选一种)
     * @return
     */
    PageInfo<ChannelDetailDto> listChannelDetail(List<String> appPackages, List<String> appVersions, List<String> channelIds, String orderByField,
                                                 String startDate, String endDate, Integer pageNum, Integer pageSize,Integer startRow, Integer endRow);



    /**
     * 渠道ROI评估
     **/
    List<ChannelRoiEstimateDto> getRoiEstimate(Map<String, Object> params);

    /**
     * 渠道ROI预测
     **/
    List<ChannelRoiPrognosisDto> getRoiPrognosis(Map<String, Object> params);

    /**
     * 渠道ROI评估-条数（分页用）
     **/
    int getRoiEstimateCount(Map<String, Object> params);

    /**
     * 渠道ROI预测-条数（分页用）
     **/
    int getRoiPrognosisCount(Map<String, Object> params);

    /**
     * 渠道ROI评估-获取最大更新时间
     **/
    String getRoiEstimateDate();

    /**
     * 渠道ROI预测-获取最大更新时间
     **/
    String getRoiPrognosisDate();

    /**
     * 统计渠道每小时的人均点击数
     * @param type  1--根据子渠道汇总，2--根据父渠道汇总
     * @param date  日期，格式：yyyy-MM-dd
     * @param channelList  渠道列表
     */
    List<AdClickNumVo> staChannelPreAdClick(int type, String date, List<String> channelList);

}
