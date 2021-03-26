package com.miguan.ballvideo.controller;

import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.entity.UserLabel;
import com.miguan.ballvideo.service.UserLabelService;
import com.miguan.ballvideo.vo.QueryParamsVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Slf4j
@Api(value="用户标签controll",tags={"用户标签接口"})
@RestController
@RequestMapping("/api/userLabel")
public class UserLabelController {

    @Resource
    private UserLabelService userLabelService;

    @ApiOperation("更新用户标签接口（老版本1.8）")
    @PostMapping("/updateUserLabel")
    public ResultMap<UserLabel> updateUserLabel(@ModelAttribute QueryParamsVo params) {
        UserLabel userLabel = userLabelService.updateUserLabelInfo(params);
        log.info("更新用户标签接口:deviceId-" + params.getDeviceId() + ",cat1-" + userLabel.getCatId1() + ",cat2" + userLabel.getCatId2());
        return ResultMap.success(userLabel);
    }

    @ApiOperation("迁移过期用户标签数据")
    @PostMapping("/deleteUserLabel")
    public ResultMap deleteUserLabel(){
        userLabelService.deleteUserLabelDatas();
        return ResultMap.success();
    }

/*
    @ApiOperation("更新用户标签接口1.8.0（测试用）")
    @PostMapping("/updateUserLabel/1.8")
    public ResultMap updateUserLabel(@ApiParam("设备ID") String deviceId) {
        UserLabel userLabel = userLabelService.updateUserLabelInfo(deviceId);
        return ResultMap.success(userLabel);
    }
    @ApiOperation("更新用户标签接口1.8.0（上线批量修改用）")
    @PostMapping("/updateOldUserLabel/1.8")
    public ResultMap updateUserLabel() {
        int userLabel = userLabelService.updateUserLabelInfo();
        return ResultMap.success("更新成功:" + userLabel + "条");
    }
*/

}
