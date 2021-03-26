package com.miguan.xuanyuan.controller.back;

import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.entity.User;
import com.miguan.xuanyuan.mapper.UserMapper;
import com.miguan.xuanyuan.service.DemoService;
import com.miguan.xuanyuan.service.common.RedisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * @Description TODO
 * @Author zhangbinglin
 * @Date 2021/1/14 15:28
 **/
@Api(value = "测试接口", tags = {"测试接口"})
@RestController
@Slf4j
@RequestMapping("/api/back")
public class DemoBackController {

    @Resource
    private DemoService demoService;
    @Resource
    private RedisService redisService;

    @Resource
    private UserMapper userMapper;



    @ApiOperation("测试接口")
    @PostMapping("/test")
    public int advCodeInfoList(){
        redisService.set("aa","111", 60);
        System.out.println(redisService.get("aa"));
        return demoService.countDemo();
    }


    @ApiOperation("测试接口")
    @GetMapping("/user")
    public User getUserInfo(@RequestParam String username){
        User user = userMapper.getUserInfo(username);
        return user;
    }

}
