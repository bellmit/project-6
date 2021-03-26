package com.miguan.reportview.controller;

import cn.afterturn.easypoi.excel.annotation.Excel;
import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.google.common.collect.Lists;
import com.miguan.reportview.common.enmus.PlatEnmu;
import com.miguan.reportview.common.exception.NullParameterException;
import com.miguan.reportview.common.exception.ResultCheckException;
import com.miguan.reportview.common.utils.AmpFiled;
import com.miguan.reportview.common.utils.ExcelUtils;
import com.miguan.reportview.controller.base.BaseController;
import com.miguan.reportview.dto.AdErrorDto;
import com.miguan.reportview.dto.ChartAndTableDto;
import com.miguan.reportview.dto.DisassemblyChartDto;
import com.miguan.reportview.dto.ResponseEntity;
import com.miguan.reportview.entity.App;
import com.miguan.reportview.service.IAdAdvertService;
import com.miguan.reportview.service.IAdErrorService;
import com.miguan.reportview.service.IAppService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.apache.commons.lang3.StringUtils.isAnyBlank;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author zhongli
 * @date 2020-07-30 
 *
 */
@Api(value = "广告行为", tags = {"广告行为"})
@RestController
public class AdErrorController extends BaseController {

    @Autowired
    private IAdErrorService adErrorService;
    @Autowired
    private IAppService appService;
    @Autowired
    private IAdAdvertService adAdvertService;


    @ApiOperation(value = "广告错误")
    @PostMapping("api/aderror/sta/getdata")
    public ResponseEntity getAdErrorData(
            @ApiParam(value = "指标 多个用,隔开;1=请求总数2=错误数3=错误率", required = true) String showType,
            @ApiParam(value = "应用马甲包 多个用,隔开") String appPackage,
            @ApiParam(value = "应用版本号 多个用,隔开") String appVersion,
            @ApiParam(value = "是否新用户 多个用,隔开;true=新用户，false=老用户") String isNewUser,
            @ApiParam(value = "父渠道ID 多个用,隔开;") String pChannelId,
            @ApiParam(value = "渠道ID 多个用,隔开;") String channelId,
            @ApiParam(value = "广告位置key 多个用,隔开") String adspace,
            @ApiParam(value = "代码位ID 多个用,隔开") String adcode,
            @ApiParam(value = "平台KEY 多个用,隔开") String plat,
            @ApiParam(value = "机型 多个用,隔开") String model,
            @ApiParam(value = "应用::广告位置key 多个用,隔开") String appAdspace,
            @ApiParam(value = "分组类型 多个用,隔开; 1=应用2=版本3=新老用户4=渠道5=广告位置6=代码位7=机型8=错误类型9=父渠道10=平台") String groupType,
            @ApiParam(value = "app类型：1=西柚视频,2=炫来电(默认值为1)") @RequestParam(defaultValue = "1") Integer appType,
            @ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd", required = true) String startDate,
            @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd", required = true) String endDate) {
        if (isAnyBlank(showType, startDate, endDate)) {
            throw new NullParameterException();
        }
        List<AdErrorDto> list = adErrorService.getData(isBlank(isNewUser) ? null : Boolean.valueOf(isNewUser),
                seqArray2List(appPackage), seqArray2List(appVersion),
                seqArray2List(pChannelId),
                seqArray2List(channelId), seqArray2List(adspace),
                seqArray2List(adcode), seqArray2List(plat),
                seqArray2List(model),
                seqArray2List(appAdspace),
                seqArray2List(groupType),
                appType,startDate, endDate);
        if (isEmpty(list)) {
            throw new ResultCheckException();
        }
        String[] arrayS = showType.split(SEQ);
        List<DisassemblyChartDto> chartData = list.stream().flatMap(e -> {
            return Stream.of(arrayS).filter(StringUtils::isNotBlank).map(String::trim).mapToInt(Integer::parseInt).mapToObj(type -> {
                try {
                    return this.buildErrorChartDto(e, type);
                } catch (Exception ex) {
                    throw new ResultCheckException(ex);
                }

            });
        }).collect(Collectors.toList());
        chartData = this.top10(chartData);
        return success(new ChartAndTableDto(chartData, list));
    }

    Map<Integer, AmpFiled> ampForError = FieldUtils.getFieldsListWithAnnotation(AdErrorDto.class, ApiModelProperty.class)
            .stream().map(e -> {
                ApiModelProperty p = e.getAnnotation(ApiModelProperty.class);
                return new AmpFiled(p.position(), p.notes(), p.value(), e);
            }).collect(Collectors.toMap(AmpFiled::getPosition, e -> e));

