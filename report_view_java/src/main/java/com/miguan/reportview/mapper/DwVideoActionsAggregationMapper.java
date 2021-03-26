package com.miguan.reportview.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.reportview.entity.DwVideoActions;
import com.miguan.reportview.vo.ChannelDataVo;

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
public interface DwVideoActionsAggregationMapper extends BaseMapper {

    @DS("clickhouse")
    int countOfData(Map<String, Object> params);

    @DS("clickhouse")
    List<Map<String, Object>> getData(Map<String, Object> params);

}
