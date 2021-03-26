package com.miguan.report.controller;

import com.miguan.report.common.constant.CommonConstant;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhongli
 * @date 2020-06-20 
 *
 */
@Api(value = "/api/video/activevalue", tags = "视频广告数据-> 日活均价")
@RestController
@RequestMapping("/api/video/activevalue")
public class VideoActiveValueController extends ActiveValueController{

    @Override
    int appType() {
        return CommonConstant.VIDEO_APP_TYPE;
    }
}
