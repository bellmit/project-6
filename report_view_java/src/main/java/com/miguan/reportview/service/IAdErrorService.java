package com.miguan.reportview.service;

import com.miguan.reportview.dto.AdErrorDto;

import java.util.List;

/**
 * @author zhongli
 * @date 2020-08-05 
 *
 */
public interface IAdErrorService {

    List<AdErrorDto> getData(Boolean isNew,
                             List<String> appPackages,
                             List<String> appVersions,
                             List<String> pChannelIds,
                             List<String> channelIds,
                             List<String> spaceKeys,
                             List<String> adcodes,
                             List<String> plates,
                             List<String> modeles,
                             List<String> appAdspace,
                             List<String> groups,
                             Integer appType,
                             String startDate,
                             String endDate);
}
