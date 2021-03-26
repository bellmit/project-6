package com.miguan.ballvideo.controller;

import com.miguan.ballvideo.common.util.DateUtil;
import com.miguan.ballvideo.common.util.Global;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.common.util.StringUtil;
import com.miguan.ballvideo.service.ClSmsService;
import com.miguan.ballvideo.vo.SmsTplVo;
import com.miguan.ballvideo.vo.SmsVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 短信Controller
 *
 * @author xy.chen
 * @version 1.0.0
 * @date 2019-08-12
 */
@Api(value = "短信接口controller",tags={"短信接口"})
@Slf4j
@RestController
public class SmsController {

    @Resource
    private ClSmsService clSmsService;

    //@ServiceLock
    @ApiOperation("获取短信验证码")
    @PostMapping("/api/user/sendSms.htm")
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
