package com.miguan.ballvideo.controller;

import com.miguan.ballvideo.common.util.ChannelUtil;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.service.ABTestService;
import com.miguan.ballvideo.vo.AbTestUserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author laiyd
 */
@Api(value="AB测试接口",tags={"AB测试接口"})
@RestController
@RequestMapping("/api/abTest")
public class ABTestController {

    @Resource
    private ABTestService aBTestService;

    @ApiOperation("根据设备Id查询返回A用户/B用户")
    @GetMapping("/findABUserInfo")
    public ResultMap findABUserInfo(@ApiParam(value = "设备ID") String deviceId,
                                    @ApiParam(value = "渠道ID") String channelId) {
        channelId = ChannelUtil.filterChannelId(channelId);
        AbTestUserVo abTestUserVo = aBTestService.findABUserInfo(deviceId, channelId);
        return ResultMap.success(abTestUserVo);
    }
}
