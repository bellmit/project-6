package com.miguan.ballvideo.service.dsp.impl;

import com.miguan.ballvideo.mapper3.AdvertAccountMapper;
import com.miguan.ballvideo.service.dsp.AdvertAccountService;
import com.miguan.ballvideo.vo.request.AdvertAccountVo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * 创意创意
 */
@Slf4j
@Service
public class AdvertAccountServiceImpl implements AdvertAccountService {

    @Resource
    private AdvertAccountMapper advertAccountMapper;

    @Override
    public AdvertAccountVo findOneByPlanId(Long planId) {
        return advertAccountMapper.findOneByPlanId(planId);
    }

    @Override
    public void update(AdvertAccountVo accountVo) {
        advertAccountMapper.update(accountVo);
    }

}
