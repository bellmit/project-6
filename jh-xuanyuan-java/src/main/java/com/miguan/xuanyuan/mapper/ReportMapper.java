package com.miguan.xuanyuan.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.github.pagehelper.Page;
import com.miguan.xuanyuan.vo.LineChartVo;
import com.miguan.xuanyuan.vo.ReportParamVo;
import com.miguan.xuanyuan.vo.ReportTableVo;
import com.miguan.xuanyuan.vo.ReportTimeDivisionVo;

import java.util.List;

/**
 * @Description 报表mapper
 **/
@DS("xy_report")
public interface ReportMapper {

    List<LineChartVo> getReportLineData(ReportParamVo params);

    Page<ReportTableVo> pageReportTableList(ReportParamVo params);

    List<ReportTimeDivisionVo> findTimeDivisionList(Long id, Integer type, String date, int noDesign, String name);
}
