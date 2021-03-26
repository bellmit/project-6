package com.miguan.ballvideo.service.dsp.impl;

import com.miguan.ballvideo.common.constants.MaterialShapeConstants;
import com.miguan.ballvideo.mapper3.AdvertCodeMapper;
import com.miguan.ballvideo.service.dsp.AdvertCodeService;
import com.miguan.ballvideo.vo.request.AdvertCodeVo;
import com.miguan.ballvideo.vo.response.AdvertCodeSimpleRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 代码位
 */
@Slf4j
@Service
public class AdvertCodeServiceImpl implements AdvertCodeService {

    @Resource
    private AdvertCodeMapper advertCodeMapper;

    @Override
    public List<AdvertCodeVo> findByStyleSize(String styleSize, Integer adType) {
        return advertCodeMapper.findByStyleSize(styleSize,adType);
    }

    @Override
    public List<AdvertCodeSimpleRes> findByCodeIds(List<Long> codeIds) {
        return advertCodeMapper.findByCodeIds(codeIds);
    }

    @Override
    public List<AdvertCodeSimpleRes> getCodeList(Long appId, String name, Integer materialShape, Integer adType) {
        return advertCodeMapper.getCodeList(appId,name,materialShape == null ? null : MaterialShapeConstants.materialShapeMap.get(materialShape),adType);
    }

    @Override
    public List<AdvertCodeVo> findAdvCodeByGroupIds(List<Integer> groupIds) {
        return advertCodeMapper.findAdvCodeByGroupIds(groupIds);
    }

    @Override
    public List<AdvertCodeVo> findAdvCodeInfoByDesignIds(List<Long> designIds) {
        return advertCodeMapper.findAdvCodeInfoByDesignIds(designIds);
    }

    @Override
    public List<AdvertCodeVo> findAdvCodeByPlanIds(List<String> planIds) {
        return advertCodeMapper.findAdvCodeByPlanIds(planIds);
    }
}
