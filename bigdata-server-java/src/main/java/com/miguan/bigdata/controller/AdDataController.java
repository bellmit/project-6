package com.miguan.bigdata.controller;

import com.miguan.bigdata.common.util.RedisClient;
import com.miguan.bigdata.service.AdDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "广告行为数据接口", tags = {"广告行为数据接口"})
@RestController
public class AdDataController {
    @Resource
    private AdDataService adDataService;

    @ApiOperation(value = "查询出广告配置代码位自动排序阀值的代码位")
    @PostMapping("/api/addata/listAdIdShowThreshold")
    public List<String> listAdIdShowThreshold(@ApiParam(value = "日期，yyyy-MM-dd") String dd,
                                              @ApiParam(value = "展现量阀值") Integer showThreshold,
                                              @ApiParam(value = "类型，1:未达到阀值，2：已达到阀值") Integer type,
                                              @ApiParam(value = "代码位，多个逗号分隔（type为2的时候使用）") String adIds) {
        return adDataService.listAdIdShowThreshold(dd, showThreshold, type, adIds);
    }

    @ApiOperation(value = "钉钉-广告展示/广告库存比值预警")
    @PostMapping("/api/addata/findEarlyWarnList")
    public String findEarlyWarnList(@ApiParam("类型：0-每小时预警，1-每天10点预警前24小时") Integer warnType) {
        String result = adDataService.findEarlyWarnList(warnType);
        return result;
    }
}
