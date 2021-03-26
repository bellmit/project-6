package com.miguan.xuanyuan.mapper;

import com.miguan.xuanyuan.vo.DesignInfoVo;

import java.util.List;

public interface OriginalityMapper {

    List<DesignInfoVo> findDesignInfos(String appKey, String code, Long positionId);

}
