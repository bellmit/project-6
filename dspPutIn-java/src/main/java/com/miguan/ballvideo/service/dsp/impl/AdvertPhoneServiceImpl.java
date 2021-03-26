package com.miguan.ballvideo.service.dsp.impl;

import com.cgcg.context.util.StringUtils;
import com.google.common.collect.Lists;
import com.miguan.ballvideo.common.constants.TypeConstant;
import com.miguan.ballvideo.mapper3.AdvertPhoneMapper;
import com.miguan.ballvideo.mapper3.PhoneBrandMapper;
import com.miguan.ballvideo.service.dsp.AdvertPhoneService;
import com.miguan.ballvideo.vo.request.AdvertPhoneVo;
import com.miguan.ballvideo.vo.response.DistrictRes;
import com.miguan.ballvideo.vo.response.PhoneBrandRes;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;

/**
 * 计划组serviceImpl
 */
@Slf4j
@Service
public class AdvertPhoneServiceImpl implements AdvertPhoneService {

    @Resource
    private AdvertPhoneMapper advertPhoneMapper;

    //依赖
    @Resource
    private PhoneBrandMapper phoneBrandMapper;

    //其它的业务信息

    @Override
    public List<PhoneBrandRes> findAll() {
        return phoneBrandMapper.findAll();
    }

    @Override
    public AdvertPhoneVo findByPlanId(Long planId) {
        return advertPhoneMapper.findByPlanId(planId);
    }

    @Override
    public List<PhoneBrandRes> findPhoneInfo(AdvertPhoneVo phoneVo) {
        if(phoneVo == null || StringUtils.isEmpty(phoneVo.getBrand())){
            return null;
        }
        Integer phoneType = phoneVo.getType();
        List<PhoneBrandRes> phoneBrandRes = Lists.newArrayList();
        if(phoneType != null && phoneType == TypeConstant.PHONE_TYPE_LIMIT){
            List<String> brands = Arrays.asList(phoneVo.getBrand().split(","));
            if(CollectionUtils.isNotEmpty(brands)){
                phoneBrandRes = phoneBrandMapper.findByBrand(brands);
            }
        }
        return phoneBrandRes;
    }

    @Override
    public void insert(AdvertPhoneVo advertPhoneVo) {
        advertPhoneMapper.insert(advertPhoneVo);
    }

    @Override
    public void deleteByPlanId(Long planId) {
        advertPhoneMapper.deleteByPlanId(planId);
    }

}
