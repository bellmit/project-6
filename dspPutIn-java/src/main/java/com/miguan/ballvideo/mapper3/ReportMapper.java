package com.miguan.ballvideo.mapper3;

import com.github.pagehelper.Page;
import com.miguan.ballvideo.vo.AdvertGroupListVo;
import com.miguan.ballvideo.vo.LineChartVo;
import com.miguan.ballvideo.vo.ReportParamVo;
import com.miguan.ballvideo.vo.ReportTableVo;

import java.util.List;
import java.util.Map;

/**
 * @Description 报表mapper
 **/
public interface ReportMapper {

    List<LineChartVo> getReportLineData(ReportParamVo params);

    Page<ReportTableVo> pageReportTableList(ReportParamVo params);

    Page<ReportTableVo> pageReportEmpty(ReportParamVo reportParamVo);
}
