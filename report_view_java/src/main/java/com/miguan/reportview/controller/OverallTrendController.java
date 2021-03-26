package com.miguan.reportview.controller;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.miguan.reportview.common.exception.NullParameterException;
import com.miguan.reportview.common.exception.ResultCheckException;
import com.miguan.reportview.common.utils.AmpFiled;
import com.miguan.reportview.common.utils.ExcelUtils;
import com.miguan.reportview.controller.base.BaseController;
import com.miguan.reportview.dto.DisassemblyChartDto;
import com.miguan.reportview.dto.ResponseEntity;
import com.miguan.reportview.service.IOverallTrendService;
import com.miguan.reportview.vo.LdOverallTrendVo;
import com.miguan.reportview.vo.OverallTrendVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author zhongli
 * @date 2020-07-30 
 *
 */
@Api(value = "数据概览-整体趋势", tags = {"数据概览"})
@RestController
public class OverallTrendController extends BaseController {

    @Autowired
    private IOverallTrendService overallTrendService;

    @ApiOperation(value = "整体趋势拆线图")
    @PostMapping("api/overalltrend/sta/getdata")
    //成本相关指标前端没看到，好像计算了，但是没用。如果需要的话，不要从app_cost读取
    public ResponseEntity<List<DisassemblyChartDto>> getData(@ApiParam(value = "显示类型; 1=新增用户2=活跃用户3=播放视频数4=日活用户价值5=日活客单成本6=新用户次留率7=活跃用户次留率" +
            "8=人均有效播放数9=广告展现量10=广告点击量11=广告点击率12=人均广告展示13=人均广告点击14=平均日使用时长15=人均播放时长16=每曝光播放时长17=每播放播放时长", required = true)
                                                                     String showType,
                                                             @ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd", required = true)
                                                                     String startDate,
                                                             @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd", required = true)
                                                                     String endDate) {
        if (StringUtils.isAnyBlank(showType, startDate, endDate)) {
            throw new NullParameterException();
        }
        List<String> showTypes = seqArray2List(showType);
        List<OverallTrendVo> list = overallTrendService.getData(startDate, endDate, showTypes);
        if (isEmpty(list)) {
            throw new ResultCheckException(() -> "未找到任何数据");
        }
        List<DisassemblyChartDto> rdata = list.stream()
                .flatMap(d -> showTypes.stream().mapToInt(Integer::parseInt)
                        .mapToObj(e -> this.getNameForShowType(d, e))).collect(Collectors.toList());
        rdata = this.top10(rdata);
        return success(rdata);
    }


    @ApiOperation(value = "整体趋势表格")
    @PostMapping("api/overalltrend/sta/gettable")
    public ResponseEntity<List<OverallTrendVo>> getTabelData(
            @ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd", required = true)
                    String startDate,
            @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd", required = true)
                    String endDate) {

        if (StringUtils.isAnyBlank(startDate, endDate)) {
            throw new NullParameterException();
        }
        List<OverallTrendVo> list = overallTrendService.getData(startDate, endDate);
        if (isEmpty(list)) {
            throw new ResultCheckException(() -> "未找到任何数据");
        }
        return success(list);
    }

    @ApiOperation(value = "导出")
    @GetMapping("api/overalltrend/sta/export")
    public void export(HttpServletResponse response,
                       @ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd", required = true) String startDate,
                       @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd", required = true) String endDate,
                       @ApiParam(value = "显示类型; 1=新增用户2=活跃用户3=播放视频数4=日活用户价值5=日活客单成本6=新用户次留率7=活跃用户次留率" +
                               "8=人均有效播放数9=广告展现量10=广告点击量11=广告点击率12=人均广告展示13=人均广告点击14=平均日使用时长15=人均播放时长16=每曝光播放时长17=每播放播放时长", required = true)
                                   String showType) throws IOException {
        if (StringUtils.isAnyBlank(startDate, endDate)) {
            throw new NullParameterException();
        }
        List<String> showTypes = seqArray2List(showType);
        List<OverallTrendVo> list = overallTrendService.getData(startDate, endDate, showTypes);
        if (isEmpty(list)) {
            throw new ResultCheckException(() -> "未找到任何数据");
        }
        ExportParams params = new ExportParams("整体趋势", "整体趋势", ExcelType.XSSF);

        params.setExclusions(poiExclusions(showType, amp));
//        params.setCreateHeadRows(false);
        ExcelUtils.defaultExport(list, OverallTrendVo.class, "整体趋势", response, params);

    }
    Map<Integer, AmpFiled> amp = FieldUtils.getFieldsListWithAnnotation(OverallTrendVo.class, ApiModelProperty.class)
            .stream().map(e -> {
                ApiModelProperty p = e.getAnnotation(ApiModelProperty.class);
                return new AmpFiled(p.position(), p.notes(), p.value(), e);
            }).collect(Collectors.toMap(AmpFiled::getPosition, e -> e));

    Map<Integer, AmpFiled> ldAmp = FieldUtils.getFieldsListWithAnnotation(LdOverallTrendVo.class, ApiModelProperty.class)
            .stream().map(e -> {
                ApiModelProperty p = e.getAnnotation(ApiModelProperty.class);
                return new AmpFiled(p.position(), p.notes(), p.value(), e);
            }).collect(Collectors.toMap(AmpFiled::getPosition, e -> e));

