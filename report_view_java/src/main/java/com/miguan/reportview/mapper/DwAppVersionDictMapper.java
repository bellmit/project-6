package com.miguan.reportview.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.reportview.entity.DwAppVersionDict;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * <p>
 * APP版本配置表 Mapper 接口
 * </p>
 *
 * @author zhongli
 * @since 2020-08-03
 */
public interface DwAppVersionDictMapper extends BaseMapper<DwAppVersionDict> {

    @Select("select DISTINCT app_version from ld_app_version_dict")
    @DS("clickhouse")
    List<String> getLdAppVersion();
}
