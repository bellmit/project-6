package com.miguan.ballvideo.mapper3;

import com.miguan.ballvideo.vo.request.AdvertAreaVo;
import org.apache.ibatis.annotations.Param;

/**
 * @Description 计划区域
 * @Date 2020/11/20 16:44
 **/
public interface AdvertAreaMapper {
    void insert(AdvertAreaVo advertAreaVo);

    void deleteByPlanId(@Param("planId") Long planId);

    AdvertAreaVo findByPlanId(Long planId);

}
