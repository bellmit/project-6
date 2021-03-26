package com.miguan.report.controller;

import com.miguan.report.dto.BannerQuotaDto;
import com.miguan.report.dto.DataDetailDto;
import com.miguan.report.dto.LineChartDto;
import com.miguan.report.mapper.DataDetailMapper;
import com.miguan.report.service.report.DataDetailService;
import com.miguan.report.service.report.SelectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

@Api(description = "/api/activeuser", tags = "数据明细报表")
@Slf4j
@RestController
@RequestMapping("/api/data/detail")
public class DataDetailController {

    @Resource
    private DataDetailService dataDetailService;
    @Resource
    private DataDetailMapper dataDetailMapper;
    @Resource
    private SelectService selectService;

    @ApiOperation(value = "数据明细列表")
    @PostMapping("/findDataDetailListByPage")
    public DataDetailDto findDataDetailListByPage(@ApiParam("页码") @RequestParam(defaultValue = "1") int pageNum,
                                                  @ApiParam(value = "每页记录数") @RequestParam(defaultValue = "10") int pageSize,
                                                  @ApiParam(value = "统计开始时间,格式：yyyy-MM-dd", required = true) String startDate,
                                                  @ApiParam(value = "统计结束时间,格式：yyyy-MM-dd", required = true) String endDate,
                                                  @ApiParam(value = "token", required = true) String token,
                                                  @ApiParam(value = "app类型：1=西柚视频,2=炫来电(默认值为1)") @RequestParam(defaultValue = "1") Integer appType,
                                                  @ApiParam(value = "应用id(下拉列表取appClientList)") String appClientId,
                                                  @ApiParam(value = "平台id") String platForm,
                                                  @ApiParam(value = "广告位置名称") String totalName,
                                                  @ApiParam(value = "代码为id") String adId,
                                                  @ApiParam(value = "排序字段,格式:升序(字段id)，降序(字段id+空格+desc)") String sortField) {
        adId = getActualAdId(adId, appClientId, platForm, totalName, appType);
        return dataDetailService.findDataDetailList(pageNum, pageSize, startDate, endDate, token, appType, appClientId, platForm, totalName, adId, sortField);
    }

    @ApiOperation(value = "数据明细折线图")
    @PostMapping("/countLineDataDetailChart")
    public List<LineChartDto> countLineDataDetailChart(@ApiParam(value = "统计开始时间,格式：yyyy-MM-dd", required = true) String startDate,
                                                       @ApiParam(value = "统计结束时间,格式：yyyy-MM-dd", required = true) String endDate,
                                                       @ApiParam(value = "app类型：1=西柚视频,2=炫来电(默认值为1)") @RequestParam(defaultValue = "1") Integer appType,
                                                       @ApiParam(value = "统计项,1-展现量,2-点击量，3-点击率，4-点击单价，5-cpm，6-营收，7-人均展现数，8-日活均价值，9-展示率，10-报错率，11-广告填充率") String statItem,
                                                       @ApiParam(value = "应用id(下拉列表取appClientList)") String appClientId,
                                                       @ApiParam(value = "平台id") String platForm,
                                                       @ApiParam(value = "广告位置名称") String totalName,
                                                       @ApiParam(value = "代码为id") String adId,
                                                       @ApiParam(value = "时间类型：1=按日，2=按周，3=按月(默认值为1)") @RequestParam(required = false, defaultValue = "1") Integer timeType) {
        adId = getActualAdId(adId, appClientId, platForm, totalName, appType);
        return dataDetailService.countLineDataDetailChart(startDate, endDate, appType, statItem, appClientId, platForm, totalName, adId, timeType);
    }

    @ApiOperation(value = "按小时数据明细折线图")
    @PostMapping("/countLineHourChart")
    public List<LineChartDto> countLineHourChart(@ApiParam(value = "统计开始时间，多个的时候逗号分隔,格式：yyyy-MM-dd", required = true) String date,
                                                 @ApiParam(value = "app类型：1=西柚视频,2=炫来电(默认值为1)") @RequestParam(defaultValue = "1") Integer appType,
                                                 @ApiParam(value = "统计项,1-展现量,2-点击量，3-点击率") String statItem,
                                                 @ApiParam(value = "应用id(下拉列表取appClientList)") String appClientId,
                                                 @ApiParam(value = "平台id") String platForm,
                                                 @ApiParam(value = "广告位置名称") String totalName,
                                                 @ApiParam(value = "代码为id") String adId) {
        adId = getActualAdId(adId, appClientId, platForm, totalName, appType);
        return dataDetailService.countLineHourChart(date, appType, statItem, appClientId, platForm, totalName, adId);
    }

    @ApiOperation(value = "导出数据明细列表")
    @GetMapping("/export")
    public void export(HttpServletResponse response,
            @ApiParam(value = "统计开始时间,格式：yyyy-MM-dd", required = true) String startDate,
            @ApiParam(value = "统计结束时间,格式：yyyy-MM-dd", required = true) String endDate,
            @ApiParam(value = "token", required = true) String token,
            @ApiParam(value = "app类型：1=西柚视频,2=炫来电(默认值为1)") @RequestParam(defaultValue = "1") Integer appType,
            @ApiParam(value = "应用id(下拉列表取appClientList)") String appClientId,
            @ApiParam(value = "平台id") String platForm,
            @ApiParam(value = "广告位置名称") String totalName,
            @ApiParam(value = "代码为id") String adId,
            @ApiParam(value = "排序字段,格式:升序(字段id)，降序(字段id+空格+desc)") String sortField) {
        adId = getActualAdId(adId, appClientId, platForm, totalName, appType);
        dataDetailService.export(response, startDate, endDate, token, appType, appClientId, platForm, totalName, adId, sortField);
    }

    @ApiOperation(value = "返回用户自定义列的列表")
    @PostMapping("/getHeadFieldList")
    public List<BannerQuotaDto> getHeadFieldList(@ApiParam("token") String token,
                                                 @ApiParam("app类型：1=西柚视频,2=炫来电(默认值为1)") @RequestParam(defaultValue = "1") Integer appType,
                                                 @ApiParam("是否隐藏代码位后的字段，0-隐藏，1-不隐藏") Integer idHide) {
        List<BannerQuotaDto> headList = dataDetailMapper.getHeadList(token, appType);
        if(idHide == 0) {
            dataDetailService.filterAdFiled(headList);
        }
        return headList;
    }

    @ApiOperation(value = "保存用户自定义列")
    @PostMapping("/saveHeadField")
    public void saveHeadField(@ApiParam("token") String token,
                              @ApiParam("app类型：1=西柚视频,2=炫来电(默认值为1)") @RequestParam(defaultValue = "1") Integer appType,
                              @ApiParam("字段id，多个的时候逗号分隔") String ids) {
        dataDetailService.saveHeadField(token, appType, ids);
    }

    /**
     * 如果代码位传0，则表示全选需要查询出广告位下的全部代码位。
     * @param adId  代码位id
     * @param totalName 广告位名称
     * @param appType app类型：1=西柚视频,2=炫来电(默认值为1)
     * @return
     */
    private String getActualAdId(String adId, String appClientId, String platForm, String totalName, Integer appType) {
        if(!"0".equals(adId)) {
            return adId;
        }
        List<String> list = selectService.getAdSpaceIdList(null, appClientId, platForm, totalName, appType);
        return String.join(",", list);
    }
}
