package com.miguan.recommend.controller;

import com.miguan.recommend.service.xy.NewUserSelectionService;
import com.miguan.recommend.vo.ResultMap;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/api/cache/init")
public class CacheInitController {

    @Resource
    private NewUserSelectionService userSelectionService;

    @ApiOperation("首刷用户视频缓存刷新")
    @PostMapping("/firstFlush")
    public ResultMap firstFlush(@RequestParam(name = "catid", required = false) String catid) {
        userSelectionService.updateVideoByCatId(catid);
        return ResultMap.success();
    }

}
