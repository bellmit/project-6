package com.miguan.advert.domain.service;

import com.miguan.advert.domain.vo.PageVo;
import com.miguan.advert.domain.vo.request.AdAdvertCodeQuery;

import java.util.List;

public interface AdAdvertCodeService {
    PageVo getList(String url, Integer page, Integer page_size, AdAdvertCodeQuery query);

    String[] findAdCodeAdId(String ad_id);
}
