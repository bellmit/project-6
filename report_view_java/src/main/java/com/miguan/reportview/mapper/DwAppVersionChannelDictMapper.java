package com.miguan.reportview.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.reportview.entity.DwAppVersionChannelDict;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * @author zhongli
 * @date 2020-08-06 
 *
 */
public interface DwAppVersionChannelDictMapper extends BaseMapper<DwAppVersionChannelDict> {

    @Select("select DISTINCT father_channel from ld_app_version_channel_dict")
    @DS("clickhouse")
    List<Object> getLdParentChannelDist();

    @Select("select channel, father_channel from ld_app_version_channel_dict where channel != '' and channel is not null and father_channel = #{parent}")
    @DS("clickhouse")
    List<DwAppVersionChannelDict> getLdSubChannelDist(@Param("parent") String parent);

    @Select("select channel, father_channel from ld_app_version_channel_dict where channel != '' and channel is not null group by channel, father_channel")
    @DS("clickhouse")
    List<DwAppVersionChannelDict> getAllSubChannel();
}
