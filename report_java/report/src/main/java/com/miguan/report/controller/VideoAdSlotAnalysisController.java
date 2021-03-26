package com.miguan.report.controller;

import com.miguan.report.common.constant.CommonConstant;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhongli
 * @date 2020-06-19 
 *
 */
@Api(description = "/api/video/adsa", tags = "视频广告数据-> 广告位分析")
@RestController
@ApiModel("广告位分析")
@RequestMapping("/api/video/adsa")
public class VideoAdSlotAnalysisController extends AdSlotAnalysisController {

    @Override
    int appType() {
        return CommonConstant.VIDEO_APP_TYPE;
    }
}
