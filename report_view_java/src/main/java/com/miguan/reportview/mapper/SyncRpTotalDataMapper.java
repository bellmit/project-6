package com.miguan.reportview.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.reportview.entity.RpTotalDay;
import com.miguan.reportview.entity.RpTotalHour;
import com.miguan.reportview.entity.RpTotalMinute;
import com.miguan.reportview.vo.*;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 全局汇总表 Mapper 接口
 * </p>
 *
 * @author zhongli
 * @since 2020-08-04
 */
public interface SyncRpTotalDataMapper{

    /**
     * 查询时间段内的rp_total_hour表信息(从ClickHouse查询)
     * @return
     */
    @DS("clickhouse")
    List<RpTotalHour> listTotalHour(Map<String, Object> params);

    /**
     * 删除rp_total_hour的数据（mysql）
     * @param startDh
     * @param endDh
     */
    void deleteTotalHour(@Param("startDh") int startDh, @Param("endDh") int endDh);

    /**
     * 批量保存rp_total_hour的数据到mysql中
     * @param lists
     */
    void batchSaveTotalHour(@Param("lists") List<RpTotalHour> lists);

    /**
     * 查询时间段内的rp_total_day表信息(从ClickHouse查询)
     * @return
     */
    @DS("clickhouse")
    List<RpTotalDay> listTotalDay(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 删除rp_total_day的数据（mysql）
     * @param startDate
     * @param endDate
     */
    void deleteTotalDay(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 批量保存rp_total_hour的数据到mysql中
     * @param lists
     */
    void batchSaveTotalDay(@Param("lists") List<RpTotalDay> lists);

    /**
     * 查询时间段内的ld_rp_total_hour表信息(从ClickHouse查询)
     * @return
     */
    @DS("clickhouse")
    List<LdRpTotalHour> listLdTotalHour(@Param("startDh") int startDh, @Param("endDh") int endDh);

    /**
     * 删除ld_rp_total_hour的数据（mysql）
     * @param startDh
     * @param endDh
     */
    void deleteLdTotalHour(@Param("startDh") int startDh, @Param("endDh") int endDh);

    /**
     * 批量保存ld_rp_total_hour的数据到mysql中
     * @param lists
     */
    void batchSaveLdTotalHour(@Param("lists") List<LdRpTotalHour> lists);

    /**
     * 查询时间段内的ld_rp_total_day表信息(从ClickHouse查询)
     * @return
     */
    @DS("clickhouse")
    List<LdRpTotalDay> listLdTotalDay(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 删除ld_rp_total_day的数据（mysql）
     * @param startDate
     * @param endDate
     */
    void deleteLdTotalDay(@Param("startDate") String startDate, @Param("endDate") String endDate);

    /**
     * 批量保存ld_rp_total_hour的数据到mysql中
     * @param lists
     */
    void batchSaveLdTotalDay(@Param("lists") List<LdRpTotalDay> lists);

    /**
     * 活跃用户留存率
     * @param yesDay 昨天日期，格式：yyyy-MM-dd
     * @param nowDay 今天日期，格式：yyyy-MM-dd
     * @return
     */
    @DS("clickhouse")
    Map<String, Object> countUserRetention(@Param("yesDay") String yesDay, @Param("nowDay") String nowDay);

    /**
     * 新增用户留存率
     * @param yesDay 昨天日期，格式：yyyy-MM-dd
     * @param nowDay 今天日期，格式：yyyy-MM-dd
     * @return
     */
    @DS("clickhouse")
    Map<String, Object> countNewUserRetention(@Param("yesDay") String yesDay, @Param("nowDay") String nowDay);

    /**
     *  查询时间段内的rp_total_minute表信息(从ClickHouse查询)
     * @param params
     * @return
     */
    @DS("clickhouse")
    List<RpTotalMinute> listTotalMinute(Map<String, Object> params);

    void deleteTotalMinute(@Param("startDm") Long startDm, @Param("endDm") Long endDm);

    void batchSaveTotalMinute(@Param("lists") List<RpTotalMinute> list);

    Long maxDmTotalMinute();

    /**
     *  查询时间段内的ld_rp_total_minute表信息(从ClickHouse查询)
     * @param params
     * @return
     */
    @DS("clickhouse")
    List<LdRpTotalMinute> listLdTotalMinute(Map<String, Object> params);

    void deleteLdTotalMinute(@Param("startDm") Long startDm, @Param("endDm") Long endDm);

    void batchSaveLdTotalMinute(@Param("lists") List<LdRpTotalMinute> list);

    Long maxDmLdTotalMinute();



}
