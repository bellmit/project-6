package com.miguan.reportview.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.reportview.entity.DwUserActions;
import com.miguan.reportview.vo.LdUserContentDataVo;
import com.miguan.reportview.vo.UserContentDataVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 视频数据宽表 Mapper 接口
 * </p>
 *
 * @author zhongli
 * @since 2020-08-04
 */
public interface DwUserActionsMapper extends BaseMapper<DwUserActions> {

    @DS("clickhouse")
    List<UserContentDataVo> getData(Map<String, Object> params);

    @DS("clickhouse")
    List<UserContentDataVo> getNewData(Map<String, Object> params);

    @DS("clickhouse")
    List<UserContentDataVo> getStaData(Map<String, Object> params);

    @DS("clickhouse")
    List<LdUserContentDataVo> getLdData(Map<String, Object> params);

    @DS("clickhouse")
    List<LdUserContentDataVo> getLdMultipleData(Map<String, Object> params);
}
