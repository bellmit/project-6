package com.miguan.report.controller;

import com.cgcg.base.core.exception.CommonException;
import com.google.common.collect.Maps;
import com.miguan.report.common.enums.AppLaiDianPackageEnum;
import com.miguan.report.common.enums.AppVideoPackageEnum;
import com.miguan.report.common.util.ExportXlsxUtil;
import com.miguan.report.dto.BannerDataExcportDto;
import com.miguan.report.vo.AdErrorVo;
import com.miguan.report.service.report.CodeSpaceService;
import com.miguan.report.vo.CodeSpaceDataVo;
import com.miguan.report.vo.CodeSpaceVo;
import com.miguan.report.vo.PageInfoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;
import tool.util.DateUtil;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.*;


@Api(description = "/api/codeSpace", tags = "代码位相关接口")
@RestController
@RequestMapping("/api/codeSpace")
public class CodeSpaceController {

    @Resource
    private CodeSpaceService codeSpaceService;
    @Autowired
    private ResourceLoader resourceLoader;

    @ApiOperation(value = "代码位分析列表")
    @PostMapping("/analyzeList")
    public PageInfoVo<CodeSpaceVo> reportList(
            @ApiParam(name = "reportType", value = "报表类型：1 视频，2 来电", required = true) String reportType,
            @ApiParam(name = "startDdate", value = "起始日期,默认昨天：yyyy-MM-dd", required = false) String startDdate,
            @ApiParam(name = "endDdate", value = "截止日期,默认昨天：yyyy-MM-dd", required = false) String endDdate,
            @ApiParam(name = "appClient", value = "应用名称-客户端类型, 多选逗号间隔。 应用名称:1 果果视频, 2 茜柚视频, 3 蜜桃视频, 4 炫来电。客户端类型: Android 1, IOS 2", required = false) String appClient,
            @ApiParam(name = "advSpace", value = "广告位, 默认启动页，多选逗号间隔", required = false) String advSpace,
            @ApiParam(name = "serialNum", value = "序号，多选逗号间隔", required = false) String serialNum,
            @ApiParam(value = "页码") @RequestParam(defaultValue = "1") int pageNum,
            @ApiParam(value = "每页记录数") @RequestParam(defaultValue = "10") int pageSize
    ) {
        if (StringUtils.isBlank(reportType)) {
            throw new CommonException("报表类型不能为空");
        }
        if (StringUtils.isBlank(startDdate)) {
            startDdate = DateUtil.dateStr2(DateUtils.addDays(new Date(), -1));
        }
        if (StringUtils.isBlank(endDdate)) {
            endDdate = DateUtil.dateStr2(DateUtils.addDays(new Date(), -1));
        }
        List<Integer> clientType = getClientTypeNew(appClient);
        List<String> packageNames = getPackageNameNew(appClient, reportType);
        List<String> codeSpaceNames = getCodeSpaceNames(advSpace);
        List<Integer> serialNums = getSerialNums(serialNum);

        return codeSpaceService.getAnalyzeListNew4Page(reportType, startDdate, endDdate,
                packageNames, clientType, codeSpaceNames, serialNums, pageNum, pageSize);
    }

    @ApiOperation(value = "代码位报错明细")
    @PostMapping("/errorDetail")
    public List<AdErrorVo> errorDetail(
            @ApiParam(name = "detailType", value = "报表类型：1 视频，2 来电", required = true) String detailType,
            @ApiParam(name = "date", value = "日期：yyyy-MM-dd", required = true) String date,
            @ApiParam(name = "codeSpace", value = "代码位", required = true) String codeSpace
    ) {
        if (StringUtils.isBlank(date)) {
            throw new CommonException("日期不能为空");
        }
        if (StringUtils.isBlank(codeSpace)) {
            throw new CommonException("代码位不能为空");
        }
        if(StringUtils.isBlank(detailType)){
            detailType = "1";
        }

        return codeSpaceService.getCodeSpaceErrorDetail(detailType, date, codeSpace);
    }

