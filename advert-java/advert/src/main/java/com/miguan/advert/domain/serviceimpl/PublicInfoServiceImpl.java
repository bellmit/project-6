package com.miguan.advert.domain.serviceimpl;

import com.miguan.advert.domain.mapper.PublicInfoMapper;
import com.miguan.advert.domain.service.PublicInfoService;
import com.miguan.advert.domain.vo.result.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@Slf4j
@Service
public class PublicInfoServiceImpl implements PublicInfoService {

    @Resource
    private PublicInfoMapper publicInfoMapper;

    @Override
    public List<AppInfoVo> getApp() {
        return publicInfoMapper.getApp();
    }

    @Override
    public List<AdPlatVo> getAdPlat() {
        return publicInfoMapper.getAdPlat();
    }

}
