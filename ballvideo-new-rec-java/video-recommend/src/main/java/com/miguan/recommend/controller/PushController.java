package com.miguan.recommend.controller;

import com.miguan.recommend.service.PushService;
import com.miguan.recommend.vo.PushPredictVo;
import com.miguan.recommend.vo.ResultMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@RestController
@RequestMapping("/api/push")
public class PushController {

    @Resource
    private PushService pushService;

    @PostMapping("/predictCount")
    public ResultMap<PushPredictVo> predictCount(String catids) {
        if(StringUtils.isEmpty(catids)){
            return ResultMap.error("参数catids不能为空");
        }

        Long count = pushService.countPush(null, catids);
        return ResultMap.success(new PushPredictVo(count));
    }
}
