package com.miguan.ballvideo.service.dsp.impl;

import com.miguan.ballvideo.mapper3.AdvertPlanCodeRelationMapper;
import com.miguan.ballvideo.service.dsp.PlanCodeRelationService;
import com.miguan.ballvideo.vo.request.AdvertPlanCodeRelationVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 计划 & 代码位关联表
 */
@Slf4j
@Service
public class PlanCodeRelationServiceImpl implements PlanCodeRelationService {

    @Resource
    AdvertPlanCodeRelationMapper advertPlanCodeRelationMapper;

    @Override
    public void insert(AdvertPlanCodeRelationVo advertPlanCodeRelationVo) {
        advertPlanCodeRelationMapper.insert(advertPlanCodeRelationVo);
    }

    @Override
    public void insertBatch(List<AdvertPlanCodeRelationVo> advertPlanCodeRelations) {
        advertPlanCodeRelationMapper.insertBatch(advertPlanCodeRelations);
    }

    @Override
    public void deleteByPlanId(Long planId) {
        advertPlanCodeRelationMapper.deleteByPlanId(planId);
    }

    @Override
    public List<AdvertPlanCodeRelationVo> findByPlanId(Long planId) {
        return advertPlanCodeRelationMapper.findByPlanId(planId);
    }
}
