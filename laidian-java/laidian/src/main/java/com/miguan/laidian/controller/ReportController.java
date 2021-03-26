package com.miguan.laidian.controller;

import com.alibaba.fastjson.JSONObject;
import com.miguan.laidian.service.QuTouTiaoService;
import com.miguan.laidian.vo.CheckQuTouTiaoVo;
import com.miguan.laidian.vo.SaveQuTouTiaoVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.Map;

@Api(value = "趣头条controller", tags = {"趣头条接口"})
@RestController
@Slf4j
@RequestMapping(value = "/token")
public class ReportController {

    @Resource
    private QuTouTiaoService quTouTiaoService;

    @ApiOperation(value = "保存趣头条用户数据")
    @GetMapping(value = "/DetectionQuTouTiaoInfo")
    public Map<String, Object> detectionQuTouTiaoInfo(@ModelAttribute SaveQuTouTiaoVo params) {
        try{
            Map<String,Object> result = quTouTiaoService.insertSelective(params);
            return result;
        }catch (Exception e){
            log.error(e.getMessage(),e);
            log.info("DetectionQuTouTiaoInfo.laidian.response.data : " + JSONObject.toJSONString(params));
            return null;
        }
    }


    @ApiOperation(value = "上报趣头条用户数据")
    @PostMapping(value = "/getQuTouTiaoCheck")
    public Map<String, Object> getQuTouTiaoCheck(@RequestBody CheckQuTouTiaoVo params) {
        Map<String,Object> result = quTouTiaoService.selectByImeiAndAndroidid(params);
        return result;
    }
}
