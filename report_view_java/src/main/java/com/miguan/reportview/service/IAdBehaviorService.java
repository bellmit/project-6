package com.miguan.reportview.service;

import com.miguan.reportview.dto.AdBehaviorDto;
import com.miguan.reportview.dto.AdBehaviorSonDto;
import com.miguan.reportview.dto.ResponseEntity;

import java.util.List;

/**
 * @author zhongli
 * @date 2020-08-05 
 *
 */
public interface IAdBehaviorService {

    List<AdBehaviorDto> getData(Boolean isNew,
                                List<String> appPackages,
                                List<String> appVersions,
                                List<String> pChannelIds,
                                List<String> channelIds,
                                List<String> spaceKeys,
                                List<String> adcodes,
                                List<String> plates,
                                List<String> appAdspace,
                                List<String> groups,
                                int apptype,
                                String startDate,
                                String endDate);

    /**
     * 子广告报表数据
     * @param appPackage 包名
     * @param adKey 广告位key
     * @param adSource 平台
     * @param renderType  渲染方式
     * @param adcType  素材
     * @param qId  子广告位
     * @param appType  app类型,1:视频，2：来电
     * @return
     */
    List<AdBehaviorSonDto> getSonData(List<String> appPackage, List<String> adKey, List<String> adSource, List<String> renderType,
                                                      List<String> adcType, List<String> qId, Integer appType, String startDate, String endDate);

    /**
     * 钉钉-广告展示/广告库存比值预警
     * @return
     */
    String findEarlyWarnList();
}
