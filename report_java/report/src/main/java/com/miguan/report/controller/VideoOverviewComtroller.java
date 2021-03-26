package com.miguan.report.controller;

import com.miguan.report.common.constant.CommonConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhongli
 * @date 2020-06-17 
 *
 */
@Api(description = "/api/video/overview", tags = "视频广告数据-> 概览")
@RestController
@ApiModel("概览页")
@RequestMapping("/api/video/overview")
public class VideoOverviewComtroller extends OverviewComtroller{

    @Override
    int appType() {
        return CommonConstant.VIDEO_APP_TYPE;
    }
}
