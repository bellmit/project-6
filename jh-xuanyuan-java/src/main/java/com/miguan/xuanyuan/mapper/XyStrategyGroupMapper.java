package com.miguan.xuanyuan.mapper;

import com.miguan.xuanyuan.dto.StrategyGroupDto;
import com.miguan.xuanyuan.entity.XyStrategyGroup;
import com.miguan.xuanyuan.mapper.common.BaseMapper;

import java.util.List;

public interface XyStrategyGroupMapper extends BaseMapper<XyStrategyGroup> {

    public List<StrategyGroupDto> getGroupList(long adPosId, int offset, int limit);

    public XyStrategyGroup getDataById(long strategyGroupId);

    XyStrategyGroup getDataByPositionAndGroupName(Long strategyGroupId, Long positionId, String groupName);

    int updateStrategyGroupByAbId(Long abId, Integer status);

    List<XyStrategyGroup> findStrategyGroupByAbId(Long abId, Integer status);

    void deleteByAppId(Long appId);

    List<XyStrategyGroup> findByAppId(Long appId);

    void deleteByPositionId(Long positionId);

    List<XyStrategyGroup> findByPositionId(Long positionId);
}
