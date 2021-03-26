package com.miguan.flow.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.flow.dto.IncentiveVideoDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * @Description 激励视频mapper
 **/
public interface IncentiveVideoMapper {

    /**
     * 查询激励视频信息
     * @param params
     */
    @DS("ballvideo")
    List<IncentiveVideoDto> findIncentiveVideoList(Map<String, Object> params);
}

