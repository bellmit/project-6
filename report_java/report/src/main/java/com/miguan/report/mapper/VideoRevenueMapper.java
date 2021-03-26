package com.miguan.report.mapper;

import com.miguan.report.dto.LineChartDto;
import com.miguan.report.vo.RevenuePlatformVo;

import java.util.List;
import java.util.Map;

/**
 * @Description 视频广告数据总览-> 总营收报表mapper
 * @Author zhangbinglin
 * @Date 2020/6/17 11:54
 **/
public interface VideoRevenueMapper {


    /**
     * 统计营收折线图
     * @param param 参数
     * @return 图表的数据列表
     */
    List<LineChartDto> countLineRevenueChart(Map<String, Object> param);

    /**
     * 统计营收柱状图基础数据
     * @param param
     * @return
     */
    List<RevenuePlatformVo> countBarRevenueChar(Map<String, Object> param);

    /**
     * 查询所有要统计的项
     * @param param
     * @return
     */
    List<RevenuePlatformVo> findAllItem(Map<String, Object> param);

}
