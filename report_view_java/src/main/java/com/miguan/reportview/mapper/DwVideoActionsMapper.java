package com.miguan.reportview.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.reportview.entity.DwVideoActions;
import com.miguan.reportview.vo.AdClickNumVo;
import com.miguan.reportview.vo.ChannelDataVo;
import com.miguan.reportview.vo.LdUserContentDataVo;
import com.miguan.reportview.vo.UmengChannelDataVo;

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
public interface DwVideoActionsMapper extends BaseMapper<DwVideoActions> {

    @DS("clickhouse")
    List<ChannelDataVo> getData(Map<String, Object> params);

    @DS("clickhouse")
    List<ChannelDataVo> getNewData(Map<String, Object> params);

    @DS("clickhouse")
    List<LdUserContentDataVo> getLdData(Map<String, Object> params);

    @DS("clickhouse")
    List<LdUserContentDataVo> getLdMultipleData(Map<String, Object> params);


    List<UmengChannelDataVo> findUmengChannelData(Map<String, Object> params);

    @DS("clickhouse")
    List<AdClickNumVo> staChannelPreAdClick(Map<String, Object> params);

}
