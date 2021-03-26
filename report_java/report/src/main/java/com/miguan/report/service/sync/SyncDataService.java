package com.miguan.report.service.sync;

import com.miguan.report.dto.AdIdAndNameDto;
import com.miguan.report.entity.BannerRule;

import java.util.Date;
import java.util.Map;

/**
 * @Description 同步数据 service
 * @Author zhangbinglin
 * @Date 2020/6/30 14:09
 **/
public interface SyncDataService {

    /**
     * 同步错误数和请求数到banner
     *
     * @param date (格式:yyyyMMdd)
     */
    void saveBannerDataExt(Date date, Integer appType);

    /**
     * 删除指定日期的banner_data_ext表的数据
     *
     * @param date
     */
    void deleteBannerDataExt(String date);

    /**
     * 计算错误率
     *
     * @param date
     */
    void updateBannerExtErrRate(String date);

    /**
     * 同步每小时展现数和点击数到hour_data表
     *
     * @param date (格式:yyyyMMdd)
     */
    void saveHourDataExt(Date date);

    /**
     * 删除指定日期的hour_data表的数据
     *
     * @param date 日期：yyyy-MM-dd
     */
    void deleteHourDataByDate(String date);

    /**
     * 计算hour_data表的点击率
     *
     * @param date 日期：yyyy-MM-dd
     */
    void updateClickRate(String date);

    /**
     * 从快手获取数据
     *
     * @param date           日期
     * @param videoAdnameMap 视频代码位信息（广告库中的）
     * @param callAdnameMap  来电代码位信息（广告库中的）
     * @param bannerRuleMap  代码位信息（报表库中的）
     */
    void getDataFromKs(Date date, Map<String, AdIdAndNameDto> videoAdnameMap, Map<String, AdIdAndNameDto> callAdnameMap, Map<String, BannerRule> bannerRuleMap);

    /**
     * 从广点通获取数据
     *
     * @param date           日期
     * @param videoAdnameMap 视频代码位信息（广告库中的）
     * @param callAdnameMap  来电代码位信息（广告库中的）
     * @param bannerRuleMap  代码位信息（报表库中的）
     */
    void getDataFromGdt(Date date, Map<String, AdIdAndNameDto> videoAdnameMap, Map<String, AdIdAndNameDto> callAdnameMap, Map<String, BannerRule> bannerRuleMap);
}
