package com.miguan.ballvideo.controller;

import cn.jiguang.common.utils.StringUtils;
import com.miguan.ballvideo.common.util.DateUtil;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.common.util.file.AWSUtil;
import com.miguan.ballvideo.common.util.file.UploadFileModel;
import com.miguan.ballvideo.service.ClDeviceService;
import com.miguan.ballvideo.service.ClUserService;
import com.miguan.ballvideo.vo.ClDeviceVo;
import com.miguan.ballvideo.vo.ClUserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value="用户登录注册controller",tags={"用户登录注册"})
@RestController
public class LoginController {

    @Autowired
    private ClUserService clUserService;

    @Autowired
    private ClDeviceService clDeviceService;

    /**
     * 登录
     * @param request
     * @param deviceVo
     */
    @ApiOperation(value = "启动首页")
    @PostMapping("/api/user/startup")
    public ResultMap<Map> startup(HttpServletRequest request, @ApiParam("设备实体") @ModelAttribute ClDeviceVo deviceVo) {
        if(deviceVo == null){
            return ResultMap.error("参数异常！");
        }
        if(StringUtils.isEmpty(deviceVo.getDeviceId()) || StringUtils.isEmpty(deviceVo.getAppPackage())){
            return ResultMap.error("参数异常！");
        }
        boolean result  = clDeviceService.startup(request, deviceVo);
        if(result) {
            //推送token失败
            return ResultMap.success(null,"推送成功！");
        } else {
            //推送token成功
            return ResultMap.error("推送失败！");
        }
    }

    /**
     * 登录
     * @param request
     * @param clUserVo
     * @param vcode
     */
    @ApiOperation(value = "用户登录")
    @PostMapping("/api/user/login")
    public ResultMap<Map> login(HttpServletRequest request, @ApiParam("用户实体") @ModelAttribute ClUserVo clUserVo, @ApiParam("手机验证码")String vcode) {
        if(clUserVo!=null && StringUtils.isEmpty(clUserVo.getLoginName())){
            return ResultMap.error("参数异常！");
        }
        Map<String, Object>  result = clUserService.login(request, clUserVo, vcode);
        String success = result.get("success").toString();
        if("0".equals(success)) {
            //登录成功
            return ResultMap.success(result,"登录成功");
        } else {
            //登录失败
            return ResultMap.error(result,"登录失败");
        }
    }

    @ApiOperation(value = "上传头像图片到白山云返回URL")
    @PostMapping(value = "/api/video/getHeadImgUrl")
    public ResultMap getHeadImgUrl(@RequestParam MultipartFile headImageFile,
                                @ApiParam("用户ID") String userId) {
        Map<String, Object> result = new HashMap<>();
        if (headImageFile != null) {
            Date currdate = DateUtil.getNow();
            UploadFileModel activeModel = AWSUtil.upload(headImageFile, userId, "userHead","head",currdate);
            result.put("bsyHeadImgUrl", activeModel.getResPath());
            return ResultMap.success(result);
        }
        return ResultMap.error("请上传图片");
    }

    @ApiOperation(value = "用户信息查询")
    @GetMapping("/api/user/findClUserInfo")
    public ResultMap findClUserInfo(HttpServletRequest request,
                                    @ApiParam("账号（手机号）") String loginName){
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("loginName", loginName);
        List<ClUserVo> clUserVoList = clUserService.findClUserList(params);
        if (clUserVoList.size() > 0) {
            return ResultMap.success(clUserVoList.get(0));
        } else {
            return ResultMap.error("用户信息查询失败");
        }

    }

    @ApiOperation(value = "用户信息更新")
    @PostMapping("/api/user/updateClUserInfo")
    public ResultMap updateClUserInfo(@ApiParam("用户实体") @ModelAttribute ClUserVo clUserVo){
        int num = clUserService.updateClUser(clUserVo);
        if (num == 1) {
            return ResultMap.success("用户信息更新成功");
        } else {
            return ResultMap.error("用户信息更新失败");
        }
    }

    @ApiOperation(value = "用户注销")
    @PostMapping("/api/user/logoff")
    public ResultMap logoff(@ApiParam("用户Id")  Long userId,
                            @ApiParam("手机验证码") String vcode){
        if(userId==null ||userId<0)return ResultMap.error("参数异常");
        clUserService.deleteByUserId(userId, vcode);
        return ResultMap.success("用户信息注销成功");
    }

    @ApiOperation(value = "获取虚拟用户信息")
    @GetMapping("/api/user/findDefaultInfo")
    public ResultMap<List<ClUserVo>> findDefaultInfo(){
        //查询id 1到20的虚拟用户信息
        Map<String, Object> params = new HashMap<String, Object>();
        params.put("defaultFlag", "1");
        List<ClUserVo> clUserVoList = clUserService.findClUserList(params);
        if (clUserVoList.size() > 0) {
            return ResultMap.success(clUserVoList);
        } else {
            return ResultMap.error("用户信息查询失败");
        }

    }
}
