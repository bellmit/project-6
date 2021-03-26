package com.miguan.reportview.controller;

import cn.afterturn.easypoi.excel.entity.ExportParams;
import cn.afterturn.easypoi.excel.entity.enmus.ExcelType;
import com.google.common.collect.Lists;
import com.miguan.reportview.common.exception.NullParameterException;
import com.miguan.reportview.common.exception.ResultCheckException;
import com.miguan.reportview.common.utils.DateUtil;
import com.miguan.reportview.common.utils.ExcelUtils;
import com.miguan.reportview.controller.base.BaseController;
import com.miguan.reportview.dto.DisassemblyChartDto;
import com.miguan.reportview.dto.ResponseEntity;
import com.miguan.reportview.dto.UserKeepStrDto;
import com.miguan.reportview.dto.UserKeepTDto;
import com.miguan.reportview.entity.App;
import com.miguan.reportview.entity.RpUserKeep;
import com.miguan.reportview.service.IAppService;
import com.miguan.reportview.service.IUserKeepService;
import com.miguan.reportview.vo.UserKeepVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tool.util.StringUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.miguan.reportview.common.utils.NumCalculationUtil.divide;
import static org.apache.commons.lang3.StringUtils.isBlank;
import static org.springframework.util.CollectionUtils.isEmpty;

/**
 * @author zhongli
 * @date 2020-07-30 
 *
 */
@Api(value = "用户留存", tags = {"用户留存"})
@RestController
public class UserKeepController extends BaseController {

    @Autowired
    private IAppService appService;
    @Autowired
    private IUserKeepService userKeepService;

    @ApiOperation(value = "拆线图")
    @PostMapping("api/userkeep/sta/getdata")
    public ResponseEntity<List<DisassemblyChartDto>> getData(
            @ApiParam(value = "显示类型 ; 1=新增用户留存2=活跃用户留存", required = true) String showType,
            @ApiParam(value = "数据类型 ; 0=当天1=1天后,2=2天后,3=3天后,4=4天后,5=5天后,6=6天后,7=7天后, 14=14天后,30=30天后", required = true) String valueType,
            @ApiParam(value = "父渠道ID 多个用,隔开;") String pChannelId,
            @ApiParam(value = "渠道ID 多个用,隔开;") String channelId,
            @ApiParam(value = "应用马甲包 多个用,隔开") String appPackage,
            @ApiParam(value = "应用版本号 多个用,隔开") String appVersion,
            @ApiParam(value = "app类型：1=西柚视频,2=炫来电(默认值为1)") @RequestParam(defaultValue = "1") Integer appType,
            @ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd", required = true) String startDate,
            @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd", required = true) String endDate) {
        if (StringUtils.isAnyBlank(showType, valueType, startDate, endDate)) {
            throw new NullParameterException();
        }
        List<UserKeepVo> list = userKeepService.getData(seqArray2List(valueType), "1".equals(showType) ? true : null,
                seqArray2List(appPackage), seqArray2List(appVersion),
                seqArray2List(pChannelId), seqArray2List(channelId),
                appType, startDate, endDate);
        if (isEmpty(list)) {
            throw new ResultCheckException();
        }
        String name1 = "1".equals(showType) ? "新增用户留存" : "活跃用户留存";
        List<DisassemblyChartDto> rdata = list.stream().map(e -> {
            DisassemblyChartDto dto = new DisassemblyChartDto();
            App app = appService.getAppByPackageName(e.getPackageName());
            String name = name1;
            name = name.concat(app == null ? "" : NAME_SEQ.concat(app.getName()));
            name = name.concat(isBlank(e.getAppVersion()) ? "" : NAME_SEQ.concat(e.getAppVersion()));
            name = name.concat(isBlank(e.getFatherChannel()) ? "" : NAME_SEQ.concat(e.getFatherChannel()));
            name = name.concat(isBlank(e.getChangeChannel()) ? "" : NAME_SEQ.concat(e.getChangeChannel()));
            dto.setType(name);
            Integer v = e.getShowValue() == null ? null : e.getShowValue().intValue();
            Double user = e.getUser();
            dto.setValue(this.getVaule(true, v, user == null ? 0 : user.doubleValue()));
            dto.setDate(DateUtil.yyyy_MM_dd4yyyyMMdd(e.getDd()));
            return dto;
        }).collect(Collectors.toList());
        rdata = this.top10(rdata);
        return success(rdata);
    }

