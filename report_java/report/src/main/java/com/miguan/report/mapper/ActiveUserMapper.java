package com.miguan.report.mapper;

import com.miguan.report.dto.LineChartDto;

import java.util.List;
import java.util.Map;

/**
 * @Description 活跃用户mapper
 * @Author zhangbinglin
 * @Date 2020/6/17 11:54
 **/
public interface ActiveUserMapper {

    /**
     * 活跃用户报表汇总统计
     * @param param 参数
     * @return
     */
    List<LineChartDto> countTotalActiveUserList(Map<String, Object> param);

    /**
     * 活跃用户报表明细项统计
     * @param param 参数
     * @return
     */
    List<LineChartDto> countActiveUserList(Map<String, Object> param);
}
