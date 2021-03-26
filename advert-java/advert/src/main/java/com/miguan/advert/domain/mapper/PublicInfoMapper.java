package com.miguan.advert.domain.mapper;

import com.miguan.advert.domain.vo.result.AdPlatVo;
import com.miguan.advert.domain.vo.result.AppInfoVo;
import java.util.List;

public interface PublicInfoMapper {

    /**
     * 查询应用列表
     * @return
     */
    List<AppInfoVo> getApp();

    /**
     * 查询广告平台列表
     * @return
     */
    List<AdPlatVo> getAdPlat();
}
