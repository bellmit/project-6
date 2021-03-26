package com.miguan.report.controller;

import com.miguan.report.dto.SelectListDto;
import com.miguan.report.service.report.SelectService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(description = "/api/select", tags = "下拉选项接口")
@Slf4j
@RestController
@RequestMapping("/api/select")
public class SelectController {

    @Resource
    private SelectService selectService;

    @ApiOperation(value = "下拉列表-详情页")
    @PostMapping("/detail")
    public SelectListDto detail(@ApiParam(value = "类型：1=西柚视频,2=炫来电", required = true) Integer app_type) {
        return selectService.detailList(app_type);
    }

    @ApiOperation(value = "下拉选项-获取代码为ID列表")
    @PostMapping("/getAdSpaceIdList")
    public List<String> getAdSpaceIdList(@ApiParam("关键字") String keyword,
                                         @ApiParam("应用, 多选逗号间隔") String app_client_id,
                                         @ApiParam("平台, 多选逗号间隔") String plat_form,
                                         @ApiParam("广告位置名称, 多选逗号间隔") String total_name,
                                         @ApiParam(value = "类型：1=西柚视频,2=炫来电", required = true) @RequestParam(defaultValue = "1") Integer app_type) {
        return selectService.getAdSpaceIdList(keyword, app_client_id, plat_form, total_name, app_type);
    }
}
