package com.miguan.bigdata.controller;

import com.google.common.base.Joiner;
import com.miguan.bigdata.dto.IncentiveVideoDto;
import com.miguan.bigdata.service.FlowStrategyService;
import com.miguan.bigdata.service.SysConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.logging.log4j.util.Strings;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import com.miguan.bigdata.vo.ResultMap;

@Api(value = "流量策略数据接口", tags = {"流量策略数据接口"})
@RestController
public class FlowStrategyController {
    @Resource
    private FlowStrategyService flowStrategyService;


    @ApiOperation(value = "获取需要做入库的激励视频id(返回结果的视频id，多个逗号分隔)")
    @GetMapping("/api/flow/strategy/inIncentiveVideo")
    public String inIncentiveVideo(@ApiParam("每个分类每日入库数") Integer configValue) {
        List<Long> list = flowStrategyService.staInIncentiveVideos(configValue);
        return Joiner.on(",").join(list);
    }

    @ApiOperation(value = "获取需要做出库的激励视频id(返回结果的视频id，多个逗号分隔)")
    @GetMapping("/api/flow/strategy/outIncentiveVideo")
    public String outIncentiveVideo( @ApiParam("保留视频数")Integer retainNum) {
        List<Long> list = null;
        if (retainNum == null) {
            return "";
        }
        list = flowStrategyService.staOutIncentiveVideos(retainNum);
        return Joiner.on(",").join(list);
    }
}
