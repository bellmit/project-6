package com.miguan.report.service.report;

public interface ShenceDataService {

    /**
     * 保存神策视频当天的数据
     * @param date 日期：yyyy-MM-dd
     * @param appType app类型
     */
    void saveShenceData(String date, int appType);

    /**
     * 删除神策的数据
     * @param startDate
     * @param endDate
     */
    void deleteShenceData(String startDate, String endDate, int appType);
}
