package com.miguan.report.controller;

import com.google.common.collect.Maps;
import com.miguan.report.common.util.DateUtil;
import com.miguan.report.common.util.ExportXlsxUtil;
import com.miguan.report.dto.DisassemblyChartDto;
import com.miguan.report.dto.ResponseDto;
import com.miguan.report.service.report.PerShowService;
import com.miguan.report.vo.PerShowVo;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.miguan.report.common.constant.CommonConstant.SUM_NAME;
import static com.miguan.report.common.util.AppNameUtil.convertPlatType2name;
import static com.miguan.report.common.util.NumCalculationUtil.roundHalfUpDouble;

/**人均展现接口
 * @author zhongli
 * @date 2020-06-19 
 *
 */
@Slf4j
public abstract class PerShowController extends BaseController {

    @Resource
    private PerShowService perShowService;

    abstract int appType();


    @ApiOperation(value = "人均展现(拆线图)")
    @PostMapping(value = "/data/load/dischart")
    @ApiResponse(code = 200, message = "", response = DisassemblyChartDto.class, responseContainer = "List")
    public ResponseDto<List<DisassemblyChartDto>> initload(
            @ApiParam(value = "统计日期,格式：yyyy-MM-dd", required = true) String startDate,
            @ApiParam(value = "统计日期,格式：yyyy-MM-dd", required = true) String endDate,
            @ApiParam(value = "显示类型：1=按天统计 2=按周统计 3=按月统计", required = true) int showDateType,
            @ApiParam(value = "统计类型：1=按应用统计 2=按平台统计", required = true) int groupType) {
        return success(loadData(startDate, endDate, showDateType, groupType));
    }

    private List<DisassemblyChartDto> loadData(String startDate, String endDate, int showDateType, int groupType) {
        List<PerShowVo> data = perShowService.loadStaData(startDate, endDate, null, showDateType, groupType, appType());
        if (data == null) {
            log.warn("人均展现(拆线图) 未从库里查询到任何数据.");
            return null;
        }
        //汇总 key 日期/( groupType == 2)平台+日期 value所有应用
        Map<String, List<PerShowVo>> cdata = data.stream().map(d -> {
            if (showDateType == 2) {
                d.setDates(d.getMinDate().concat("至").concat(d.getMaxDate()));
            }
            return d;
        }).collect(Collectors
                .groupingBy(groupType == 1 ? PerShowVo::getDates : e -> String.valueOf(e.getPlatType()).concat(e.getDates())));
        //数据类型转成DisassemblyChartDto
        List<DisassemblyChartDto> rdata = data.stream().map(d -> new DisassemblyChartDto(d.getDates(), d.getName(), roundHalfUpDouble(d.getDataValue()))).collect(Collectors.toList());
        if (appType() == 1) {
            //汇总数据类型转成DisassemblyChartDto
            List<DisassemblyChartDto> tdata = cdata.entrySet().stream()
                    .map(et -> {
                        String date = groupType == 1 ? et.getKey() : et.getKey().substring(1);
                        double sumMol = et.getValue().stream().mapToDouble(PerShowVo::getSumMol).sum();
                        double sumDem = et.getValue().stream().mapToDouble(PerShowVo::getSumDem).sum();
                        double dataValue = sumDem == 0 ? 0 : roundHalfUpDouble(sumMol / sumDem);
                        return new DisassemblyChartDto(date,
                                groupType == 1 ? SUM_NAME : convertPlatType2name(SUM_NAME, -1, Integer.valueOf(et.getKey().substring(0, 1))),
                                dataValue);
                    })
                    .collect(Collectors.toList());

            rdata.addAll(tdata);
        }
        //排序后返回
        sort(rdata);
        return rdata;
    }

    @ApiOperation(value = "广告人均展现(拆线图)")
    @PostMapping(value = "/data/load/ad/dischart")
    @ApiResponse(code = 200, message = "", response = DisassemblyChartDto.class, responseContainer = "List")
    public ResponseDto<List<DisassemblyChartDto>> initload(
            @ApiParam(value = "统计日期,格式：yyyy-MM-dd", required = true) String startDate,
            @ApiParam(value = "统计日期,格式：yyyy-MM-dd", required = true) String endDate,
            @ApiParam(value = "广告位名称", required = true) String adSpace
//            @ApiParam(value = "显示类型：1=按天统计 2=按周统计 3=按月统计", required = true) int showDateType,
//            @ApiParam(value = "统计类型：1=按应用统计 2=按平台统计", required = true) int groupType
    ) {
        List<PerShowVo> data = perShowService.loadStaData(startDate, endDate, adSpace, 1, 1, appType());
        if (data == null) {
            log.warn("人均展现(拆线图) 未从库里查询到任何数据.");
            return successForNotData();
        }
        //汇总
        Map<String, List<PerShowVo>> cdata = data.stream().collect(Collectors.groupingBy(PerShowVo::getDates));
        //数据类型转成DisassemblyChartDto
        List<DisassemblyChartDto> rdata = data.stream().map(d -> new DisassemblyChartDto(d.getDates(), d.getName(), roundHalfUpDouble(d.getDataValue()))).collect(Collectors.toList());
        //汇总数据类型转成DisassemblyChartDto
        if (appType() == 1) {
            List<DisassemblyChartDto> tdata = cdata.entrySet().stream()
                    .map(et -> {
                        double sumMol = et.getValue().stream().mapToDouble(PerShowVo::getSumMol).sum();
                        double sumDem = et.getValue().stream().mapToDouble(PerShowVo::getSumDem).sum();
                        double dataValue = roundHalfUpDouble(sumMol / sumDem);
                        return new DisassemblyChartDto(et.getKey(), SUM_NAME, roundHalfUpDouble(dataValue));
                    })
                    .collect(Collectors.toList());
            rdata.addAll(tdata);
        }
        sort(rdata);
        return success(rdata);
    }

    @Autowired
    private ResourceLoader resourceLoader;

    @GetMapping("/data/export")
    @ApiOperation(value = "导出")
    public void download(HttpServletResponse response,
                         @ApiParam(value = "统计日期,格式：yyyy-MM-dd", required = true) String startDate,
                         @ApiParam(value = "统计日期,格式：yyyy-MM-dd", required = true) String endDate,
                         @ApiParam(value = "显示类型：1=按天统计 2=按周统计 3=按月统计", required = true) int showDateType,
                         @ApiParam(value = "统计类型：1=按应用统计 2=按平台统计", required = true) int groupType
    ) {
        List<DisassemblyChartDto> lists = loadData(startDate, endDate, showDateType, groupType);
        if (lists == null) {
            return;
        }
        Map<String, Object> varMap = Maps.newHashMapWithExpectedSize(2);
        varMap.put("name", "人均展现");
        varMap.put("type_name", groupType == 1 ? "应用" : "平台");
        String filename = "人均展现统计表-".concat(DateUtil.yyyyMMdd());
        ExportXlsxUtil.export(response, resourceLoader,
                "export_xlsx/type_details.xlsx", filename, lists, varMap);
    }
}
