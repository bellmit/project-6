package com.miguan.report.controller;

import com.miguan.report.common.constant.CommonConstant;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**来电概览
 * @author zhongli
 * @date 2020-06-17 
 *
 */
@Api(value = "/api/call/overview", tags = "来电广告数据-> 概览")
@RestController
@RequestMapping("/api/call/overview")
public class CallOverviewComtroller extends OverviewComtroller {

    @Override
    int appType() {
        return CommonConstant.CALL_APP_TYPE;
    }
}
