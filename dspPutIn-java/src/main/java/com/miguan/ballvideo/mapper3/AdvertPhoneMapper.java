package com.miguan.ballvideo.mapper3;

import com.miguan.ballvideo.vo.request.AdvertPhoneVo;
import org.apache.ibatis.annotations.Param;

/**
 * @Description 计划区域
 * @Date 2020/11/20 16:44
 **/
public interface AdvertPhoneMapper {

    void insert(AdvertPhoneVo advertPhoneVo);

    void deleteByPlanId(@Param("planId") Long planId);

    AdvertPhoneVo findByPlanId(@Param("planId") Long planId);
}
