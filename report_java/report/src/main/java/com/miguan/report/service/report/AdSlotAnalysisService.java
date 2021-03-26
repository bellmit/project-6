package com.miguan.report.service.report;

import com.miguan.report.vo.AdSlotAnlyDetailVo;
import com.miguan.report.vo.AdSlotAnlyVo;

import java.util.List;

/**广告位分析服务接口
 * @author zhongli
 * @date 2020-06-19 
 *
 */
public interface AdSlotAnalysisService {

    /**
     * 加载按应用名称和广告位分组的数据
     * @param qdate 此日期数据
     * @param platType 1=穿山甲 2=广点通 3=快手
     * @param appType 1=视频 2=来电
     * @return
     */
    List<AdSlotAnlyVo> loadByAppNameAndAdspace(String qdate, int platType, int appType);

    /**
     *
     * @param adSpace 广告位
     * @param startDate 此日期之后的数据
     * @param endDate   此日期之前的数据
     * @param apps      应用(带设备标识) 集
     * @param platType  1=穿山甲 2=广点通 3=快手
     * @param appType   1=视频 2=来电
     * @return
     */
    List<AdSlotAnlyDetailVo> loadAdSlotDetail(String adSpace,
                                              String startDate, String endDate,
                                              List<String> apps,
                                              int platType, int appType);

    /**
     * 统计用户行为数据
     * @param adSpace
     * @param startDate
     * @param endDate
     * @param apps
     * @param appType
     * @return
     */
    List<AdSlotAnlyDetailVo> loadAdUserBehaviorDetail(String adSpace,
                                              String startDate, String endDate,
                                              List<String> apps, int appType);
}
