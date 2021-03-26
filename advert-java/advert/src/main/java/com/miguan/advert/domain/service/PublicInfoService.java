package com.miguan.advert.domain.service;

import com.miguan.advert.domain.vo.result.AdPlatVo;
import com.miguan.advert.domain.vo.result.AppInfoVo;
import java.util.List;

public interface PublicInfoService {

    List<AppInfoVo> getApp();

    List<AdPlatVo> getAdPlat();

}
