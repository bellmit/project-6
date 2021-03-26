package com.miguan.report.controller;

import com.cgcg.base.core.exception.CommonException;
import com.google.common.collect.Maps;
import com.miguan.report.common.util.DateUtil;
import com.miguan.report.common.util.ExportXlsxUtil;
import com.miguan.report.dto.LineChartDto;
import com.miguan.report.service.report.AppUseTimeService;
import com.miguan.report.task.AppUseTimeTask;
import com.miguan.report.vo.AppUseTimePandectVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Api(description = "/api/appUserTime", tags = "使用时长相关接口")
@RestController
@RequestMapping("/api/appUserTime")
public class AppUseTimeController {

    @Resource
    private AppUseTimeService appUseTimeService;
    @Autowired
    private ResourceLoader resourceLoader;
    @Resource
    private AppUseTimeTask appUseTimeTask;

    @GetMapping("/init")
    public void init(String date){
        if(StringUtils.isBlank(date)){
            return;
        }
        appUseTimeTask.getDateAndSaveList(date);
    }

    @PostMapping("/pandect")
    public AppUseTimePandectVo pandect(
            @ApiParam(name = "appType", value = "应用类型：1 视频, 2 来电", required = true) Integer appType,
            @ApiParam(name = "dataType", value = "数据类型：1 APP类型当日汇总, 2 茜柚视频-Android, 3 果果视频_Android, 4 炫来电-Android", required = true) Integer dataType
    ) {
        return appUseTimeService.getUseTimePandect(appType, dataType);
    }

    @ApiOperation(value = "使用时长报表")
    @PostMapping("/reportList")
    public List<LineChartDto> reportList(
            @ApiParam(name = "appType", value = "应用类型：1 视频, 2 来电", required = true) Integer appType,
            @ApiParam(name = "startDate", value = "起始时间，格式yyyy-MM-dd", required = true) String startDate,
            @ApiParam(name = "endDate", value = "截止时间，格式yyyy-MM-dd", required = true) String endDate,
            @ApiParam(value = "时间类型：1=按日，2=按周，3=按月(默认值为1)") @RequestParam(required = false, defaultValue = "1") Integer timeType
    ) {
        if (appType == null) {
            throw new CommonException("应用类型不能为空");
        }
        if (StringUtils.isBlank(startDate)) {
            throw new CommonException("起始时间不能为空");
        }
        if (StringUtils.isBlank(endDate)) {
            throw new CommonException("截止时间不能为空");
        }
        return appUseTimeService.getUseTimeReportList(appType, startDate, endDate, timeType);
    }


    @ApiOperation(value = "使用时长报表导出")
    @GetMapping("/export")
    public void export(HttpServletResponse response,
                       @ApiParam(name = "appType", value = "应用类型：1 视频, 2 来电", required = true) Integer appType,
                       @ApiParam(name = "startDate", value = "起始时间，格式yyyy-MM-dd", required = true) String startDate,
                       @ApiParam(name = "endDate", value = "截止时间，格式yyyy-MM-dd", required = true) String endDate,
                       @ApiParam(value = "时间类型：1=按日，2=按周，3=按月(默认值为1)") @RequestParam(required = false, defaultValue = "1") Integer timeType
    ) {
        List<LineChartDto> lists = appUseTimeService.getUseTimeReportList(appType, startDate, endDate, timeType);
        Map<String, Object> varMap = Maps.newHashMapWithExpectedSize(2);
        varMap.put("name", "时长(分钟)");
        varMap.put("type_name", "应用");
        String filename = "使用时长报表-".concat(DateUtil.yyyyMMdd());
        ExportXlsxUtil.export(response, resourceLoader, "export_xlsx/type_details.xlsx", filename, lists, varMap);
    }
}
