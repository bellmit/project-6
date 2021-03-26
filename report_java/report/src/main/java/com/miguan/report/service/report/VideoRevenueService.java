package com.miguan.report.service.report;

import com.github.pagehelper.PageInfo;
import com.miguan.report.dto.LineChartDto;
import com.miguan.report.dto.RevBarDto;

import java.util.List;

/**
 * @Description 视频广告数据总览-> 总营收报表service
 * @Author zhangbinglin
 * @Date 2020/6/17 14:09
 **/
public interface VideoRevenueService {

    /**
     * 统计营收数据
     *
     * @param startDate 统计开始时间
     * @param endDate   统计结束时间
     * @param appType   app类型：1=西柚视频,2=炫来电
     * @param type      统计类型：1=按app统计,2=按平台统计
     * @param totalName 广告位,为空的话就是统计全部广告位
     * @param timeType  时间类型：1=按日，2=按周，3=按月(默认值为1)
     * @return
     */
    List<LineChartDto> countLineRevenueChart(String startDate, String endDate, Integer appType, Integer type, String totalName, Integer timeType);

    /**
     *
     * @param startDate 开始时间
     * @param endDate 结束时间
     * @param appType  app类型：1=西柚视频,2=炫来电
     * @param timeType 时间类型：1=按日，2=按周，3=按月(默认值为1)
     * @return
     */
    RevBarDto countBarRevenueChar(String startDate, String endDate, Integer appType, Integer timeType);
}
