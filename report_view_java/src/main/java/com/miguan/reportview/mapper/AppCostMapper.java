package com.miguan.reportview.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.reportview.entity.AppCost;
import com.miguan.reportview.vo.CostVo;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * 应用总成本(app_cost)数据Mapper
 *
 * @author zhongli
 * @since 2020-08-07 18:40:02
 * @description 
*/
@Mapper
public interface AppCostMapper extends BaseMapper<AppCost> {

    List<CostVo> getCostForDate(Map<String, Object> params);
}
