package com.miguan.reportview.controller;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.miguan.reportview.common.exception.NullParameterException;
import com.miguan.reportview.common.exception.ResultCheckException;
import com.miguan.reportview.common.utils.AmpFiled;
import com.miguan.reportview.common.utils.ExcelUtils;
import com.miguan.reportview.controller.base.BaseController;
import com.miguan.reportview.dto.*;
import com.miguan.reportview.entity.App;
import com.miguan.reportview.service.IAppService;
import com.miguan.reportview.service.IChannelDataService;
import com.miguan.reportview.vo.AdClickNumVo;
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
import java.util.ArrayList;
import java.util.HashMap;
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
@Api(value = "渠道数据", tags = {"渠道数据"})
@RestController
public class ChannelController extends BaseController {

    @Autowired
    private IChannelDataService channelDataService;
    @Autowired
    private IAppService appService;

    @ApiOperation(value = "视频-渠道拆线图和表格数据")
    @PostMapping("api/channel/sta/getdata")
    public ResponseEntity<ChartAndTableDto<DisassemblyChartDto, ChannelDataDto>> getData(
            @ApiParam(value = "指标类型 多个用,隔开; 1=新增用户数2=注册用户数3=有效行为率4=首页浏览率5=播放转化率6=人均播放数7=有效播放率8=人均有效播放数9=人均播放时长10=有效播放视频率11=广告点击转化率12=人均广告点击13=新用户留存14=新增用户数（友盟）", required = true)
                    String showType,
            @ApiParam(value = "应用马甲包 多个用,隔开")
                    String appPackage,
            @ApiParam(value = "应用版本号 多个用,隔开")
                    String appVersion,
            @ApiParam(value = "是否新用户 多个用,隔开;true,false")
                    String isNewUser,
            @ApiParam(value = "父渠道ID 多个用,隔开;")
                    String pChannelId,
            @ApiParam(value = "渠道ID 多个用,隔开;")
                    String channelId,
            @ApiParam(value = "分组类型 多个用,隔开; 1=应用2=版本3=新老用户4=渠道5=父渠道")
                    String groupType,
            @ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd", required = true)
                    String startDate,
            @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd", required = true)
                    String endDate) {
        if (isAnyBlank(showType, startDate, endDate)) {
            throw new NullParameterException();
        }
        List<ChannelDataDto> list = channelDataService.getData(isBlank(isNewUser) || isNewUser.indexOf(",") >= 0 ? null : Boolean.valueOf(isNewUser),
                seqArray2List(appPackage), seqArray2List(appVersion),
                seqArray2List(pChannelId),
                seqArray2List(channelId), seqArray2List(groupType),
                startDate, endDate);
        if (isEmpty(list)) {
            throw new ResultCheckException();
        }
        String[] arrayS = showType.split(SEQ);
        List<DisassemblyChartDto> chartData = list.stream().flatMap(e -> {
            return Stream.of(arrayS).filter(StringUtils::isNotBlank).map(String::trim).mapToInt(Integer::parseInt).mapToObj(type -> {
                try {
                    return this.buildChartDto(e, type);
                } catch (Exception ex) {
                    throw new ResultCheckException(ex);
                }

            });
        }).collect(Collectors.toList());
        chartData = this.top10(chartData);
        return success(new ChartAndTableDto(chartData, list));
    }

    Map<Integer, AmpFiled> amp = FieldUtils.getFieldsListWithAnnotation(ChannelDataDto.class, ApiModelProperty.class)
            .stream().map(e -> {
                ApiModelProperty p = e.getAnnotation(ApiModelProperty.class);
                return new AmpFiled(p.position(), p.notes(), p.value(), e);
            }).collect(Collectors.toMap(AmpFiled::getPosition, e -> e));

