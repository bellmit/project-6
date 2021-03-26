package com.miguan.report.mapper;

import com.miguan.report.dto.LineChartDto;
import com.miguan.report.entity.report.AppUseTime;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface AppUseTimeMapper {

    int deleteAllByUseDay(@Param("useDay") String useDay);

    List<AppUseTime> findByAppTypeAndDataTypeAndUseDayOrderByIdDesc(@Param("appType") Integer appType, @Param("dataType") Integer dataType, @Param("useDay") String... useDay);

    List<AppUseTime> findDayReportByAppTypeAndBetweenDay(@Param("appType") Integer appType, @Param("startDate") String startDate, @Param("endDate") String endDate);

    List<LineChartDto> findWeekReportByAppTypeAndBetweenDay(@Param("appType") Integer appType, @Param("startDate") String startDate, @Param("endDate") String endDate);

    List<LineChartDto> findMonthReportByAppTypeAndBetweenDay(@Param("appType") Integer appType, @Param("startDate") String startDate, @Param("endDate") String endDate);

}
