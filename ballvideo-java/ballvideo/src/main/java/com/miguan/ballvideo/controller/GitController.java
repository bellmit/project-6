package com.miguan.ballvideo.controller;

import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.common.util.redis.RedisClient;
import com.miguan.ballvideo.entity.Git;
import com.miguan.ballvideo.service.GiService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value="测试接口",tags={"测试接口"})
@RestController
public class GitController {

    @Resource
    private GiService giService;

    @Resource
    private RedisClient redisClient;

    @ApiOperation("测试接口")
    @GetMapping(value = "/out/queryMapperTest.htm")
    public Map queryMapperTest() {
//        String asa = Global.getValue("app_environment");
        Map<String, Object> result = new HashMap<>();
        Map<String, Object> map = new HashMap<>();
        map.put("测试", "哈哈123");
        //       List<Map<String, Object>> test = giService.queryProductList(new HashMap<>());
        List<Map<String,Object>> list = new ArrayList<>();
        list.add(map);
        result.put("list", list);
        try {
        } catch (Exception e) {
            e.printStackTrace();
        }

        return map;
    }

    @GetMapping(value = "/quaryJpaAll.htm")
    public ResultMap<List<Git>> quaryJpaAll() {
        List<Map<String, Object>> test = giService.queryProductList(new HashMap<>());

        redisClient.set("zbl", "test", 60 * 60 * 24);

        String tt = redisClient.get("zbl");
        List<Git> list = giService.findAll();
        return ResultMap.success(list);
    }

}
