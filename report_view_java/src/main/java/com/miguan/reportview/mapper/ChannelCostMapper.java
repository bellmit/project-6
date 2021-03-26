package com.miguan.reportview.mapper;

import com.miguan.reportview.dto.ChannelCostDto;
import com.miguan.reportview.entity.FatherChannel;

import java.util.List;
import java.util.Map;

/**
 * 渠道维表管理
 */
public interface ChannelCostMapper {

    int dateCostGenerate();

    int updateCost(Map<String, Object> map);

    List<ChannelCostDto> list(Map<String, Object> map);

    int getChannelCostCount(Map<String, Object> map);

    int addFatherChannelCost(FatherChannel channelDO);

}
