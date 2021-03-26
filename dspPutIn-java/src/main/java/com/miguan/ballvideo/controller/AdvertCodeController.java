package com.miguan.ballvideo.controller;

import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.service.dsp.AdvertCodeService;
import com.miguan.ballvideo.vo.response.AdvertCodeSimpleRes;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;


@Slf4j
@Api(value="DSP代码位 Controller",tags={"DSP代码位接口"})
@RestController
@RequestMapping("/api/AdvertCode")
public class AdvertCodeController {

    @Resource
    private AdvertCodeService advertCodeService;

    @ApiOperation("获得代码位列表")
    @GetMapping("/getCodeList")
    public ResultMap<List<AdvertCodeSimpleRes>> getCodeList(@ApiParam("appId") Long appId,
                                                            @ApiParam("name") String name,
                                                            @ApiParam("materialShape") Integer materialShape,
                                                            @ApiParam("materialType") Integer materialType) {
        return ResultMap.success(advertCodeService.getCodeList(appId,name,materialShape,materialType));
    }
}
