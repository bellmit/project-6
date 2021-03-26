package com.miguan.xuanyuan.service.impl;

import com.alibaba.fastjson.JSON;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.util.DateUtils;
import com.miguan.xuanyuan.dto.XyStrategyDto;
import com.miguan.xuanyuan.entity.XyAdCode;
import com.miguan.xuanyuan.entity.XyStrategy;
import com.miguan.xuanyuan.entity.XyStrategyCode;
import com.miguan.xuanyuan.entity.XyStrategyGroup;
import com.miguan.xuanyuan.mapper.XyStrategyMapper;
import com.miguan.xuanyuan.service.XyStrategyService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

@Service
public class XyStrategyServiceImpl implements XyStrategyService {

    @Resource
    public XyStrategyMapper strategyMapper;

    public long insert(XyStrategy xyStrategy){
        long id = strategyMapper.insert(xyStrategy);
        return id;
    }


    public int update(XyStrategy xyStrategy) {
        return strategyMapper.update(xyStrategy);
    }

    public XyStrategy initStrategyEntity(long strategyGroupId) {
        String curTime = DateUtils.getCurrentDateTimeStr();
        String customField = XyStrategyService.getInitCustomField();
        XyStrategy xyStrategy = new XyStrategy();
        xyStrategy.setStrategyGroupId(strategyGroupId);
        xyStrategy.setType(0);
        xyStrategy.setAbItemId(0L);
        xyStrategy.setAbRate(0);
        xyStrategy.setSortType(1);
        xyStrategy.setCustomField(customField);
        xyStrategy.setStatus(1);
        return xyStrategy;
    }

    /**
     * 根据分组id获取策略列表
     *
     * @param strategyGroupId
     * @return
     */
    public List<XyStrategyDto> getStrategyList(long strategyGroupId) {
        return strategyMapper.getStrategyList(strategyGroupId);
    }

    public XyStrategyDto getDataById(Long id) {
        return strategyMapper.getDataById(id);
    }

    public XyStrategyDto getDataByAbItemId(Long strategyGroupId, Long abItemId) {
        return strategyMapper.getDataByAbItemId(strategyGroupId, abItemId);
    }

    /**
     * 设置数据
     *
     * @param xyStrategy
     */
    public void putStrategyWitchAbItemId(XyStrategy xyStrategy) {
        XyStrategyDto result = getDataByAbItemId(xyStrategy.getStrategyGroupId(), xyStrategy.getAbItemId());
        if (result != null) {
            xyStrategy.setId(result.getId());
            update(xyStrategy);
        } else {
            insert(xyStrategy);
        }
    }

    /**
     * 关闭实验分组
     *
     * @param strategyGroupId
     * @param abItemIdList
     * @return
     */
    public int updateStrategyClose(Long strategyGroupId, List<Long> abItemIdList) {
        return strategyMapper.updateStrategyClose(strategyGroupId, abItemIdList);
    }

    /**
     * 获取默认策略分组
     *
     * @param positionId
     * @return
     */
    public XyStrategy getDefaultStrategyByPositionId(Long positionId) {
        return strategyMapper.getDefaultStrategyByPositionId(positionId);
    }
}
