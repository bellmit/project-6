package com.miguan.reportview.mapper;

import com.miguan.reportview.entity.FatherChannel;

import java.util.List;
import java.util.Map;

/**
 * 渠道维表管理
 */
public interface ChannelManageMapper {

    int addFatherChannel(FatherChannel channelDO);

    int updateFatherChannel(FatherChannel channelDO);

    int deleteFatherChannel(Long id);

    List<FatherChannel> listFatherChannel(Map<String, Object> map);

    int getFatherChannelCount(Map<String, Object> map);

    List<String> listFatherChannelYm();

    List<String> listOwner();

    FatherChannel getFatherChannel(Long id);


}
