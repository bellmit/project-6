package com.miguan.flow.service;

import com.miguan.flow.dto.IncentiveParamDto;
import com.miguan.flow.dto.IncentiveVideoDto;

import java.util.List;

/**
 * @Description 激励视频service
 **/
public interface IncentiveVideoService {

    /**
     * 根据参数从推荐接口中获取激励视频id，并返回激励视频信息
     * @param paramDto
     * @return
     */
    List<IncentiveVideoDto> findIncentiveVideoList(IncentiveParamDto paramDto);
}
