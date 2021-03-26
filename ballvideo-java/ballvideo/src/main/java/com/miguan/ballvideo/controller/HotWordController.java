package com.miguan.ballvideo.controller;

import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.service.HotWordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value="热词接口",tags={"热词接口"})
@RestController
@RequestMapping("/api/hotWord")
public class HotWordController {

    @Resource
    private HotWordService hotWordService;

    @ApiOperation("获取百度当日前10热词（PHP调用）")
    @GetMapping("/getBaiduHotWord")
    public ResultMap getBaiduHotWord(@ApiParam(value = "操作者") String editor) {
        hotWordService.getBaiduHotWord(editor);
        return ResultMap.success();
    }

    @ApiOperation("获取10条热词（App调用）")
    @GetMapping("/findHotWordInfo")
    public ResultMap findHotWordInfo(@ApiParam(value = "渠道Id") String channelId,
                                     @ApiParam(value = "版本号") String appVersion,
                                     @ApiParam("手机类型：1-ios，2：安卓") String mobileType) {
        List<String> resultList = hotWordService.findHotWordInfo(channelId, appVersion, mobileType);
        Map<String, Object> resultMap = new HashMap<>();
        resultMap.put("contentList", resultList);
        return ResultMap.success(resultMap);
    }

    @ApiOperation("刷新redis热词缓存（PHP调用）")
    @GetMapping("/freshHotWordInfo")
    public ResultMap freshHotWordInfo() {
        hotWordService.freshHotWordInfo();
        return ResultMap.success();
    }
}
