package com.miguan.laidian.controller;

import com.miguan.laidian.common.annotation.CommonParams;
import com.miguan.laidian.common.constants.Constant;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.common.util.VersionUtil;
import com.miguan.laidian.service.SysConfigService;
import com.miguan.laidian.service.VersionInfoService;
import com.miguan.laidian.vo.ClUserVersion;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;


/**
 * 短信Controller
 *
 * @author hyl
 * @version 1.0.0
 * @date 2019年8月23日15:47:29
 */
@Api(value = "检查用户版本更新controller", tags = {"检查用户版本更新接口"})
@RestController
@RequestMapping("/api/app")
public class VersionController {

    @Autowired
    VersionInfoService versionInfoService;
    @Autowired
    private SysConfigService sysConfigService;

    /**
     * 判断用户灰度测试版本
     */
    @ApiOperation("判断用户灰度测试版本")
    @PostMapping(value = "/findSysVersionInfoAndUserId")
    public ResultMap findSysVersionInfoAndUserId(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                                 ClUserVersion clUserVersion) {
        String appType = commomParams.getAppType();
        String channelId = commomParams.getChannelId() == null ? "all" : commomParams.getChannelId();
        String appVersion = commomParams.getAppVersion();
        if(VersionUtil.compareIsHigh(appVersion, Constant.APPVERSION_253)){
            Map<String, Object> restMap = sysConfigService.getSysVersionInfo(appType,appVersion,channelId);
            return ResultMap.success(restMap);
        }else{
            Map<String, Object> restMap = versionInfoService.findSysVersionInfoAndUserId(appType, clUserVersion);
            return ResultMap.success(restMap);
        }
    }

    /**
     * 判断用户灰度测试版本  用户确认更新
     * 记录用户更新次数
     */
    @ApiOperation("记录用户更新次数")
    @PostMapping(value = "/addUserVersion")
    public ResultMap addUserVersionInfo(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                        ClUserVersion clUserVersion) {
        String appType = commomParams.getAppType();
        int i = versionInfoService.addUserVersionInfo(appType, clUserVersion);
        if (i > 0) {
            return ResultMap.success();
        } else {
            return ResultMap.error();
        }
    }

}
