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
@Api(value = "/api/call/activevalue", tags = "来电广告数据-> 日活均价")
@RestController
@RequestMapping("/api/call/activevalue")
public class CallActiveValueController extends ActiveValueController{

    @Override
    int appType() {
        return CommonConstant.CALL_APP_TYPE;
    }
}
