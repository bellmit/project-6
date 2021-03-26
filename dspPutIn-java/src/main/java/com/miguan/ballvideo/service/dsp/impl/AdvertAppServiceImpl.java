package com.miguan.ballvideo.service.dsp.impl;

import com.miguan.ballvideo.common.constants.MaterialShapeConstants;
import com.miguan.ballvideo.mapper3.AdvertAppMapper;
import com.miguan.ballvideo.service.dsp.AdvertAppService;
import com.miguan.ballvideo.vo.response.AdvertAppSimpleRes;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

/**
 * 应用
 */
@Slf4j
@Service
public class AdvertAppServiceImpl implements AdvertAppService {

    @Resource
    private AdvertAppMapper advertAppMapper;

    @Override
    public List<AdvertAppSimpleRes> getAppList(Integer materialShape, String materialType) {
        return advertAppMapper.getAppList(materialShape == null ? null : MaterialShapeConstants.materialShapeMap.get(materialShape),materialType);
    }
}
