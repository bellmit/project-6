package com.miguan.advert.domain.mapper;

import com.miguan.advert.common.base.BaseMapper;
import com.miguan.advert.domain.pojo.AdPlat;
import com.miguan.advert.domain.vo.result.AdPlatVo;



public interface AdPlatMapper extends BaseMapper<AdPlat> {

    AdPlat findByPlatKey(String platKey);

    String findPlatNameByPlatKey(String platKey);
}
