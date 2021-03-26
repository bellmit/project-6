package com.miguan.xuanyuan.service;

import com.alibaba.fastjson.JSON;
import com.miguan.xuanyuan.dto.XyStrategyDto;
import com.miguan.xuanyuan.entity.XyStrategy;

import java.util.Arrays;
import java.util.List;

public interface XyStrategyService {

    public long insert(XyStrategy xyStrategy);

    int update(XyStrategy xyStrategy);

    public XyStrategy initStrategyEntity(long strategyGroupId);

    public List<XyStrategyDto> getStrategyList(long strategyGroupId);

    XyStrategyDto getDataById(Long id);

    XyStrategyDto getDataByAbItemId(Long strategyGroupId, Long abItemId);

    public void putStrategyWitchAbItemId(XyStrategy xyStrategy);

    int updateStrategyClose(Long strategyGroupId, List<Long> abItemIdList);

    XyStrategy getDefaultStrategyByPositionId(Long positionId);

    /**
     * 初始化自定义字段
     *
     * @return
     */
    public static String getInitCustomField() {
        String customField = JSON.toJSONString(Arrays.asList(new String[]{"", "", "", "", "", ""}));
        return customField;
    }

}
