package com.miguan.ballvideo.controller;

import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.service.LiJieUserInfoService;
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
@Api(value="删除李杰账号信息接口",tags={"删除李杰账号信息接口"})
@RestController
@RequestMapping("/api/delete")
public class LiJieUserInfoController {

    @Resource
    private LiJieUserInfoService liJieUserInfoService;

    @ApiOperation("删除李杰账号信息")
    @GetMapping("/UserInfo")
    public ResultMap findABUserInfo() {
        liJieUserInfoService.deleteUserInfo();
        return ResultMap.success();
    }

    @ApiOperation("根据设备号删除账号信息")
    @GetMapping("/UserInfoByDeviceId")
    public ResultMap userInfoByDeviceId(@ApiParam("用户设备号") String deviceId,
                                        @ApiParam("马甲包：1-茜柚视频，2-果果视频，3-逗趣视频，4-蜜桃视频，5-茜柚视频极速版") String type) {
        int result = liJieUserInfoService.deleteUserInfo(deviceId, type);
        return ResultMap.success(result);
    }
}
