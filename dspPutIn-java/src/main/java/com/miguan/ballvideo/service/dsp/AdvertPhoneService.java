package com.miguan.ballvideo.service.dsp;

import com.miguan.ballvideo.common.exception.ServiceException;
import com.miguan.ballvideo.vo.request.AdvertAreaVo;
import com.miguan.ballvideo.vo.request.AdvertPhoneVo;
import com.miguan.ballvideo.vo.response.PhoneBrandRes;

import java.util.List;

/**
 * 广告手机表
 */
public interface AdvertPhoneService {

    /**
     * 新增手机关联表
     */
    void insert(AdvertPhoneVo advertPhoneVo);

    /**
     * 根据广告计划删除区域表
     * @param planId
     */
    void deleteByPlanId(Long planId);

    List<PhoneBrandRes> findAll();

    AdvertPhoneVo findByPlanId(Long planId);

    List<PhoneBrandRes> findPhoneInfo(AdvertPhoneVo phoneVo);
}
