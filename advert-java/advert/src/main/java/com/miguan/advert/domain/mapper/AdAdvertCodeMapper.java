package com.miguan.advert.domain.mapper;

import com.github.pagehelper.Page;
import com.miguan.advert.common.base.BaseMapper;
import com.miguan.advert.domain.pojo.AdAdvertCode;
import com.miguan.advert.domain.vo.request.AdAdvertCodeQuery;
import org.apache.ibatis.annotations.Param;



public interface AdAdvertCodeMapper  extends BaseMapper<AdAdvertCode> {

    Page<AdAdvertCode> findQueryPage(AdAdvertCodeQuery query);

    String[] findAdCodeAdId(@Param("adId") String adId);

    Integer countUsedPlat();
}