    @ApiOperation(value = "代码位报表导出")
    @GetMapping("/export")
    public void export(
            HttpServletResponse response,
            @ApiParam(name = "reportType", value = "报表类型：1 视频，2 来电", required = true) String reportType,
            @ApiParam(name = "startDdate", value = "起始日期,默认昨天：yyyy-MM-dd", required = false) String startDdate,
            @ApiParam(name = "endDdate", value = "截止日期,默认昨天：yyyy-MM-dd", required = false) String endDdate,
            @ApiParam(name = "appClient", value = "应用名称-客户端类型, 多选逗号间隔。 应用名称:1 果果视频, 2 茜柚视频, 3 蜜桃视频, 4 炫来电。客户端类型: Android 1, IOS 2", required = false) String appClient,
            @ApiParam(name = "advSpace", value = "广告位, 默认启动页，多选逗号间隔", required = false) String advSpace,
            @ApiParam(name = "serialNum", value = "序号，多选逗号间隔", required = false) String serialNum
    ) {
        if (StringUtils.isBlank(reportType)) {
            throw new CommonException("报表类型不能为空");
        }
        if (StringUtils.isBlank(startDdate)) {
            startDdate = DateUtil.dateStr2(DateUtils.addDays(new Date(), -1));
        }
        if (StringUtils.isBlank(endDdate)) {
            endDdate = DateUtil.dateStr2(DateUtils.addDays(new Date(), -1));
        }
        List<Integer> clientType = getClientTypeNew(appClient);
        List<String> packageNames = getPackageNameNew(appClient, reportType);
        List<String> codeSpaceNames = getCodeSpaceNames(advSpace);
        List<Integer> serialNums = getSerialNums(serialNum);
        List<CodeSpaceVo> voList = codeSpaceService.getAnalyzeListNew(reportType, startDdate, endDdate, packageNames, clientType, codeSpaceNames, serialNums, 0, 9999);
        List<BannerDataExcportDto> lists = new ArrayList<BannerDataExcportDto>();
        if (!CollectionUtils.isEmpty(voList)) {
            for (CodeSpaceVo vo : voList) {
                String date = vo.getDate();
                String curAppName = vo.getCut_app_name();
                String totalName = vo.getTotal_name();
                if (CollectionUtils.isEmpty(vo.getCode())) {
                    BannerDataExcportDto exportDto = new BannerDataExcportDto();
                    exportDto.setDate(date);
                    exportDto.setCutAppName(curAppName);
                    exportDto.setTotalName(totalName);
                    exportDto.setAdSpaceId("");
                    exportDto.setPrice(0d);
                    exportDto.setChannelType("");
                    exportDto.setOptionValue(0);
                    exportDto.setAdRequest(0);
                    exportDto.setAdReturn(0);
                    exportDto.setAdFilling(0d);
                    exportDto.setShowNumber(0);
                    exportDto.setShowNumberRate(0d);
                    exportDto.setClickNumber(0);
                    exportDto.setClickRate(0d);
                    exportDto.setEarnings(0d);
                    exportDto.setCpm(0d);
                    exportDto.setErrorNumber(0);
                    exportDto.setErrorRate(0d);
                    lists.add(exportDto);
                } else {
                    List<CodeSpaceDataVo> codeList = vo.getCode();
                    for (CodeSpaceDataVo dataVo : codeList) {
                        BannerDataExcportDto exportDto = new BannerDataExcportDto();
                        BeanUtils.copyProperties(dataVo, exportDto);
                        exportDto.setDate(date);
                        exportDto.setCutAppName(curAppName);
                        exportDto.setTotalName(totalName);
                        exportDto.setChannelType(dataVo.getChannelTypeStr());
                        lists.add(exportDto);
                    }
                }
            }
        }


        Map<String, Object> varMap = Maps.newHashMapWithExpectedSize(17);
        varMap.put("cutAppName", "应用");
        varMap.put("totalName", "广告位");
        varMap.put("adSpaceId", "代码位");
        varMap.put("price", "价格");
        varMap.put("channelType", "渠道类型");
        varMap.put("optionValue", "排序值");
        varMap.put("adRequest", "广告请求量");
        varMap.put("adReturn", "广告返回量");
        varMap.put("adFilling", "广告填充率");
        varMap.put("showNumber", "展现量");
        varMap.put("showNumberRate", "展现率");
        varMap.put("clickNumber", "点击数");
        varMap.put("clickRate", "点击率");
        varMap.put("earnings", "收益");
        varMap.put("cpm", "CPM");
        varMap.put("errorNumber", "报错数");
        varMap.put("errorRate", "报错率");
        String filename = "代码位分析报表-".concat(com.miguan.report.common.util.DateUtil.yyyyMMdd());
        ExportXlsxUtil.export(response, resourceLoader, "export_xlsx/code_space_details.xlsx", filename, lists, varMap);
    }

    private List<Integer> getSerialNums(String serialNum) {
        List<Integer> serialNums = null;
        if (StringUtils.isNotBlank(serialNum)) {
            serialNums = new ArrayList<Integer>();
            List<String> serialNumStrs = Arrays.asList(serialNum.split(","));
            for (String num : serialNumStrs) {
                serialNums.add(Integer.valueOf(num));
            }
        }
        return serialNums;
    }

    private List<String> getCodeSpaceNames(String advSpace) {
        List<String> codeSpaceNames = null;
        if (StringUtils.isNotBlank(advSpace)) {
            codeSpaceNames = Arrays.asList(advSpace.split(","));
        }
        return codeSpaceNames;
    }

    private List<String> getPackageNameNew(String appClient, String reportType) {
        List<String> packageNames = new ArrayList<>();
        if (StringUtils.isNotBlank(appClient)) {
            String[] appIdAndClientIds = appClient.split(",");
            for (String str : appIdAndClientIds) {
                String[] appIdAndClientId = str.split("-");
                if (StringUtils.equals("1", reportType)) {
                    AppVideoPackageEnum packageEnum = AppVideoPackageEnum.getByAppEnumAndClientId(Integer.valueOf(appIdAndClientId[0]), Integer.valueOf(appIdAndClientId[1]));
                    packageNames.add(packageEnum == null ? "" : packageEnum.getPackageName());
                } else {
                    AppLaiDianPackageEnum packageEnum = AppLaiDianPackageEnum.getByAppEnumAndClientId(Integer.valueOf(appIdAndClientId[0]), Integer.valueOf(appIdAndClientId[1]));
                    packageNames.add(packageEnum == null ? "" : packageEnum.getPackageName());
                }
            }
        } else {
            if(StringUtils.equals("1", reportType)){
                packageNames = AppVideoPackageEnum.getAllVideoPackageList();
            } else {
                packageNames = AppLaiDianPackageEnum.getAllLaiDianPackageList();
            }
        }
        return packageNames;
    }

    private List<Integer> getClientTypeNew(String appClient) {
        List<Integer> clientType = new ArrayList<Integer>();
        if (StringUtils.isNotBlank(appClient)) {
            String[] appIdAndClientIds = appClient.split(",");
            for (String str : appIdAndClientIds) {
                Integer clientId = Integer.valueOf(str.split("-")[1]);
                if (!clientType.contains(clientId)) {
                    clientType.add(clientId);
                }
            }
        }
        return clientType;
    }
}
