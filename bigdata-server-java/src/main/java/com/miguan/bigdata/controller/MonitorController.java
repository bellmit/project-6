package com.miguan.bigdata.controller;

import com.miguan.bigdata.common.util.RedisClient;
import com.miguan.bigdata.service.MonitorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(value = "监测接口", tags = {"监测接口"})
@RestController
public class MonitorController {
    @Resource
    private RedisClient redisClient;

    @ApiOperation(value = "修改监听埋点uuid为空的阀值")
    @PostMapping("/api/monitor/modifyThreshold")
    public void modifyThreshold(@ApiParam(value = "阀值类型,1--广告行为阀值，2--用户行为阀值") int type,
                              @ApiParam(value = "阀值") String value) {
        if(type == 1) {
            redisClient.set("adUuidThreshold", value);
        } else if(type == 2){
            redisClient.set("userUuidThreshold", value);
        }
    }
}
