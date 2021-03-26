package com.miguan.bigdata.service;

import com.miguan.bigdata.dto.DeviceInfoDto;

import java.util.List;

public interface DeviceInfoService {

    public DeviceInfoDto findDistinctIdByChannel(String packageName, Integer actDay, List<String> excludeChannels, String beginDateTime, String endDateTime, Integer limit);
}
