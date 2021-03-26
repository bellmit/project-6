package com.miguan.advert.domain.mapper;

import com.github.pagehelper.Page;
import com.miguan.advert.domain.vo.request.AdOperationLogQuery;
import com.miguan.advert.domain.vo.result.AdOperationLogVo;

import java.util.Map;

public interface AdOperationLogMapper {

    int add(AdOperationLogVo vo);

    /**
     * 查询表信息
     * @return
     */
    Page<AdOperationLogVo> findBySelective(AdOperationLogQuery adOperationLogQuery);

}
