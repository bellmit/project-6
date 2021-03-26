package com.miguan.xuanyuan.mapper;

import com.miguan.xuanyuan.dto.request.AdCodeRequest;
import com.miguan.xuanyuan.entity.XyAdCode;
import com.miguan.xuanyuan.mapper.common.BaseMapper;

import java.util.List;

public interface XyAdCodeMapper extends BaseMapper<XyAdCode> {

    int insert(AdCodeRequest adCodeRequest);

    int update(AdCodeRequest adCodeRequest);

    XyAdCode getDataByPlatKeyAndCodeId(String sourcePlatKey, String sourceCodeId, Long id);

    Long getCodePutInStatus(String codeId);

    int updateCodeNotPutIn(Long positionId);

    List<XyAdCode> findCodeNotPutIn(Long positionId);

    void deleteByPositionId(Long positionId);

    List<XyAdCode> findByPositionId(Long positionId);

    void deleteByAppId(Long appId);

    List<XyAdCode> findByAppId(Long appId);

    List<XyAdCode> findByAppIdAndPlatKey(Long appId, String platKey);
}
