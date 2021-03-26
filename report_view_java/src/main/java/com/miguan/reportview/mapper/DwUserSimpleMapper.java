package com.miguan.reportview.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.reportview.dto.YesterDayUserKeepDto;
import com.miguan.reportview.entity.DwUserActions;
import com.miguan.reportview.entity.RpUserKeep;
import com.miguan.reportview.vo.UserContentDataVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 用户留存宽表 Mapper 接口
 * </p>
 *
 * @author liyu
 * @since 2020-08-26
 */
public interface DwUserSimpleMapper extends BaseMapper<YesterDayUserKeepDto> {

    @DS("clickhouse")
    int countYesterDayUserKeep(Map<String, Object> params);

    @DS("clickhouse")
    List<Map<String, Object>> findYesterDayUserKeep(Map<String, Object> params);

    @DS("clickhouse")
    int countLdDayUserKeep(Map<String, Object> params);

    @DS("clickhouse")
    List<RpUserKeep> findLdDayUserKeep(Map<String, Object> params);
}