    private DisassemblyChartDto buildChartDto(ChannelDataDto d, int type) throws IllegalAccessException {
        AmpFiled p = amp.get(type);
        if (p == null) {
            throw new NullParameterException(() -> String.format("未找到com.miguan.reportview.dto.ChannelDataDto.class 的相关属性ApiModelProperty#position = %d 的定义", type));
        }
        String name = p.getName();
        App app = appService.getAppByPackageName(d.getPackageName());
        name = name.concat(app == null ? "" : NAME_SEQ.concat(app.getName()));
        name = name.concat(isBlank(d.getAppVersion()) ? "" : NAME_SEQ.concat(d.getAppVersion()));
        name = name.concat(isBlank(d.getIsNew()) ? "" : NAME_SEQ.concat("1".equals(d.getIsNew()) ? "新用户" : "老用户"));
        name = name.concat(isBlank(d.getFatherChannel()) ? "" : NAME_SEQ.concat(d.getFatherChannel()));
        name = name.concat(isBlank(d.getChannel()) ? "" : NAME_SEQ.concat(d.getChannel()));

        Object vaule = FieldUtils.readField(p.getField(), d, true);
        DisassemblyChartDto dto = new DisassemblyChartDto();
        dto.setDate(d.getDate());
        dto.setType(name);
        dto.setValue(vaule == null ? Double.NaN : Double.parseDouble(vaule.toString()));
        dto.setFormart(p.getNotes());
        return dto;
    }

    @ApiOperation(value = "视频-渠道数据导出")
    @GetMapping("api/channel/sta/export")
    public void export(HttpServletResponse response,
                       @ApiParam(value = "应用马甲包 多个用,隔开")
                               String appPackage,
                       @ApiParam(value = "应用版本号 多个用,隔开")
                               String appVersion,
                       @ApiParam(value = "是否新用户 多个用,隔开;true=新用户，false=老用户")
                               String isNewUser,
                       @ApiParam(value = "父渠道ID 多个用,隔开;")
                               String pChannelId,
                       @ApiParam(value = "渠道ID 多个用,隔开;")
                               String channelId,
                       @ApiParam(value = "分组类型 多个用,隔开; 1=应用2=版本3=新老用户4=渠道5=父渠道")
                               String groupType,
                       @ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd", required = true)
                               String startDate,
                       @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd", required = true)
                               String endDate,
                       @ApiParam(value = "指标类型 多个用,隔开; 1=新增用户数2=注册用户数3=有效行为率4=首页浏览率5=播放转化率6=人均播放数7=有效播放率8=人均有效播放数9=人均播放时长10=有效播放视频率11=广告点击转化率12=人均广告点击13=新用户留存", required = true)
                                   String showType) throws IOException {

        if (isAnyBlank(startDate, endDate)) {
            throw new NullParameterException();
        }
        List<String> groups = seqArray2List(groupType);
        List<ChannelDataDto> list = channelDataService.getData(isBlank(isNewUser) || isNewUser.indexOf(",") >= 0 ? null : Boolean.valueOf(isNewUser),
                seqArray2List(appPackage), seqArray2List(appVersion),
                seqArray2List(pChannelId),
                seqArray2List(channelId), groups,
                startDate, endDate);
        if (isEmpty(list)) {
            throw new ResultCheckException();
        }
        list.forEach(d -> {
            App app = appService.getAppByPackageName(d.getPackageName());
            d.setPackageName(app == null ? "" : app.getName());
            d.setIsNew(isBlank(d.getIsNew()) ? "" : "1".equals(d.getIsNew()) ? "新用户" : "老用户");
        });
        ExportParams params = new ExportParams("渠道数据", "渠道数据", ExcelType.XSSF);
//        params.setCreateHeadRows(false);
        //过滤掉不需要的字段
        ArrayList<String> allGroup = Lists.newArrayList("1", "2", "3", "4","5");
        if (!isEmpty(groups)) {
            allGroup.removeAll(groups);
        }
        //导出排除字段/属性
        String[] exclusions1 = poiExclusions(allGroup, groupNames);
        String[] exclusions2 = poiExclusions(showType, amp);
        params.setExclusions(mergeExclusions(exclusions1, exclusions2));

        ExcelUtils.defaultExport(list, ChannelDataDto.class, "渠道数据", response, params);
    }

    private final List<String> groupNames = Lists.newArrayList("应用", "版本号", "是否新用户", "渠道", "父渠道");


