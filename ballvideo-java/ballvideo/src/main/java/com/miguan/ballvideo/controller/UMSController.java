package com.miguan.ballvideo.controller;

import cn.jiguang.common.utils.StringUtils;
import com.miguan.ballvideo.common.util.DateUtil;
import com.miguan.ballvideo.common.util.Global;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.common.util.StringUtil;
import com.miguan.ballvideo.common.util.file.AWSUtil;
import com.miguan.ballvideo.common.util.file.UploadFileModel;
import com.miguan.ballvideo.service.ClDeviceService;
import com.miguan.ballvideo.service.ClSmsService;
import com.miguan.ballvideo.service.ClUserService;
import com.miguan.ballvideo.vo.ClDeviceVo;
import com.miguan.ballvideo.vo.ClUserVo;
import com.miguan.ballvideo.vo.SmsTplVo;
import com.miguan.ballvideo.vo.SmsVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Api(value="UMS用户中心controller",tags={"UMS用户中心"})
@RestController
@RequestMapping("/uapi")
public class UMSController {

    @Autowired
    private ClUserService clUserService;

    @Autowired
    private ClDeviceService clDeviceService;

    @Resource
    private ClSmsService clSmsService;
    /**
     * 登录
     * @param request
     * @param deviceVo
     */
    @ApiOperation(value = "启动首页")
    @PostMapping("/user/startup")
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
    @PostMapping("/user/login")
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
    @PostMapping(value = "/video/getHeadImgUrl")
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
    @GetMapping("/user/findClUserInfo")
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
    @PostMapping("/user/updateClUserInfo")
    public ResultMap updateClUserInfo(@ApiParam("用户实体") @ModelAttribute ClUserVo clUserVo){
        int num = clUserService.updateClUser(clUserVo);
        if (num == 1) {
            return ResultMap.success("用户信息更新成功");
        } else {
            return ResultMap.error("用户信息更新失败");
        }
    }

    @ApiOperation(value = "用户注销")
    @PostMapping("/user/logoff")
    public ResultMap logoff(@ApiParam("用户Id")  Long userId,
                            @ApiParam("手机验证码") String vcode){
        if(userId==null ||userId<0)return ResultMap.error("参数异常");
        clUserService.deleteByUserId(userId, vcode);
        return ResultMap.success("用户信息注销成功");
    }

    @ApiOperation("获取短信验证码")
    @PostMapping("/user/sendSms.htm")
    public ResultMap sendSms(@ApiParam("手机号") @RequestParam(value = "phone") String phone,
                             @ApiParam("短信类型：login(登录),forgetPassword(忘记密码),cancel(注销帐号)") @RequestParam(value = "type") String type,
                             @ApiParam("马甲包名") String appPackage,
                             @ApiParam("版本号")String appVersion) {
        Map<String, Object> data = new HashMap<String, Object>();
        String appEnvironment = Global.getValue("app_environment");
        String isSendSms = Global.getValue("dev_is_send_sms");
        if ("dev".equals(appEnvironment)&&"20".equals(isSendSms)){
            data.put("state", "10");
            return ResultMap.success(data, "已发送，请注意查收");
        }
        String message = this.check(phone, type);
        if(!StringUtil.isBlank(message)){
            data.put("state", "20");
            return ResultMap.error(data, message);
        }
        long countDown = clSmsService.findTimeDifference(phone, type);
        if (countDown != 0) {
            data.put("countDown", countDown);
            data.put("state", "20");
            message = "获取短信验证码过于频繁，请稍后再试";
            return ResultMap.error(data, message);
        } else {
            String vcode = clSmsService.sendSms(phone, type, appPackage, appVersion);
            if (StringUtil.isNotBlank(vcode)) {
//                data.put("vcode", vcode);
                data.put("state", "10");
                return ResultMap.success(data, "已发送，请注意查收");
            } else {
                return ResultMap.error(400, "发送失败");
            }
        }
    }

    /**
     * 短信校验
     *
     * @param phone 手机号
     * @param type  短信类型：login(登录)
     * @return
     */
    private String check(String phone, String type) {
        if (StringUtil.isBlank(phone) || StringUtil.isBlank(type)) {
            return "手机号不能为空";
        }
        if (!StringUtil.isPhone(phone)) {
            return "手机号不正确";
        }
        List<SmsVo> smsList = clSmsService.countDayTime(phone, type);
        SmsTplVo smsTpl = clSmsService.querySmsTplInfoByType(type);
        Integer mostTime = smsTpl.getMaxSend();
        if (mostTime != null) {  //如果mostTime为空，则无限制次数
            if (mostTime - smsList.size() <= 0) {
                return "获取短信验证码过于频繁，请明日再试";
            }
        }
        if (StringUtil.equals("login", type) || StringUtil.equals("forgetPassword", type)) {
            Integer size = smsList.size();
            if (size > 0) {
                SmsVo lastSms = smsList.get(size - 1);
                int between = DateUtil.minuteBetween(DateUtil.getNow(), lastSms.getSendTime());
                if (between == 0) {
                    return "获取短信验证码过于频繁，请稍后再试";
                }
            }
        }
        return null;
    }
}
