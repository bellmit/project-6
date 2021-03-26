package com.miguan.reportview.service;

/**
 * @Description 同步用户内容运营数据和渠道数据表service
 * @Author zhangbinglin
 * @Date 2020/8/19 18:27
 **/
public interface SyncUserContentService {
    /**
     * 同步用户内容运营数据
     * @param startDate 开始时间,如：2020-08-01
     * @param endDate  结束时间,如：2020-08-19
     */
    void syncUserContent(String startDate, String endDate);

    /**
     * 同步来电用户事件数据
     * @param date 时间,如：2020-08-01
     */
    void syncLdUserContent(String date);
}
