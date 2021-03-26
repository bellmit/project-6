package com.miguan.bigdata.service;

/**
 * 用户设备应用安装列表service
 */
public interface AppListService {

    /**
     * mongodb的drive库的apps_info_list表中历史数据的distinctId字段数据刷进去
     */
    void brushOldAppListDistinctId();
}
