package com.miguan.laidian.controller;


import com.miguan.laidian.common.annotation.CommonParams;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.DateUtil;
import com.miguan.laidian.common.util.Global;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.common.util.file.AWSUtil;
import com.miguan.laidian.common.util.file.UploadFileModel;
import com.miguan.laidian.service.ClDeviceService;
import com.miguan.laidian.service.ClUserService;
import com.miguan.laidian.vo.ClDeviceVo;
import com.miguan.laidian.vo.ClUserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value = "用户controller", tags = {"锁屏接口"})
@RestController
@RequestMapping("/api/user")
public class LoginController {

    @Resource
    private ClUserService clUserService;

    @Resource
    private ClDeviceService clDeviceService;

    /**
     * app启动保存设备信息
     * @param commomParams
     */
    @ApiOperation(value = "app启动保存设备信息")
    @PostMapping("/startup")
    public ResultMap<Map> startup(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                  @ApiParam("华为对设备的唯一标识") String  huaweiToken,
                                  @ApiParam("vivo对设备的唯一标识") String  vivoToken,
                                  @ApiParam("oppo对设备的唯一标识") String  oppoToken,
                                  @ApiParam("小米对设备的唯一标识") String  xiaomiToken) {
        ClDeviceVo clDeviceVo = new ClDeviceVo();
        BeanUtils.copyProperties(commomParams, clDeviceVo);
        if (StringUtils.isNotEmpty(huaweiToken)) {
            clDeviceVo.setHuaweiToken(huaweiToken);
        } else if (StringUtils.isNotEmpty(vivoToken)) {
            clDeviceVo.setVivoToken(vivoToken);
        } else if (StringUtils.isNotEmpty(oppoToken)) {
            clDeviceVo.setOppoToken(oppoToken);
        } else if (StringUtils.isNotEmpty(xiaomiToken)) {
            clDeviceVo.setXiaomiToken(xiaomiToken);
        } else {
            return ResultMap.error("保存失败：token不能为空！");
        }
        boolean result  = clDeviceService.startup(clDeviceVo);
        if(result) {
            return ResultMap.success(null,"保存成功！");
        } else {
            return ResultMap.error("保存失败！");
        }
    }

    /**
     * 登录
     * @param commomParams
     * @param clUserVo
     * @param vcode
     */
    @ApiOperation(value = "用户登录")
    @PostMapping("/login")
    public ResultMap<Map> login(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                @ApiParam("用户实体") @ModelAttribute ClUserVo clUserVo,
                                @ApiParam("手机验证码") String vcode) {
        String appType = commomParams.getAppType();
        Map<String, Object> result = clUserService.login(appType, clUserVo, vcode);
        String success = result.get("success").toString();
        if ("0".equals(success)) {
            return ResultMap.success(result, "登录成功");
        } else {
            return ResultMap.error(result);
        }
    }

    @ApiOperation(value = "上传头像图片到白山云返回URL")
    @RequestMapping(value = "/getHeadImgUrl", method = RequestMethod.POST)
    public ResultMap getHeadImgUrl(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                                   @RequestParam MultipartFile headImageFile) {
        String appType = commomParams.getAppType();
        String userId = commomParams.getUserId();
        if (headImageFile != null) {
            Map<String, Object> result = new HashMap<>();
            String appEnvironment = Global.getValue("app_environment",appType);
            String folder = "dev-headimg-laidian";
            if ("prod".equals(appEnvironment)) {
                folder = "pro-headimg-laidian";
            }
            UploadFileModel activeModel = AWSUtil.upload(headImageFile, userId, folder, "head", DateUtil.getNow());
            result.put("bsyHeadImgUrl", activeModel.getResPath());
            return ResultMap.success(result);
        }
        return ResultMap.error("请上传图片");
    }

    @ApiOperation(value = "用户信息查询")
    @GetMapping("/findClUserInfo")
    public ResultMap findClUserInfo(@ApiParam("账号（手机号）") String loginName) {
        if(StringUtils.isBlank(loginName)){
            return ResultMap.error("用户登陆名为空，用户信息查询失败");
        }
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("loginName", loginName);
        params.put("state", ClUserVo.USE);//状态（10 正常 20 禁用）
        List<ClUserVo> clUserVoList = clUserService.findClUserList(params);
        if (clUserVoList.size() > 0) {
            return ResultMap.success(clUserVoList.get(0));
        } else {
            return ResultMap.error("用户信息查询失败");
        }

    }

    @ApiOperation(value = "用户信息更新")
    @PostMapping("/updateClUserInfo")
    public ResultMap updateClUserInfo(@ApiParam("用户实体") @ModelAttribute ClUserVo clUserVo) {
        int num = clUserService.updateClUser(clUserVo);
        if (num == 1) return ResultMap.success("用户信息更新成功");
        return ResultMap.error("用户信息更新失败");
    }

}
