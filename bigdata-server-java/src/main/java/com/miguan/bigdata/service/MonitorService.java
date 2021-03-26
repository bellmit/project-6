package com.miguan.bigdata.service;

/**
 * @Description 监测service
 * @Author zhangbinglin
 * @Date 2020/11/6 8:57
 **/
public interface MonitorService {

    /**
     * 监测广告行为，用户行为中uuid为空的情况
     * @return
     */
    String getUuidNullMonitor();
}
