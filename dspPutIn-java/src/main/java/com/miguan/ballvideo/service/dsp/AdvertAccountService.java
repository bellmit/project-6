package com.miguan.ballvideo.service.dsp;

import com.miguan.ballvideo.vo.request.AdvertAccountVo;
/**
 * 广告账户
 */
public interface AdvertAccountService {

    /**
     * 获取最新的广告账户,通过广告计划id,
     */
    AdvertAccountVo findOneByPlanId(Long planId);

    void update(AdvertAccountVo accountVo);
}
