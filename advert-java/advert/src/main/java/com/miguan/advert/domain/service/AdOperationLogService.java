package com.miguan.advert.domain.service;

import com.miguan.advert.domain.vo.PageVo;
import com.miguan.advert.domain.vo.request.AdOperationLogQuery;

public interface AdOperationLogService {

    PageVo findPage(Integer page, Integer page_size, AdOperationLogQuery adOperationLogQuery);
}
