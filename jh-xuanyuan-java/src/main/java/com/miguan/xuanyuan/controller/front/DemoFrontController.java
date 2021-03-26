package com.miguan.xuanyuan.controller.front;

import com.miguan.xuanyuan.service.DemoService;
import com.miguan.xuanyuan.service.common.RedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @Description TODO
 * @Author zhangbinglin
 * @Date 2021/1/14 15:28
 **/
@Api(value = "测试接口", tags = {"测试接口"})
@RestController
@Slf4j
@RequestMapping("/api/front")
public class DemoFrontController {

    @Resource
    private DemoService demoService;
    @Resource
    private RedisService redisService;

    @ApiOperation("测试接口")
    @GetMapping("/test")
    public int advCodeInfoList(){
        redisService.set("aa","111", 60);
        System.out.println(redisService.get("aa"));
        return demoService.countDemo();
    }
}
