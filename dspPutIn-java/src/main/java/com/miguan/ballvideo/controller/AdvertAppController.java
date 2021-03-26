package com.miguan.ballvideo.controller;

import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.service.dsp.AdvertAppService;
import com.miguan.ballvideo.vo.response.AdvertAppSimpleRes;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;


@Slf4j
@Api(value="应用 Controller",tags={"应用接口"})
@RestController
@RequestMapping("/api/AdvertApp")
public class AdvertAppController {

    @Resource
    private AdvertAppService advertAppService;

    @ApiOperation("获得应用列表")
    @GetMapping("/getAppList")
    public ResultMap<List<AdvertAppSimpleRes>> getAppList(@ApiParam("materialShape") Integer materialShape,@ApiParam("materialType") String materialType) {
        try {
            return ResultMap.success(advertAppService.getAppList(materialShape,materialType));
        }  catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error();
        }
    }
}
