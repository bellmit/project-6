package com.miguan.reportview.service.impl;

import com.miguan.reportview.dto.ChannelCostDto;
import com.miguan.reportview.entity.FatherChannel;
import com.miguan.reportview.mapper.ChannelCostMapper;
import com.miguan.reportview.service.IChannelCostService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;

/**
 * 渠道维表管理
 */
@Service
public class ChannelCostServiceImpl implements IChannelCostService {

    @Resource
    private ChannelCostMapper channelCostMapper;



    public int dateCostGenerate(){
        return channelCostMapper.dateCostGenerate();
    };

    public int updateCost(Map<String, Object> map){
        return channelCostMapper.updateCost(map);
    };


    public List<ChannelCostDto> list(Map<String, Object> map){
        return channelCostMapper.list(map);
    };

    public int getChannelCostCount(Map<String, Object> map){
        return channelCostMapper.getChannelCostCount(map);
    };

    public int addFatherChannelCost(FatherChannel channelDO){
        return channelCostMapper.addFatherChannelCost(channelDO);
    };

}
