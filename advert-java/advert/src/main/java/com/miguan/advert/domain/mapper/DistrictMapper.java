package com.miguan.advert.domain.mapper;

import com.miguan.advert.domain.vo.result.AdCityVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface DistrictMapper {
    List<AdCityVo> getCityList(Integer type);
}
