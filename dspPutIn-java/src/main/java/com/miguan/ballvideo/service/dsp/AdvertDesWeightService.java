package com.miguan.ballvideo.service.dsp;

import com.miguan.ballvideo.vo.request.AdvertDesWeightVo;

import java.util.List;

/**
 * 广告关联表
 */
public interface AdvertDesWeightService {

    /**
     * 新增关联表
     */
    void insert(AdvertDesWeightVo advertDesWeightVo) ;
    /**
     * 新增关联表
     */
    void insertBatch(List<AdvertDesWeightVo> advertDesWeightVos) ;

    /**
     * 根据广告计划删除关联表
     * @param planId
     */
    void deleteByPlanId(Long planId);

    /**
     * 根据计划组删除关联表
     * @param groupId
     */
    void deleteByGroupId(Long groupId);

    /**
     * 根据广告创意删除关联表
     * @param desId
     */
    void deleteByDesId(Long desId);

    List<AdvertDesWeightVo> findByPlanId(Long planId);

    AdvertDesWeightVo findByDesignId(Long designId);
}
