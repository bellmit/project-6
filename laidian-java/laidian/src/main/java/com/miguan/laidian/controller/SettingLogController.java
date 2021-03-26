package com.miguan.laidian.controller;

import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.entity.AuthoritySettingErrlog;
import com.miguan.laidian.service.SettingLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

@Api(value="权限设置日志controll",tags={"权限设置日志接口"})
@RestController
@RequestMapping("/api/authoritySetting")
public class SettingLogController {
    @Resource
    private SettingLogService settingLogService;

    @ApiOperation("权限设置异常日志接口")
    @PostMapping("/errLogInfo")
    public ResultMap settingErrLogInfo(@ModelAttribute AuthoritySettingErrlog authoritySettingErrlog) {
        AuthoritySettingErrlog result = settingLogService.addSettingErrLogInfo(authoritySettingErrlog);
        return ResultMap.success(result);
    }
}
