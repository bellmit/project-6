package com.miguan.bigdata.controller;

import com.miguan.bigdata.service.AdDataService;
import com.miguan.bigdata.service.AppListService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "用户设备应用安装列表接口", tags = {"用户设备应用安装列表接口"})
@RestController
public class AppListController {
    @Resource
    private AppListService appListService;


    @ApiOperation("mongodb的drive库的apps_info_list表中历史数据的distinctId字段数据刷进去(此接口上线后只需要执行一次)")
    @PostMapping("/api/applist/brushOldAppListDistinctId")
    public void brushOldAppListDistinctId() {
        appListService.brushOldAppListDistinctId();
    }
}
