package com.miguan.bigdata.service;

import com.alibaba.fastjson.JSONObject;
import com.miguan.bigdata.entity.mongo.AppDeviceVo;

import java.util.List;

public interface AppDeviceService {

    public static String collection_name = "android_app_device";
    /**
     * 根据起始时间查询设备
     *
     * @param beginTime
     * @param endTime
     */
    public List<AppDeviceVo> findDistinctByTime(String beginTime, String endTime, int skip, int limit);

    /**
     * 根据包名、设备ID，更新设备推送状态
     *
     * @param packageName
     * @param distinct_id
     * @param createTime
     */
    public void update4NpushInit(String packageName, String distinct_id, String createTime, Integer npushState, Integer npushChannel, Integer lastVisitDate);

    /**
     * 根据包名、设备ID，更新设备推送状态
     *
     * @param packageName
     * @param distinct_id
     */
    public void updateNpushStateToStop(String packageName, String distinct_id);

    /**
     * 根据dt、包名统计设备数量
     * @param dt
     * @param packageName
     * @return
     */
    public long countDistinctByDtAndPackageName(Integer dt, String packageName);

    /**
     * 根据dt、包名查询设备ID
     * @param dt
     * @param packageName
     * @return
     */
    public List<String> findDistinctByDtAndPackageName(Integer dt, String packageName, int skip, int limit);

    /**
     * 更新设备的推送厂商
     * @param param
     */
    public void updateDistinctNPushChannel(JSONObject param);
}
