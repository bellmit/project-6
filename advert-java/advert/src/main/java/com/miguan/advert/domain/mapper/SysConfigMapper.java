package com.miguan.advert.domain.mapper;

import com.github.pagehelper.Page;
import com.miguan.advert.domain.dto.SysConfigDto;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

public interface SysConfigMapper {

    Page<SysConfigDto> listSysConfig(Map<String, Object> params);

    void insertSysConfig(@Param("dto") SysConfigDto sysConfigDto);

    void updateSysConfig(@Param("dto") SysConfigDto sysConfigDto);

    void deleteSysConfig(@Param("code") String code);
}
