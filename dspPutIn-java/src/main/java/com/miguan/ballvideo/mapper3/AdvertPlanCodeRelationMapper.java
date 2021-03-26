package com.miguan.ballvideo.mapper3;

import com.miguan.ballvideo.vo.request.AdvertPlanCodeRelationVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @Description 计划和广告位关联表
 * @Date 2020/11/20 16:44
 **/
public interface AdvertPlanCodeRelationMapper {
    void insert(AdvertPlanCodeRelationVo advertPlanCodeRelationVo);
    void insertBatch(@Param("advertPlanCodeRelations")List<AdvertPlanCodeRelationVo> advertPlanCodeRelationVos);

    void deleteByPlanId(@Param("planId") Long planId);

    List<AdvertPlanCodeRelationVo> findByPlanId(Long planId);

}
