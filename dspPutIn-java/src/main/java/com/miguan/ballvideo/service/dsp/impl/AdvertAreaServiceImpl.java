package com.miguan.ballvideo.service.dsp.impl;

import com.cgcg.context.util.StringUtils;
import com.google.common.collect.Lists;
import com.miguan.ballvideo.common.constants.TypeConstant;
import com.miguan.ballvideo.mapper3.AdvertAreaMapper;
import com.miguan.ballvideo.mapper3.DistrictMapper;
import com.miguan.ballvideo.service.dsp.AdvertAreaService;
import com.miguan.ballvideo.vo.request.AdvertAreaVo;
import com.miguan.ballvideo.vo.response.DistrictRes;
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
public class AdvertAreaServiceImpl implements AdvertAreaService {

    @Resource
    private AdvertAreaMapper advertAreaMapper;

    //依赖
    @Resource
    private DistrictMapper districtMapper;
    //其它的业务信息

    @Override
    public List<DistrictRes> getProvinceList() {
        return districtMapper.getList(TypeConstant.TYPE_PROVINCE,null);
    }

    @Override
    public List<DistrictRes> getCityListByProvince(Long pid) {
        return districtMapper.getList(TypeConstant.TYPE_CITY,pid);
    }

    @Override
    public void insert(AdvertAreaVo advertAreaVo) {
        advertAreaMapper.insert(advertAreaVo);
    }

    @Override
    public void deleteByPlanId(Long planId) {
        advertAreaMapper.deleteByPlanId(planId);
    }

    @Override
    public AdvertAreaVo findByPlanId(Long planId) {
        return advertAreaMapper.findByPlanId(planId);
    }

    @Override
    public List<DistrictRes> findAreaInfo(AdvertAreaVo areaVo) {
        if(areaVo == null || StringUtils.isEmpty(areaVo.getArea())){
            return null;
        }
        Integer areaType = areaVo.getType();
        List<DistrictRes> districtResList = Lists.newArrayList();
        if(areaType != null && areaType == TypeConstant.AREA_TYPE_LIMIT){
            List<String> names = Arrays.asList(areaVo.getArea().split(","));
            if(CollectionUtils.isNotEmpty(names)){
                districtResList = districtMapper.findByDistinctName(names,TypeConstant.TYPE_CITY);
            }
        }
        return districtResList;
    }

}
