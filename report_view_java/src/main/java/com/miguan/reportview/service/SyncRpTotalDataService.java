package com.miguan.reportview.service;

/**
 * 同步rp_total_day数据 和 rp_total_hour数据
 */
public interface SyncRpTotalDataService {
    /**
     * 从clickhouse同步rp_total_hour的数据
     * @param startDh 开始小时，例如：2020082712
     * @param endDh 结束小时，例如：2020082712
     */
    public void syncRpTotalHour(int startDh, int endDh);

    /**
     * 从clickhouse同步rp_total_day的数据
     * @param startDate 开始时间
     * @param endDate 结束时间
     */
    void syncRpTotalDay(String startDate, String endDate);

    /**
     * 从clickhouse同步ld_rp_total_hour的数据
     * @param startDh 开始小时，例如：2020082712
     * @param endDh 结束小时，例如：2020082712
     */
    void syncLdRpTotalHour(int startDh, int endDh);

    /**
     * 从clickhouse同步rp_total_hour的数据
     * @param startDm 开始分钟，例如：202008271250
     * @param endDm 结束分钟，例如：202008271255
     */
    void syncRpTotalMinute(Long startDm, Long endDm);

    /**
     * 从clickhouse同步ld_rp_total_day的数据
     * @param startDate 开始时间
     * @param endDate 结束时间
     */
    void syncLdRpTotalDay(String startDate, String endDate);

    /**
     * 从clickhouse同步ld_rp_total_hour的数据
     * @param startDm 开始分钟，例如：202008271250
     * @param endDm 结束分钟，例如：202008271255
     */
    void syncLdRpTotalMinute(Long startDm, Long endDm);
}
