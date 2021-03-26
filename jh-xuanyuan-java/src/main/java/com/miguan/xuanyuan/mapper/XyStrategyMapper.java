package com.miguan.xuanyuan.mapper;

import com.miguan.xuanyuan.dto.XyStrategyDto;
import com.miguan.xuanyuan.entity.XyStrategy;
import com.miguan.xuanyuan.mapper.common.BaseMapper;

import java.util.List;

public interface XyStrategyMapper extends BaseMapper<XyStrategy> {

    public List<XyStrategyDto> getStrategyList(long strategyGroupId);

    XyStrategyDto getDataById(Long id);

    XyStrategyDto getDataByAbItemId(Long strategyGroupId, Long abItemId);

    int updateStrategyClose(Long strategyGroupId, List<Long> abItemIdList);

    List<XyStrategyDto> findStrategyClose(Long strategyGroupId, List<Long> abItemIdList);

    XyStrategy getDefaultStrategyByPositionId(Long positionId);
}
