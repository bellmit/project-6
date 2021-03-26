package com.miguan.laidian.controller;


import com.miguan.laidian.common.annotation.CommonParams;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.DateUtil;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.common.util.StringUtil;
import com.miguan.laidian.service.ClSmsService;
import com.miguan.laidian.vo.SmsTplVo;
import com.miguan.laidian.vo.SmsVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
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
@Api(value = "短信controller", tags = {"短信接口"})
@RestController
@RequestMapping("/api/user")
public class SmsController {

    public static final Logger logger = LoggerFactory.getLogger(SmsController.class);

    @Resource
    private ClSmsService clSmsService;

    @ApiOperation("获取短信验证码")
    @PostMapping(value = "/sendSms.htm")
    public ResultMap sendSms(@ApiParam("公共请求参数") @CommonParams CommonParamsVo commomParams,
                             @ApiParam("手机号") @RequestParam(value = "phone") String phone,
                             @ApiParam("短信类型：login(登录)") @RequestParam(value = "type") String type) {
        String appType = commomParams.getAppType();
        Map<String, Object> data = new HashMap<String, Object>();
        String message = this.check(phone, type);
        if (StringUtil.isBlank(message)) {
            long countDown = clSmsService.findTimeDifference(phone, type, appType);
            if (countDown != 0) {
                data.put("countDown", countDown);
                data.put("state", "20");
                message = "获取短信验证码过于频繁，请稍后再试";
                logger.info("发送短信，phone：" + phone + "， type：" + type + "，发送前的校验结果message：" + message);
                return ResultMap.error(data, message);
            } else {
                logger.info("发送短信，phone：" + phone + "， type：" + type + "，准备发送");
                String orderNo = clSmsService.sendSms(phone, type, appType);
                if (StringUtil.isNotBlank(orderNo)) {
                    data.put("state", "10");
                    return ResultMap.success(data, "已发送，请注意查收");
                } else {
                    return ResultMap.error(400, "发送失败");
                }
            }
        } else {
            logger.info("发送短信，phone：" + phone + "， type：" + type + "，发送前的校验结果message：" + message);
            data.put("state", "20");
        }
        return ResultMap.error(data, message);
    }

    /**
     * 短信校验
     *
     * @param phone 手机号
     * @param type  短信类型：login(登录)
     * @return
     */
    private String check(String phone, String type) {
        List<SmsVo> smsList = clSmsService.countDayTime(phone, type);
        SmsTplVo smsTpl = clSmsService.querySmsTplInfoByType(type);
        Integer mostTime = smsTpl.getMaxSend();
        if (mostTime != null) {  //如果mostTime为空，则无限制次数
            if (mostTime - smsList.size() <= 0) {
                return "获取短信验证码过于频繁，请明日再试";
            }
        }

        if (StringUtil.isBlank(phone) || StringUtil.isBlank(type)) {
            return "手机号不能为空";
        }

        if (!StringUtil.isPhone(phone)) {
            return "手机号不正确";
        }

        if (StringUtil.equals("login", type)) {
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
