package com.miguan.report.service.report;

import com.umeng.uapp.param.UmengUappChannelInfo;
import com.umeng.uapp.param.UmengUappDailyDataInfo;

public interface UmengService {

    /**
     * 通过友盟获取APP使用时长
     */
    public Double getDurations(String appId, String dateString);

    /**
     * 统计日活用户数，新增用户数，总用户数
     * @param appId 友盟应用ID(茜柚视频-Android:5d59ff453fc195a47d000c8c, 果果视频_Android:5dd52df2570df3a75800008d)
     * @param date 日期
     * @return
     */
    UmengUappDailyDataInfo getStatData(String appId, String date);

    /**
     * 新增umeng_data友盟数据
     * @param date 日期，格式：yyyy-MM-dd
     */
    void saveUmengData(String date);

    /**
     * 从友盟获取渠道对应的数据信息
     * @param date
     */
    void saveUmengChannel(String date);

    /**
     * 删除友盟的数据
     * @param startDate
     * @param endDate
     */
    public void deleteUmengData(String startDate, String endDate, String appName);

    int countByDate(String date);
}
