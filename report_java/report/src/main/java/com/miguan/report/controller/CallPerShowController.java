package com.miguan.report.controller;

import com.miguan.report.common.constant.CommonConstant;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author zhongli
 * @date 2020-06-19 
 *
 */
@Api(value = "/api/call/pershow", tags = "来电广告数据-> 人均展示")
@RestController
@RequestMapping("/api/call/pershow")
public class CallPerShowController extends PerShowController {

    @Override
    int appType() {
        return CommonConstant.CALL_APP_TYPE;
    }
}