    @ApiOperation(value = "表格")
    @PostMapping("api/userkeep/sta/gettabledata")
    public ResponseEntity<List<UserKeepTDto>> getTableData(
            @ApiParam(value = "显示类型 ; 1=新增用户留存2=活跃用户留存", required = true) String showType,
            @ApiParam(value = "显示方式 ; 1=天2=周", required = true) String viewType,
            @ApiParam(value = "数据类型 ; 1=留存率2=留存数", required = true) String valueType,
            @ApiParam(value = "父渠道ID 多个用,隔开;") String pChannelId,
            @ApiParam(value = "渠道ID 多个用,隔开;") String channelId,
            @ApiParam(value = "应用马甲包 多个用,隔开") String appPackage,
            @ApiParam(value = "应用版本号 多个用,隔开") String appVersion,
            @ApiParam(value = "app类型：1=西柚视频,2=炫来电(默认值为1)") @RequestParam(defaultValue = "1") Integer appType,
            @ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd", required = true) String startDate,
            @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd", required = true) String endDate) {

        if (StringUtils.isAnyBlank(showType, viewType, valueType, startDate, endDate)) {
            throw new NullParameterException();
        }
        List<RpUserKeep> list = userKeepService.getTableData("1".equals(showType) ? true : null,
                seqArray2List(appPackage), seqArray2List(appVersion),
                seqArray2List(pChannelId), seqArray2List(channelId),
                appType, startDate, endDate);
        if (isEmpty(list)) {
            throw new ResultCheckException();
        }
        boolean isRate = "1".equals(valueType);
        return success(procData(list, isRate));
    }

    private List<UserKeepTDto> procData(List<RpUserKeep> list, boolean isRate) {
        return list.stream().map(e -> {
            UserKeepTDto dto = new UserKeepTDto();
            double user = e.getUser().doubleValue();
            dto.setDate(DateUtil.yyyy_MM_dd4yyyyMMdd(e.getSd().toString()));
            dto.setUser(user);
            dto.setPackageName(e.getPackageName());
            dto.setAppVersion(e.getAppVersion());
            dto.setChangeChannel(e.getChangeChannel());
            dto.setKeep1(this.getVaule(isRate, e.getKeep1(), user));
            dto.setKeep2(this.getVaule(isRate, e.getKeep2(), user));
            dto.setKeep3(this.getVaule(isRate, e.getKeep3(), user));
            dto.setKeep4(this.getVaule(isRate, e.getKeep4(), user));
            dto.setKeep5(this.getVaule(isRate, e.getKeep5(), user));
            dto.setKeep6(this.getVaule(isRate, e.getKeep6(), user));
            dto.setKeep7(this.getVaule(isRate, e.getKeep7(), user));
            dto.setKeep14(this.getVaule(isRate, e.getKeep14(), user));
            dto.setKeep30(this.getVaule(isRate, e.getKeep30(), user));
            dto.setFatherChannel(e.getFatherChannel());
            return dto;
        }).collect(Collectors.toList());
    }

    private double getVaule(boolean isRate, Integer v, double user) {
        if (v == null) {
            return 0;
        }
        return isRate ? divide(v.doubleValue(), user, true) : v.doubleValue();
    }

