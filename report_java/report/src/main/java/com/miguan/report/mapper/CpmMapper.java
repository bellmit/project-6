package com.miguan.report.mapper;

import com.miguan.report.dto.LineChartDto;
import com.miguan.report.vo.RevenuePlatformVo;

import java.util.List;
import java.util.Map;

/**
 * @Description 千展收益（CPM）mapper
 * @Author zhangbinglin
 * @Date 2020/6/17 11:54
 **/
public interface CpmMapper {

    /**
     * 活跃用户比对报表数据
     * @param param 参数
     * @return
     */
    List<LineChartDto> countLineCpmChart(Map<String, Object> param);


}