    private DisassemblyChartDto buildErrorChartDto(AdErrorDto d, int type) throws IllegalAccessException {
        AmpFiled p = ampForError.get(type);
        if (p == null) {
            throw new NullParameterException(() -> String.format("未找到com.miguan.reportview.dto.ChannelDataDto.class 的相关属性ApiModelProperty#position = %d 的定义", type));
        }
        String name = p.getName();
        App app = appService.getAppByPackageName(d.getPackageName());
        name = name.concat(app == null ? "" : NAME_SEQ.concat(app.getName()));
        name = name.concat(isBlank(d.getAppVersion()) ? "" : NAME_SEQ.concat(d.getAppVersion()));
        name = name.concat(isBlank(d.getModel()) ? "" : NAME_SEQ.concat(d.getModel()));
        name = name.concat(isBlank(d.getIsNew()) ? "" : NAME_SEQ.concat("1".equals(d.getIsNew()) ? "新用户" : "老用户"));
        name = name.concat(isBlank(d.getFatherChannel()) ? "" : NAME_SEQ.concat(d.getFatherChannel()));
        name = name.concat(isBlank(d.getChannel()) ? "" : NAME_SEQ.concat(d.getChannel()));
        String plat = d.getFlat();
        plat = isBlank(d.getFlat()) ? "" : NAME_SEQ.concat(PlatEnmu.valueOf(plat) == null ? d.getFlat() : PlatEnmu.getValueByBiCode(plat));
        name = name.concat(plat);
        name = name.concat(isBlank(d.getAdSpace()) ? "" : NAME_SEQ.concat(adAdvertService.getAdSpaceNname(d.getAdSpace())));
        name = name.concat(isBlank(d.getAdCode()) ? "" : NAME_SEQ.concat(d.getAdCode()));
        name = name.concat(isBlank(d.getMsg()) ? "" : NAME_SEQ.concat(d.getMsg()));

        DisassemblyChartDto dto = new DisassemblyChartDto();
        dto.setDate(d.getDate());
        dto.setType(name);
        double vaule;
        if (type == 1) {
            vaule = d.getReqNum();
        } else if (type == 2) {
            vaule = d.getErrNum();
        } else if (type == 3) {
            vaule = d.getErrRate();
        } else {
            //柱状图显示错误数
            vaule = d.getErrNum();
        }
        dto.setValue(vaule);
        dto.setFormart(p.getNotes());
        return dto;
    }

    @ApiOperation(value = "广告错误导出")
    @GetMapping("api/aderror/sta/export")
    public void export1(HttpServletResponse response,
                        @ApiParam(value = "应用马甲包 多个用,隔开") String appPackage,
                        @ApiParam(value = "应用版本号 多个用,隔开") String appVersion,
                        @ApiParam(value = "是否新用户 多个用,隔开;true=新用户，false=老用户") String isNewUser,
                        @ApiParam(value = "父渠道ID 多个用,隔开;") String pChannelId,
                        @ApiParam(value = "渠道ID 多个用,隔开;") String channelId,
                        @ApiParam(value = "广告位置key 多个用,隔开") String adspace,
                        @ApiParam(value = "代码位ID 多个用,隔开") String adcode,
                        @ApiParam(value = "平台KEY 多个用,隔开") String plat,
                        @ApiParam(value = "机型 多个用,隔开") String model,
                        @ApiParam(value = "应用::广告位置key 多个用,隔开") String appAdspace,
                        @ApiParam(value = "分组类型 多个用,隔开; 1=应用2=版本3=新老用户4=渠道5=广告位置6=代码位7=机型8=错误类型9=父渠道10=平台") String groupType,
                        @ApiParam(value = "app类型：1=西柚视频,2=炫来电(默认值为1)") @RequestParam(defaultValue = "1") Integer appType,
                        @ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd", required = true) String startDate,
                        @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd", required = true) String endDate) throws IOException {
        if (isAnyBlank(startDate, endDate)) {
            throw new NullParameterException();
        }
        List<String> groups = seqArray2List(groupType);
        List<AdErrorDto> list = adErrorService.getData(isBlank(isNewUser) ? null : Boolean.valueOf(isNewUser),
                seqArray2List(appPackage), seqArray2List(appVersion),
                seqArray2List(pChannelId),
                seqArray2List(channelId), seqArray2List(adspace),
                seqArray2List(adcode), seqArray2List(plat),
                seqArray2List(model),
                seqArray2List(appAdspace),
                groups,
                appType,startDate, endDate);
        if (isEmpty(list)) {
            throw new ResultCheckException();
        }
        ExportParams params = new ExportParams("广告错误信息", "广告错误", ExcelType.XSSF);
//        params.setCreateHeadRows(false);
        //过滤掉不需要的字段
        ArrayList<String> allGroup = Lists.newArrayList("1", "2", "3", "4", "5","6","7","8","9");
        if (!isEmpty(groups)) {
            allGroup.removeAll(groups);
        }
        params.setExclusions(poiExclusions(allGroup, groupNames));
        list.forEach(d -> {
            App app = appService.getAppByPackageName(d.getPackageName());
            d.setPackageName(app == null ? null : app.getName());
            d.setIsNew(isBlank(d.getIsNew()) ? null : "1".equals(d.getIsNew()) ? "新用户" : "老用户");
            d.setAdSpace(isBlank(d.getAdSpace()) ? null : adAdvertService.getAdSpaceNname(d.getAdSpace()));
            String platName = isBlank(d.getFlat()) ? null : PlatEnmu.getValueByBiCode(d.getFlat());
            d.setFlat(platName);

        });
        ExcelUtils.defaultExport(list, AdErrorDto.class, "广告错误", response, params);
    }

    private final List<String> groupNames = Lists.newArrayList("应用","版本号","是否新用户","父渠道","渠道","平台","广告位","代码位","机型");
}
