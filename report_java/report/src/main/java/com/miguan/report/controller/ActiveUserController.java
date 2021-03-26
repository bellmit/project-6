package com.miguan.report.controller;

import com.alibaba.fastjson.JSONArray;
import com.google.common.collect.Maps;
import com.miguan.report.common.util.DateUtil;
import com.miguan.report.common.util.ExportXlsxUtil;
import com.miguan.report.dto.ActiveDetailDto;
import com.miguan.report.dto.LineChartDto;
import com.miguan.report.dto.PairLineDto;
import com.miguan.report.service.ActiveUserService;
import com.miguan.report.service.report.ShenceDataService;
import com.miguan.report.service.report.UmengService;
import com.miguan.report.service.sync.SyncDataService;
import com.miguan.report.service.third.KuaiShouService;
import com.miguan.report.task.SyncUmengTask;
import com.miguan.report.vo.mongo.BannerExtVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Api(description = "/api/activeuser", tags = "活跃用户")
@Slf4j
@RestController
@RequestMapping("/api/activeuser")
public class ActiveUserController {

    @Resource
    private ActiveUserService activeUserService;
    @Resource
    private ResourceLoader resourceLoader;

    @ApiOperation(value = "活跃用户比对报表数据（折线图图）")
    @PostMapping("/countActiveUserLineChar")
    public PairLineDto countActiveUserLineChart(@ApiParam(value = "统计开始时间,格式：yyyy-MM-dd", required = true) String startDate,
                                                   @ApiParam(value = "统计结束时间,格式：yyyy-MM-dd", required = true) String endDate,
                                                   @ApiParam(value = "app类型：1=西柚视频,2=炫来电(默认值为1)") @RequestParam(defaultValue = "1") Integer appType,
                                                   @ApiParam(value = "左统计项，1=活跃用户，2=千展收益，3=人均展现，4=日活价值，5=使用时长，6=收益，7=新用户占比") Integer leftStatItem,
                                                   @ApiParam(value = "是否对比，0=否，1=是") Integer isContrast,
                                                   @ApiParam(value = "appId(如果勾选了对比，则需要传appId，没传的话就按汇总的对比)") String appId,
                                                   @ApiParam(value = "右统计项，1=活跃用户，2=千展用户，3=人均展现，4=日活价值，5=使用时长，6=收益，7=新用户占比") Integer rightStatItem) {
        return activeUserService.countActiveUserLineChart(startDate, endDate, appType, leftStatItem, isContrast, appId, rightStatItem);
    }

    @ApiOperation(value = "活跃用户比对报表数据明细")
    @PostMapping("/countActiveUserChart")
    public List<ActiveDetailDto> countActiveUserChart(@ApiParam(value = "统计开始时间,格式：yyyy-MM-dd", required = true) String startDate,
                                                      @ApiParam(value = "统计结束时间,格式：yyyy-MM-dd", required = true) String endDate,
                                                      @ApiParam(value = "app类型：1=西柚视频,2=炫来电(默认值为1)") @RequestParam(defaultValue = "1") Integer appType,
                                                      @ApiParam(value = "左统计项，1=活跃用户，2=千展收益，3=人均展现，4=日活价值，5=使用时长，6=收益，7=新用户占比") Integer leftStatItem,
                                                      @ApiParam(value = "是否对比，0=否，1=是") Integer isContrast,
                                                      @ApiParam(value = "appId(如果勾选了对比，则需要传appId，没传的话就按汇总的对比)") String appId,
                                                      @ApiParam(value = "右统计项，1=活跃用户，2=千展用户，3=人均展现，4=日活价值，5=使用时长，6=收益，7=新用户占比") Integer rightStatItem) {
        List<LineChartDto> baseDatas = activeUserService.countActiveUserChart(startDate, endDate, appType, leftStatItem, isContrast, appId, rightStatItem, 1);
        LinkedHashMap<String, List<Double>> baseMaps = new LinkedHashMap<>();
        for(LineChartDto lcd : baseDatas) {
            String key = lcd.getDate() + "," +lcd.getType();
            List<Double> list = baseMaps.get(key) == null ? new ArrayList<>() : baseMaps.get(key);
            list.add(lcd.getValue());
            baseMaps.put(key, list);
        }

        List<ActiveDetailDto> result = new ArrayList<>();
        for(String key : baseMaps.keySet()) {
            ActiveDetailDto activeDetail = new ActiveDetailDto();
            String[] dateAndType = key.split(",");
            activeDetail.setDate(dateAndType[0]);
            activeDetail.setType(dateAndType[1]);
            List<Double> values = baseMaps.get(key);
            activeDetail.setLeftValue(values.get(0));
            if(values.size() > 1) {
                activeDetail.setRightValue(values.get(1));
            }
            result.add(activeDetail);
        }
        return result;
    }


    @ApiOperation(value = "活跃用户比对报表数据导出")
    @GetMapping("/export")
    public void export(HttpServletResponse response,
                       @ApiParam(value = "统计开始时间,格式：yyyy-MM-dd", required = true) String startDate,
                       @ApiParam(value = "统计结束时间,格式：yyyy-MM-dd", required = true) String endDate,
                       @ApiParam(value = "app类型：1=西柚视频,2=炫来电(默认值为1)") @RequestParam(defaultValue = "1") Integer appType,
                       @ApiParam(value = "左统计项，1=活跃用户，2=千展收益，3=人均展现，4=日活价值，5=使用时长，6=收益，7=新用户占比") Integer leftStatItem,
                       @ApiParam(value = "是否对比，0=否，1=是") Integer isContrast,
                       @ApiParam(value = "appId(如果勾选了对比，则需要传appId，没传的话就按汇总的对比)") String appId,
                       @ApiParam(value = "右统计项，1=活跃用户，2=千展用户，3=人均展现，4=日活价值，5=使用时长，6=收益，7=新用户占比") Integer rightStatItem) {
        List<LineChartDto> list = activeUserService.countActiveUserChart(startDate, endDate, appType, leftStatItem, isContrast, appId, rightStatItem);
        Map<String, Object> varMap = Maps.newHashMapWithExpectedSize(2);
        varMap.put("name", "明细值");
        varMap.put("type_name", "应用");
        String filename = "活跃用户统计表-".concat(DateUtil.yyyyMMdd());
        ExportXlsxUtil.export(response, resourceLoader,
                "export_xlsx/type_details.xlsx", filename, list, varMap);
    }

    @Resource
    private KuaiShouService kuaiShouService;

    @PostMapping("/test")
    public void test() {
        JSONArray result = kuaiShouService.getDailyShare("2020-07-10");
        System.out.println(result);
    }
}
