package com.miguan.advert.domain.service;

import com.miguan.advert.domain.vo.PageVo;

public interface DictionariesService {

    PageVo getList(Integer page, Integer page_size);
}
