package com.miguan.xuanyuan.service;

import com.miguan.xuanyuan.common.exception.ServiceException;
import com.miguan.xuanyuan.dto.XyStrategyCodeDto;
import com.miguan.xuanyuan.dto.XyStrategyDto;
import com.miguan.xuanyuan.entity.XyStrategy;
import com.miguan.xuanyuan.entity.XyStrategyCode;

import java.util.List;

public interface XyStrategyCodeService {

    public int insert(XyStrategyCode xyStrategyCode);

    public int update(XyStrategyCode xyStrategyCode);


    public List<XyStrategyCodeDto> getStrategyCodeList(long strategyId);

    /**
     * 获取所有代码位列表
     *
     * @param positionId
     * @return
     */
    public List<XyStrategyCodeDto> getAllStrategyCodeList(Long strategyGroupId, Long positionId);


    XyStrategyCode getStrategyCodeListByAdCodeId(Long strategyId, Long adCodeId);

    XyStrategyCodeDto getStrategyCodeInfo(Long adCodeId);

    void putStrategyCode(XyStrategyCode xyStrategyCode) throws ServiceException;

    int updateStrategyCodeClose(Long strategyId);
}