    @ApiOperation(value = "来电-渠道拆线图和表格数据")
    @PostMapping("api/channel/sta/getlddata")
    public ResponseEntity<ChartAndTableDto<DisassemblyChartDto, LdChannelDataDto>> getLdData(
            @ApiParam(value = "指标类型 多个用,隔开; 1=新增用户2=注册用户3=首页浏览率4=用户转化率5=设置转化率6=来电设置成功率7=壁纸设置成功率8=锁屏设置成功率9=QQ/微信皮肤设置成功率10=铃声设置成功率11=广告点击转化率12=人均广告点击次数13=新用户留存14=新增用户数（友盟）", required = true)
                    String showType,
            @ApiParam(value = "应用版本号 多个用,隔开") String appVersion,
            @ApiParam(value = "是否新用户 多个用,隔开;true,false") String isNewUser,
            @ApiParam(value = "渠道ID 多个用,隔开;") String channelId,
            @ApiParam(value = "父渠道ID 多个用,隔开;") String pChannelId,
            @ApiParam(value = "分组类型 多个用,隔开; 2=版本3=新老用户4=渠道5=父渠道") String groupType,
            @ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd", required = true) String startDate,
            @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd", required = true) String endDate) {
        if (isAnyBlank(showType, startDate, endDate)) {
            throw new NullParameterException();
        }
        Boolean isNew = isBlank(isNewUser) || isNewUser.indexOf(",") >= 0 ? null : Boolean.valueOf(isNewUser);
        List<LdChannelDataDto> list = channelDataService.getLdData(seqArray2List(appVersion),isNew, seqArray2List(pChannelId),seqArray2List(channelId),
                seqArray2List(groupType),startDate, endDate);
        if (isEmpty(list)) {
            throw new ResultCheckException();
        }
        String[] arrayS = showType.split(SEQ);
        List<DisassemblyChartDto> chartData = list.stream().flatMap(e -> {
            return Stream.of(arrayS).filter(StringUtils::isNotBlank).map(String::trim).mapToInt(Integer::parseInt).mapToObj(type -> {
                try {
                    return this.buildLdChartDto(e, type);
                } catch (Exception ex) {
                    throw new ResultCheckException(ex);
                }

            });
        }).collect(Collectors.toList());
        chartData = this.top10(chartData);
        return success(new ChartAndTableDto(chartData, list));
    }

    Map<Integer, AmpFiled> ldAmp = FieldUtils.getFieldsListWithAnnotation(LdChannelDataDto.class, ApiModelProperty.class)
            .stream().map(e -> {
                ApiModelProperty p = e.getAnnotation(ApiModelProperty.class);
                return new AmpFiled(p.position(), p.notes(), p.value(), e);
            }).collect(Collectors.toMap(AmpFiled::getPosition, e -> e));

    private DisassemblyChartDto buildLdChartDto(LdChannelDataDto d, int type) throws IllegalAccessException {
        AmpFiled p = ldAmp.get(type);
        if (p == null) {
            throw new NullParameterException(() -> String.format("未找到com.miguan.reportview.dto.ChannelDataDto.class 的相关属性ApiModelProperty#position = %d 的定义", type));
        }
        String name = p.getName();
        name = name.concat(isBlank(d.getAppVersion()) ? "" : NAME_SEQ.concat(d.getAppVersion()));
        name = name.concat(isBlank(d.getIsNewApp()) ? "" : NAME_SEQ.concat("1".equals(d.getIsNewApp()) ? "新用户" : "老用户"));
        name = name.concat(isBlank(d.getFatherChannel()) ? "" : NAME_SEQ.concat(d.getFatherChannel()));
        name = name.concat(isBlank(d.getChannel()) ? "" : NAME_SEQ.concat(d.getChannel()));

        Object vaule = FieldUtils.readField(p.getField(), d, true);
        DisassemblyChartDto dto = new DisassemblyChartDto();
        dto.setDate(d.getDate());
        dto.setType(name);
        dto.setValue(vaule == null ? Double.NaN : Double.parseDouble(vaule.toString()));
        dto.setFormart(p.getNotes());
        return dto;
    }

