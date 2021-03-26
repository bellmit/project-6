package com.miguan.report.mapper;

import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description 同步数据使用的mapper
 * @Author zhangbinglin
 * @Date 2020/7/2 10:25
 **/
public interface SyncDataMapper {

    void batchInsertHourData(@Param("contents") List<String> contents);

    void deleteBannerDataTotalName(@Param("date")String date, @Param("platForm")Integer platForm);

    /**
     * 从banner_data汇总数据到banner_data_total_name
     * @param date
     * @param platForm
     */
    void insertBannerDataTotalName(@Param("date")String date, @Param("platForm")Integer platForm);
}
