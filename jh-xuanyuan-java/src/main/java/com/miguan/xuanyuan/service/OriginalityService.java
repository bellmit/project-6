package com.miguan.xuanyuan.service;

import com.miguan.xuanyuan.dto.CreativeParamsDto;
import com.miguan.xuanyuan.vo.CreativeInfoVo;

import java.util.Date;

public interface OriginalityService {
    CreativeInfoVo creativeInfo(CreativeParamsDto queueVo);

    boolean isPlanActive(Integer putTimeType, Date startDate, Date endDate, Integer timeSetting, String timesConfig);
}