    @ApiOperation(value = "来电-渠道数据导出")
    @GetMapping("api/channel/sta/ldexport")
    public void ldExport(HttpServletResponse response,
                         @ApiParam(value = "应用版本号 多个用,隔开") String appVersion,
                         @ApiParam(value = "是否新用户 多个用,隔开;true=新用户，false=老用户") String isNewUser,
                         @ApiParam(value = "父渠道ID 多个用,隔开;") String pChannelId,
                         @ApiParam(value = "渠道ID 多个用,隔开;") String channelId,
                         @ApiParam(value = "分组类型 多个用,隔开; 2=版本3=新老用户4=渠道5=父渠道") String groupType,
                         @ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd", required = true) String startDate,
                         @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd", required = true) String endDate,
                         @ApiParam(value = "指标类型 多个用,隔开; 1=新增用户2=注册用户3=首页浏览率4=用户转化率5=设置转化率6=来电设置成功率7=壁纸设置成功率8=锁屏设置成功率9=QQ/微信皮肤设置成功率10=铃声设置成功率11=广告点击转化率12=人均广告点击次数13=新用户留存", required = true)
                                 String showType) throws IOException {

        if (isAnyBlank(startDate, endDate)) {
            throw new NullParameterException();
        }
        Boolean isNew = isBlank(isNewUser) || isNewUser.indexOf(",") >= 0 ? null : Boolean.valueOf(isNewUser);
        List<LdChannelDataDto> list = channelDataService.getLdData(seqArray2List(appVersion),isNew, seqArray2List(pChannelId),seqArray2List(channelId),
                seqArray2List(groupType),startDate, endDate);
        if (isEmpty(list)) {
            throw new ResultCheckException();
        }
        list.forEach(d -> {
            d.setIsNewApp(isBlank(d.getIsNewApp()) ? "" : "1".equals(d.getIsNewApp()) ? "新用户" : "老用户");
        });
        ExportParams params = new ExportParams("渠道数据", "渠道数据", ExcelType.XSSF);
        //导出排除字段/属性
        List<String> groups = seqArray2List(groupType);
        ArrayList<String> allGroup = Lists.newArrayList("1", "2", "3", "4","5");
        if (!isEmpty(groups)) {
            allGroup.removeAll(groups);
        }
        String[] exclusions1 = poiExclusions(allGroup, ldGroupNames);
        String[] exclusions2 = poiExclusions(showType, ldAmp);
        params.setExclusions(mergeExclusions(exclusions1, exclusions2));
        ExcelUtils.defaultExport(list, LdChannelDataDto.class, "渠道数据", response, params);
    }

    private final List<String> ldGroupNames = Lists.newArrayList("应用", "版本号", "是否新用户", "渠道", "父渠道");


    @ApiOperation(value = "视频-渠道用户明细数据")
    @PostMapping("api/channel/sta/listChannelDetail")
    public ResponseEntity<PageInfo<ChannelDetailDto>> listChannelDetail(
            @ApiParam(value = "应用马甲包 多个用,隔开") String appPackage,
            @ApiParam(value = "应用版本号 多个用,隔开") String appVersion,
            @ApiParam(value = "渠道ID 多个用,隔开") String channelId,
            @ApiParam(value = "排序字段,格式:字段名+空格+升降序标识,例如：dd asc(日期升序), dd desc(日期降序)。dd=日期,distinct_id=设备id,imei=imei,model=手机型号,package_name=应用,app_version=版本,channel=渠道,play_count=播放次数,vplay_count=有效播放次数,play_time_real=播放时长,vad_show_count=广告展示数,vad_click_count=广告点击数")
                    String orderByField,
            @ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd", required = true) String startDate,
            @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd", required = true) String endDate,
            @ApiParam(value = "页码", required = true) Integer pageNum,
            @ApiParam(value = "每页记录数", required = true) Integer pageSize) {
        PageInfo<ChannelDetailDto> pageList = channelDataService.listChannelDetail(seqArray2List(appPackage), seqArray2List(appVersion), seqArray2List(channelId),
                orderByField, startDate, endDate, pageNum, pageSize, null, null);
        return success(pageList);
    }

