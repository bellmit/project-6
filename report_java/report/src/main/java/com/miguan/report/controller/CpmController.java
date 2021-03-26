package com.miguan.report.controller;

import com.google.common.collect.Maps;
import com.miguan.report.common.util.DateUtil;
import com.miguan.report.common.util.ExportXlsxUtil;
import com.miguan.report.dto.LineChartDto;
import com.miguan.report.service.CpmService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@Api(description = "/api/cpm", tags = "千展收益（CPM）")
@Slf4j
@RestController
@RequestMapping("/api/cpm")
public class CpmController {

    @Resource
    private CpmService cpmService;
    @Resource
    private ResourceLoader resourceLoader;

    @ApiOperation(value = "千展收益（CPM）报表数据（折线图和明细）")
    @PostMapping("/countLineCpmChart")
    public List<LineChartDto> countLineCpmChart(@ApiParam(value = "统计开始时间,格式：yyyy-MM-dd", required = true) String startDate,
                                                @ApiParam(value = "统计结束时间,格式：yyyy-MM-dd", required = true) String endDate,
                                                @ApiParam(value = "app类型：1=西柚视频,2=炫来电(默认值为1)") @RequestParam(defaultValue = "1") Integer appType,
                                                @ApiParam(value = "统计类型：1=按应用统计,2=按平台统计(默认值为1)") @RequestParam(required = false, defaultValue = "1") Integer type,
                                                @ApiParam(value = "广告位,为空的话就是统计全部广告位") String totalName,
                                                @ApiParam(value = "时间类型：1=按日，2=按周，3=按月(默认值为1)") @RequestParam(required = false, defaultValue = "1") Integer timeType) {
        return cpmService.countLineCpmChart(startDate, endDate, appType, type, totalName, timeType);
    }

    @ApiOperation(value = "千展收益（CPM）报表数据（导出）")
    @GetMapping("/export")
    public void export(HttpServletResponse response,
                       @ApiParam(value = "统计开始时间,格式：yyyy-MM-dd", required = true) String startDate,
                       @ApiParam(value = "统计结束时间,格式：yyyy-MM-dd", required = true) String endDate,
                       @ApiParam(value = "app类型：1=西柚视频,2=炫来电(默认值为1)") @RequestParam(defaultValue = "1") Integer appType,
                       @ApiParam(value = "统计类型：1=按应用统计,2=按平台统计(默认值为1)") @RequestParam(required = false, defaultValue = "1") Integer type,
                       @ApiParam(value = "广告位,为空的话就是统计全部广告位") String totalName,
                       @ApiParam(value = "时间类型：1=按日，2=按周，3=按月(默认值为1)") @RequestParam(required = false, defaultValue = "1") Integer timeType) {
        List<LineChartDto> list = cpmService.countLineCpmChart(startDate, endDate, appType, type, totalName, timeType);
        Map<String, Object> varMap = Maps.newHashMapWithExpectedSize(2);
        varMap.put("name", "CPM");
        varMap.put("type_name", type == 1 ? "应用" : "平台");
        String filename = "千展收益(CPM)统计表-".concat(DateUtil.yyyyMMdd());
        ExportXlsxUtil.export(response, resourceLoader,
                "export_xlsx/type_details.xlsx", filename, list, varMap);
    }
}
