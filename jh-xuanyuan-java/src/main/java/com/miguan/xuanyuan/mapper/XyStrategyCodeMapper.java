package com.miguan.xuanyuan.mapper;

import com.miguan.xuanyuan.mapper.common.BaseMapper;
import com.miguan.xuanyuan.dto.XyStrategyCodeDto;
import com.miguan.xuanyuan.entity.XyStrategyCode;

import java.util.List;

public interface XyStrategyCodeMapper extends BaseMapper<XyStrategyCode> {

    /**
     * 获取具体分组（默认分组，对照组、测试组）的代码位列表
     *
     * @param strategyId
     * @return
     */
    public List<XyStrategyCodeDto> getStrategyCodeList(long strategyId);


    /**
     * 获取所有代码位列表
     *
     * @param positionId
     * @return
     */
    public List<XyStrategyCodeDto> getAllStrategyCodeList(Long strategyGroupId, Long positionId);

    /**
     * 获取广告源信息
     *
     * @param adCodeId
     * @return
     */
    XyStrategyCodeDto getStrategyCodeInfo(Long adCodeId);


    XyStrategyCode getStrategyCodeListByAdCodeId(Long strategyId, Long adCodeId);


    int updateStrategyCodeClose(Long strategyId);

    List<XyStrategyCode> findStrategyCodeClose(Long strategyId);


}
