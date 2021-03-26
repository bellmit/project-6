package com.miguan.reportview.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.miguan.reportview.entity.RpTotalDay;
import com.miguan.reportview.vo.LdOverallTrendVo;
import com.miguan.reportview.vo.LdRealTimeStaVo;
import com.miguan.reportview.vo.OverallTrendVo;
import com.miguan.reportview.vo.RealTimeStaVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 全局汇总表 Mapper 接口
 * </p>
 *
 * @author zhongli
 * @since 2020-08-04
 */
public interface RpTotalDayMapper extends BaseMapper<RpTotalDay> {

    @DS("clickhouse")
    List<RealTimeStaVo> getCheckereddata(Map<String, Object> param);
    List<RealTimeStaVo> getRealTimeCheckereddata(Map<String, Object> param);

    List<OverallTrendVo> getOverallTrendData(Map<String, Object> param);

    @DS("clickhouse")
    List<LdRealTimeStaVo> getLdCheckereddata(Map<String, Object> param);
    List<LdRealTimeStaVo> getLdRealTimeCheckereddata(Map<String, Object> param);

    List<LdOverallTrendVo> getLdOverallTrendData(Map<String, Object> param);
}