    @ApiOperation(value = "视频-渠道用户明细数据-导出")
    @GetMapping("api/channel/channelDetailExport")
    public void channelDetailExport(
            HttpServletResponse response,
            @ApiParam(value = "应用马甲包 多个用,隔开") String appPackage,
            @ApiParam(value = "应用版本号 多个用,隔开") String appVersion,
            @ApiParam(value = "渠道ID 多个用,隔开") String channelId,
            @ApiParam(value = "排序字段,格式:字段名+空格+升降序标识,例如：dd asc(日期升序), dd desc(日期降序)。dd=日期,distinct_id=设备id,imei=imei,model=手机型号,package_name=应用,app_version=版本,channel=渠道,play_count=播放次数,vplay_count=有效播放次数,play_time_real=播放时长,vad_show_count=广告展示数,vad_click_count=广告点击数")
                    String orderByField,
            @ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd", required = true) String startDate,
            @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd", required = true) String endDate,
            @ApiParam(value = "页码", required = true) Integer startRow,
            @ApiParam(value = "每页记录数", required = true) Integer endRow) throws IOException {
        PageInfo<ChannelDetailDto> pageList = channelDataService.listChannelDetail(seqArray2List(appPackage), seqArray2List(appVersion), seqArray2List(channelId),
                orderByField, startDate, endDate, null, null, startRow, endRow);
        ExportParams params = new ExportParams("渠道用户明细数据", "渠道用户明细数据", ExcelType.XSSF);
        ExcelUtils.defaultExport(pageList.getList(), ChannelDetailDto.class, "渠道用户明细数据", response, params);
    }



