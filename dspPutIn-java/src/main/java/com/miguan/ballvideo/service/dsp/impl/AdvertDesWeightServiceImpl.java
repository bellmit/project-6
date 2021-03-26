package com.miguan.ballvideo.service.dsp.impl;

import com.miguan.ballvideo.common.exception.ServiceException;
import com.miguan.ballvideo.mapper3.AdvertDesWeightMapper;
import com.miguan.ballvideo.service.dsp.AdvertDesWeightService;
import com.miguan.ballvideo.vo.request.AdvertDesWeightVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Collection;
import java.util.List;

/**
 * 计划组serviceImpl
 */
@Slf4j
@Service
public class AdvertDesWeightServiceImpl implements AdvertDesWeightService {

    @Resource
    private AdvertDesWeightMapper advertDesWeightMapper;

    //其它的业务信息

    @Override
    public void insert(AdvertDesWeightVo advertDesWeightVo){
        advertDesWeightMapper.insert(advertDesWeightVo);
    }

    @Override
    public void insertBatch(List<AdvertDesWeightVo> advertDesWeightVos) {
        if(CollectionUtils.isEmpty(advertDesWeightVos)){
            return ;
        }
        advertDesWeightMapper.insertBatch(advertDesWeightVos);
    }

    @Override
    public void deleteByGroupId(Long groupId) {
        advertDesWeightMapper.deleteByGroupId(groupId);
    }

    @Override
    public void deleteByPlanId(Long planId) {
        advertDesWeightMapper.deleteByPlanId(planId);
    }

    @Override
    public void deleteByDesId(Long desId) {
        advertDesWeightMapper.deleteByDesId(desId);
    }

    @Override
    public List<AdvertDesWeightVo> findByPlanId(Long planId) {
        return advertDesWeightMapper.findByPlanId(planId);
    }

    @Override
    public AdvertDesWeightVo findByDesignId(Long designId) {
        return advertDesWeightMapper.findByDesignId(designId);
    }
}