    @ApiOperation(value = "导出")
    @GetMapping("api/userkeep/sta/export")
    public void export(HttpServletResponse response,
                       @ApiParam(value = "显示类型 ; 1=新增用户留存2=活跃用户留存") String showType,
                       @ApiParam(value = "显示方式 ; 1=天2=周") String viewType,
                       @ApiParam(value = "数据类型 ; 1=留存率2=留存数") String valueType,
                       @ApiParam(value = "父渠道ID 多个用,隔开;") String pChannelId,
                       @ApiParam(value = "渠道ID 多个用,隔开;") String channelId,
                       @ApiParam(value = "应用马甲包 多个用,隔开") String appPackage,
                       @ApiParam(value = "应用版本号 多个用,隔开") String appVersion,
                       @ApiParam(value = "app类型：1=西柚视频,2=炫来电(默认值为1)") @RequestParam(defaultValue = "1") Integer appType,
                       @ApiParam(value = "开始日期; 日期格式: yyyy-MM-dd") String startDate,
                       @ApiParam(value = "结束日期; 日期格式: yyyy-MM-dd") String endDate) throws IOException {
        if (StringUtils.isAnyBlank(showType, viewType, valueType, startDate, endDate)) {
            throw new NullParameterException();
        }
        List<RpUserKeep> list = userKeepService.getTableData("1".equals(showType) ? true : null,
                seqArray2List(appPackage), seqArray2List(appVersion),
                seqArray2List(pChannelId), seqArray2List(channelId),
                appType, startDate, endDate);
        if (isEmpty(list)) {
            throw new ResultCheckException();
        }
        boolean isRate = "1".equals(valueType);
        List<UserKeepTDto> rdata = procData(list, isRate);
        list.clear();
        rdata.forEach(d -> {
            App app = appService.getAppByPackageName(d.getPackageName());
            d.setPackageName(app == null ? "" : app.getName());
        });

        ExportParams params = new ExportParams("留存数据".concat(isRate ? "-留存率" : "-留存数"), "留存数据", ExcelType.XSSF);
        List<String> getGroupCode = getGroupCode(appPackage, appVersion, pChannelId, channelId);
        params.setExclusions(poiExclusions(getGroupCode, groupNames));
//        params.setCreateHeadRows(false);
        ExcelUtils.defaultExport(exportConcat(rdata, isRate), UserKeepStrDto.class, "留存数据", response, params);
    }

    private List<UserKeepStrDto> exportConcat(List<UserKeepTDto> rdata, boolean isRate) {
        List<UserKeepStrDto> list = new ArrayList<>();
        for(UserKeepTDto tdto : rdata) {
            UserKeepStrDto strDto = new UserKeepStrDto();
            BeanUtils.copyProperties(tdto, strDto);
            strDto.setUser(tdto.getUser() + "");
            strDto.setKeep1(tdto.getKeep1() + (isRate ? "%" :""));
            strDto.setKeep2(tdto.getKeep2() + (isRate ? "%" :""));
            strDto.setKeep3(tdto.getKeep3() + (isRate ? "%" :""));
            strDto.setKeep4(tdto.getKeep4() + (isRate ? "%" :""));
            strDto.setKeep5(tdto.getKeep5() + (isRate ? "%" :""));
            strDto.setKeep6(tdto.getKeep6() + (isRate ? "%" :""));
            strDto.setKeep7(tdto.getKeep7() + (isRate ? "%" :""));
            strDto.setKeep14(tdto.getKeep14() + (isRate ? "%" :""));
            strDto.setKeep30(tdto.getKeep30() + (isRate ? "%" :""));
            list.add(strDto);
        }
        return list;
    }

    private List<String> getGroupCode(String appPackage,String appVersion,String pChannelId,String channelId) {
        List<String> group = new ArrayList<>();
        if(StringUtil.isBlank(appPackage)) {
            group.add("1");
        }
        if(StringUtil.isBlank(appVersion)) {
            group.add("2");
        }
        if(StringUtil.isBlank(pChannelId)) {
            group.add("3");
        }
        if(StringUtil.isBlank(channelId)) {
            group.add("4");
        }
        return group;
    }

    private final List<String> groupNames = Lists.newArrayList("应用", "版本", "父渠道", "渠道");
}
