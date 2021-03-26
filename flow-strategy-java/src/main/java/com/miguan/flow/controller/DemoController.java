package com.miguan.flow.controller;

import com.miguan.flow.dto.DemoDto;
import com.miguan.flow.service.DemoService;
import com.miguan.flow.service.common.RedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@Api(value = "demo接口", tags = {"demo接口"})
@RestController
public class DemoController {
    @Resource
    private DemoService demoService;
    @Resource
    private RedisService redisService;

    @ApiOperation(value = "demo列表")
    @PostMapping("/api/demo/listDemo")
    public List<DemoDto> listDemo(@ApiParam(value = "type 类型：1-视频，2-来电") Integer type) {
        redisService.set("zbl","sss", 1000);
        System.out.println(redisService.get("zbl"));
        redisService.del("zbl");
        return demoService.listDeom();
    }
}
