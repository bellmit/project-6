package com.miguan.reportview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.reportview.entity.BannerDataTotalName;
import com.miguan.reportview.vo.AdTotalVo;

import java.util.List;
import java.util.Map;

/**
 * 数据汇总(banner_data_total_name)数据Mapper
 *
 * @author zhongli
 * @since 2020-08-07 10:04:29
 * @description 
*/
public interface BannerDataTotalNameMapper extends BaseMapper<BannerDataTotalName> {

    List<AdTotalVo> getData(Map<String, Object> params);
}