    private DisassemblyChartDto getNameForShowType(OverallTrendVo vo, int showType) {
        AmpFiled p = amp.get(showType);
        if (p == null) {
            throw new NullParameterException(() -> String.format("未找到com.miguan.reportview.dto.OverallTrendVo.class 的相关属性ApiModelProperty#position = %d 的定义", showType));
        }
        try{
            String type = p.getName();
            Double value = (Double) FieldUtils.readField(p.getField(), vo, true);
            DisassemblyChartDto dto = new DisassemblyChartDto();
            dto.setDate(vo.getDate());
            dto.setType(type);
            dto.setValue(value == null ? 0 : value.doubleValue());
            dto.setFormart(p.getNotes());
            return dto;
        }catch(Exception e){
            throw new ResultCheckException(e);
        }
    }

    @ApiOperation(value = "来电整体趋势拆线图")
    @PostMapping("api/overalltrend/sta/getlddata")
    public ResponseEntity<List<DisassemblyChartDto>> getLdData(@ApiParam(value = "显示类型; 1=新增用户,2=活跃用户,3=详情页播放用户,4=设置用户,5=成功设置用户,6=人均观看次数,7=人均设置次数,8=人均成功设置次数,9=广告展现用户,10=广告点击用户,11=广告展现量,12=广告点击量,13=人均广告展现,14=人均广告点击,15=广告点击率,16=广告点击用户占比,17=人均APP启动次数,18=活跃用户留存率,19=新增用户留存率", required = true)
                                                                     String showType,
                                                             @ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd", required = true)
                                                                     String startDate,
                                                             @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd", required = true)
                                                                     String endDate) {
        if (StringUtils.isAnyBlank(showType, startDate, endDate)) {
            throw new NullParameterException();
        }
        List<String> showTypes = seqArray2List(showType);
        List<LdOverallTrendVo> list = overallTrendService.getLdData(startDate, endDate, showTypes);
        if (isEmpty(list)) {
            throw new ResultCheckException(() -> "未找到任何数据");
        }
        List<DisassemblyChartDto> rdata = list.stream()
                .flatMap(d -> showTypes.stream().mapToInt(Integer::parseInt)
                        .mapToObj(e -> this.getLdNameForShowType(d, e))).collect(Collectors.toList());
        rdata = this.top10(rdata);
        return success(rdata);
    }

    private DisassemblyChartDto getLdNameForShowType(LdOverallTrendVo vo, int showType) {
        AmpFiled p = ldAmp.get(showType);
        if (p == null) {
            throw new NullParameterException(() -> String.format("未找到com.miguan.reportview.dto.LdOverallTrendVo.class 的相关属性ApiModelProperty#position = %d 的定义", showType));
        }
        try{
            String type = p.getName();
            Double value = Double.parseDouble(String.valueOf(FieldUtils.readField(p.getField(), vo, true)));
            DisassemblyChartDto dto = new DisassemblyChartDto();
            dto.setDate(vo.getDate());
            dto.setType(type);
            dto.setValue(value == null ? 0 : value.doubleValue());
            dto.setFormart(p.getNotes());
            return dto;
        }catch(Exception e){
            throw new ResultCheckException(e);
        }
    }

    @ApiOperation(value = "来电整体趋势表格")
    @PostMapping("api/overalltrend/sta/getldtable")
    public ResponseEntity<List<LdOverallTrendVo>> getTabelLdData(
            @ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd", required = true)
                    String startDate,
            @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd", required = true)
                    String endDate) {
        if (StringUtils.isAnyBlank(startDate, endDate)) {
            throw new NullParameterException();
        }
        List<LdOverallTrendVo> list = overallTrendService.getLdData(startDate, endDate);
        if (isEmpty(list)) {
            throw new ResultCheckException(() -> "未找到任何数据");
        }
        return success(list);
    }

    @ApiOperation(value = "来电导出")
    @GetMapping("api/overalltrend/sta/ld/export")
    public void ldExport(HttpServletResponse response,
                         @ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd", required = true) String startDate,
                         @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd", required = true) String endDate,
                         @ApiParam(value = "显示类型; 1=新增用户,2=活跃用户,3=详情页播放用户,4=设置用户,5=成功设置用户,6=人均观看次数,7=人均设置次数,8=人均成功设置次数,9=广告展现用户,10=广告点击用户,11=广告展现量,12=广告点击量,13=人均广告展现,14=人均广告点击,15=广告点击率,16=广告点击用户占比,17=人均APP启动次数,18=活跃用户留存率,19=新增用户留存率", required = true)
                                 String showType) throws IOException {
        if (StringUtils.isAnyBlank(startDate, endDate)) {
            throw new NullParameterException();
        }
        List<LdOverallTrendVo> list = overallTrendService.getLdData(startDate, endDate);
        if (isEmpty(list)) {
            throw new ResultCheckException(() -> "未找到任何数据");
        }
        ExportParams params = new ExportParams("整体趋势", "整体趋势", ExcelType.XSSF);
        //导出排除字段/属性
        params.setExclusions(poiExclusions(showType, ldAmp));
//        params.setCreateHeadRows(false);
        ExcelUtils.defaultExport(list, LdOverallTrendVo.class, "整体趋势", response, params);

    }
}