    @ApiOperation(value = "渠道roi评估")
    @PostMapping("api/channel/roi/estimate")
    public Map<String,Object>  RoiEstimate(
            @ApiParam(value = "产品线", required = true) String appType,
            @ApiParam(value = "开始日期; 日期格式: yyyyMMdd", required = true) String startDate,
            @ApiParam(value = "结束日期; 日期格式: yyyyMMdd", required = true) String endDate,
            @ApiParam(value = "父渠道id列表") String channelIds,
            @ApiParam(value = "分页参数，起始行号，从0开始") Integer offset,
            @ApiParam(value = "分页参数，返回几条数据") Integer limit
            ) {
        if (isAnyBlank( appType,startDate, endDate)) {
            throw new NullParameterException();
        }
        if (isAnyBlank( channelIds)) {
            channelIds=null;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appType",appType);
        params.put("startDate",startDate);
        params.put("endDate",endDate);
        //params.put("channelIds",channelIds);
        params.put("channelIds", isBlank(channelIds) ? null : channelIds.split(","));
        params.put("offset",offset);
        params.put("limit",limit);
        String updateDt=channelDataService.getRoiEstimateDate();
        params.put("updateDt", updateDt);
        List<ChannelRoiEstimateDto> list = channelDataService.getRoiEstimate(params);
        int count=channelDataService.getRoiEstimateCount(params);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("result",list);
        map.put("updateDt",updateDt);
        map.put("count",count);
        return map;
    }

    @ApiOperation(value = "渠道roi评估-导出")
    @PostMapping("api/channel/roi/estimateExport")
    public void RoiEstimateExport(HttpServletResponse response,
            @ApiParam(value = "产品线", required = true) String appType,
            @ApiParam(value = "开始日期; 日期格式: yyyyMMdd", required = true) String startDate,
            @ApiParam(value = "结束日期; 日期格式: yyyyMMdd", required = true) String endDate,
            @ApiParam(value = "父渠道id列表") String channelIds,
            @ApiParam(value = "分页参数，起始行号，从0开始") Integer offset,
            @ApiParam(value = "分页参数，返回几条数据") Integer limit
    )throws IOException {
        if (isAnyBlank( appType,startDate, endDate)) {
            throw new NullParameterException();
        }
        if (isAnyBlank( channelIds)) {
            channelIds=null;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appType",appType);
        params.put("startDate",startDate);
        params.put("endDate",endDate);
        //params.put("channelIds",channelIds);
        params.put("channelIds", isBlank(channelIds) ? null : channelIds.split(","));
        params.put("offset",offset);
        params.put("limit",limit);
        String updateDt=channelDataService.getRoiEstimateDate();
        params.put("updateDt", updateDt);
        List<ChannelRoiEstimateDto> list = channelDataService.getRoiEstimate(params);
        //excel导出
        ExportParams excelParams = new ExportParams("渠道roi评估-导出", "渠道roi评估-导出", ExcelType.XSSF);
        ExcelUtils.defaultExport(list, ChannelRoiEstimateDto.class, "渠道roi评估-导出", response, excelParams);
    }


    @ApiOperation(value = "渠道roi预测")
    @PostMapping("api/channel/roi/prognosis")
    public Map<String,Object> roiPrognosis(
            @ApiParam(value = "产品线", required = true) String appType,
            @ApiParam(value = "最近几天") String dayNum,
            @ApiParam(value = "父渠道id列表") String channelIds,
            @ApiParam(value = "分页参数，起始行号，从0开始") Integer offset,
            @ApiParam(value = "分页参数，返回几条数据") Integer limit
    ) {
        if (isAnyBlank( appType)) {
            throw new NullParameterException();
        }
        if (isAnyBlank( channelIds)) {
            channelIds=null;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appType",appType);
        params.put("dayNum",dayNum);
        //params.put("channelIds",channelIds);
        params.put("channelIds", isBlank(channelIds) ? null : channelIds.split(","));
        params.put("offset",offset);
        params.put("limit",limit);
        String updateDt=channelDataService.getRoiPrognosisDate();
        params.put("updateDt", updateDt);
        List<ChannelRoiPrognosisDto> list = channelDataService.getRoiPrognosis(params);
        int count = channelDataService.getRoiPrognosisCount(params);
        Map<String,Object> map = new HashMap<String,Object>();
        map.put("result",list);
        map.put("updateDt",updateDt);
        map.put("count",count);
        return map;
    }


 @ApiOperation(value = "渠道roi预测-导出")
    @PostMapping("api/channel/roi/prognosisExport")
    public void roiPrognosisExport(HttpServletResponse response,
            @ApiParam(value = "产品线", required = true) String appType,
            @ApiParam(value = "最近几天") String dayNum,
            @ApiParam(value = "父渠道id列表") String channelIds,
            @ApiParam(value = "分页参数，起始行号，从0开始") Integer offset,
            @ApiParam(value = "分页参数，返回几条数据") Integer limit
    ) throws IOException {
        if (isAnyBlank( appType)) {
            throw new NullParameterException();
        }
        if (isAnyBlank( channelIds)) {
            channelIds=null;
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("appType",appType);
        params.put("dayNum",dayNum);
        //params.put("channelIds",channelIds);
        params.put("channelIds", isBlank(channelIds) ? null : channelIds.split(","));
        params.put("offset",offset);
        params.put("limit",limit);
        String updateDt=channelDataService.getRoiPrognosisDate();
        params.put("updateDt", updateDt);
        List<ChannelRoiPrognosisDto> list = channelDataService.getRoiPrognosis(params);
        //excel导出
        ExportParams excelParams = new ExportParams("渠道roi预测-导出", "渠道roi预测-导出", ExcelType.XSSF);
        ExcelUtils.defaultExport(list, ChannelRoiPrognosisDto.class, "渠道roi预测-导出", response, excelParams);
    }
    

    @ApiOperation(value = "渠道每小时的人均点击数折线图")
    @PostMapping("/api/channel/preadclick/line")
    public List<DisassemblyChartDto> staChannelPreAdClick(@ApiParam(value = "类型:1-按子渠道统计，2-按父渠道统计", required = true) int type,
                                                          @ApiParam(value = "日期，例如：2020-10-26", required = true) String date,
                                                          @ApiParam(value = "渠道（子渠道或父渠道），多个时逗号分隔", required = true) String channels) {
        List<AdClickNumVo> list = channelDataService.staChannelPreAdClick(type, date, seqArray2List(channels));
        List<DisassemblyChartDto> resultList = new ArrayList<>();
        for(AdClickNumVo vo : list) {
            DisassemblyChartDto dto = new DisassemblyChartDto();
            dto.setDate(vo.getHh());
            dto.setType(vo.getChannel());
            dto.setValue(vo.getPreAdClick());
            resultList.add(dto);
        }
        return resultList;
    }

   






}
