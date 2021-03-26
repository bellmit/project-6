package com.miguan.ballvideo.service.dsp;

import com.miguan.ballvideo.vo.request.AdvertPlanCodeRelationVo;

import java.util.List;

/**
 * 广告计划 & 代码位关联表
 */
public interface PlanCodeRelationService {

    /**
     * 新增关联表
     */
    void insert(AdvertPlanCodeRelationVo advertPlanCodeRelationVo);

    /**
     * 批量新增关联表
     */
    void insertBatch(List<AdvertPlanCodeRelationVo> advertPlanCodeRelations);

    /**
     * 根据广告计划删除关联
     * @param planId
     */
    void deleteByPlanId(Long planId);

    List<AdvertPlanCodeRelationVo> findByPlanId(Long planId);
}
