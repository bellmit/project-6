package com.miguan.ballvideo.mapper3;

import com.miguan.ballvideo.vo.request.AdvertDesWeightVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;


/**
 * @Description 计划组mapper
 * @Author zhangbinglin
 * @Date 2020/11/20 16:44
 **/
public interface AdvertDesWeightMapper {

    void insert(AdvertDesWeightVo advertDesWeightVo);

    void insertBatch(@Param("advertDesWeightVos") List<AdvertDesWeightVo> advertDesWeightVos);

    void deleteByGroupId(@Param("groupId") Long groupId);

    void deleteByPlanId(@Param("planId") Long planId);

    void deleteByDesId(@Param("desId") Long desId);

    List<AdvertDesWeightVo> findByPlanId(Long planId);

    AdvertDesWeightVo findByDesignId(@Param("desId") Long designId);
}
