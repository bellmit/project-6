package com.miguan.report.controller;

import com.miguan.report.common.constant.CommonConstant;
import io.swagger.annotations.Api;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**来电广告位分析
 * @author zhongli
 * @date 2020-06-19 
 *
 */
@Api(value = "/api/call/adsa", tags = "来电广告数据-> 广告位分析")
@RestController
@RequestMapping("/api/call/adsa")
public class CallAdSlotAnalysisController extends AdSlotAnalysisController{

    @Override
    int appType() {
        return CommonConstant.CALL_APP_TYPE;
    }
}
