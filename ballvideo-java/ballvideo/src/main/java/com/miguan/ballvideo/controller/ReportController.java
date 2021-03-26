package com.miguan.ballvideo.controller;

import com.alibaba.fastjson.JSONObject;
import com.miguan.ballvideo.service.QuTouTiaoService;
import com.miguan.ballvideo.vo.CheckQuTouTiaoVo;
import com.miguan.ballvideo.vo.SaveQuTouTiaoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@Api(value = "上报controller",tags={"上报接口"})
@RestController
@Slf4j
@RequestMapping(value = "/token")
public class ReportController {

    @Resource
    private QuTouTiaoService quTouTiaoService;

    @ApiOperation(value = "保存趣头条用户数据")
    @GetMapping(value = "/DetectionQuTouTiaoInfo")
    public Map<String, Object> detectionQuTouTiaoInfo(@ModelAttribute SaveQuTouTiaoVo params) {
        log.info("DetectionQuTouTiaoInfo.ball.request.data  : " + JSONObject.toJSONString(params));
        Map<String,Object> result = quTouTiaoService.insertSelective(params);
        log.info("DetectionQuTouTiaoInfo.ball.response.data : " + JSONObject.toJSONString(result));
        return result;
    }


    @ApiOperation(value = "上报趣头条用户数据")
    @PostMapping(value = "/getQuTouTiaoCheck")
    public Map<String, Object> getQuTouTiaoCheck(@RequestBody CheckQuTouTiaoVo params) {
        log.info("getQuTouTiaoCheck.ball.request.data : " + JSONObject.toJSONString(params));
        Map<String,Object> result = quTouTiaoService.selectByImeiAndAndroidid(params);
        log.info("getQuTouTiaoCheck.ball.response.data : " + JSONObject.toJSONString(result));
        return result;
    }
}
