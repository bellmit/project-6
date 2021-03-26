package com.miguan.flow.controller;

import com.miguan.flow.dto.IncentiveParamDto;
import com.miguan.flow.dto.IncentiveVideoDto;
import com.miguan.flow.service.IncentiveVideoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "激励视频接口", tags = {"激励视频接口"})
@RestController
@Slf4j
@RequestMapping("/api/incentive")
public class IncentiveVideoController {

    @Resource
    private IncentiveVideoService incentiveVideoService;

    @ApiOperation(value = "获取激励视频")
    @PostMapping("/findIncentiveVideoList")
    public List<IncentiveVideoDto> findIncentiveVideoList(@RequestBody IncentiveParamDto paramDto) {
        return incentiveVideoService.findIncentiveVideoList(paramDto);
    }
}
