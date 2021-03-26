package com.miguan.report.service.report;

import com.miguan.report.dto.LineChartDto;
import com.miguan.report.vo.AppUseTimePandectVo;

import java.util.List;

public interface AppUseTimeService {


    public AppUseTimePandectVo getUseTimePandect(Integer appType, Integer dataType);

    public List<LineChartDto> getUseTimeReportList(Integer appType, String startDateStr, String endDateStr, Integer timeTypeendDate);
}
