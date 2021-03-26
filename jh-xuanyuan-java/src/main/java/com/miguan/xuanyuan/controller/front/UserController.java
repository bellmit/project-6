package com.miguan.xuanyuan.controller.front;

import com.alibaba.fastjson.JSONObject;
import com.cgcg.context.util.StringUtils;
import com.miguan.xuanyuan.common.constant.XyConstant;
import com.miguan.xuanyuan.common.log.annotation.LogInfo;
import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.controller.AuthBaseController;
import com.miguan.xuanyuan.dto.request.*;
import com.miguan.xuanyuan.entity.XyUser;
import com.miguan.xuanyuan.service.*;
import com.miguan.xuanyuan.service.common.RedisService;
import com.miguan.xuanyuan.service.third.SmsService;
import com.miguan.xuanyuan.vo.UserVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.models.auth.In;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.regex.Pattern;
import org.springframework.core.env.Environment;

@Api(value = "用户管理", tags = {"用户管理"})
@Slf4j
@RestController
@RequestMapping("/api/front/user/")
public class UserController extends AuthBaseController {

    @Resource
    SmsService smsService;

    @Resource
    XyUserService xyUserService;

    @Resource
    private RedisService redisService;

    @Autowired
    private Environment env;

    @Resource
    private XyPlatAccountService xyPlatAccountService;


    @ApiOperation("获取短信验证码")
    @GetMapping(value = {"/getVerifiCode"})
    public ResultMap getVerifiCode(@RequestParam("phone") String phone) throws Exception{

        if (StringUtils.isEmpty(phone)) {
            return ResultMap.error("手机号码不能为空");
        }

        String pattern = "^1\\d{10}$";
        boolean isMatch = Pattern.matches(pattern, phone);
        if (!isMatch) {
            return ResultMap.error("请输入正确手机格式");
        }

        JSONObject result = null;
        try {
            int getVerifiCodeCnt = redisService.getRegisterVerifiCodeCntCache(phone);
            if (getVerifiCodeCnt >= 5) {
                return ResultMap.error("操作过于频繁，请稍后再试");
            }
            redisService.setRegisterVerifiCodeCntCache(phone);
            result = smsService.getRegisterVerifiCode(phone);
            int code = (int) result.get("code");
            String verifiCode = (String) result.get("verifiCode");
            if (code != 0) {
                return ResultMap.error("发送短信失败");
            }
            redisService.setRegisterVerifiCodeCache(phone, verifiCode);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error("发送短信错误");
        }

        if (env.getProperty("spring.profiles.active").equals("prod")) {
            return ResultMap.success();
        } else {
            return ResultMap.success(result);
        }

    }

    @ApiOperation("用户注册")
    @PostMapping(value = {"/register"})
    @LogInfo(pathName="前台-用户管理-用户注册",plat= XyConstant.FRONT_PLAT,type = XyConstant.LOG_ADD)
    public ResultMap register(@RequestBody @Valid UserRegisterRequest userRegisterRequest) {

        String password = userRegisterRequest.getPassword();
        String passwordRe = userRegisterRequest.getPasswordRe();
        if (!password.equals(passwordRe)) {
            return ResultMap.error("确认密码不一致");
        }

        if (xyUserService.getUserByPhone(userRegisterRequest.getPhone()) != null) {
            return ResultMap.error("手机号码已被注册");
        }

        if (xyUserService.getUserByUsername(userRegisterRequest.getUsername()) != null) {
            return ResultMap.error("用户名已被使用");
        }

//        Integer type = userRegisterRequest.getType();
//        if (type == null ) {
//            return ResultMap.error("type不能为空");
//        }
//
//        if (type != XyConstant.MEDIA_TYPE_BUSINESS && type != XyConstant.MEDIA_TYPE_PERSON) {
//            return ResultMap.error("type数据错误");
//        }
        userRegisterRequest.setType(XyConstant.MEDIA_TYPE_BUSINESS);

        String verifiCode = userRegisterRequest.getVerifiCode();
        String phone = userRegisterRequest.getPhone();
        String verifiCodeCache = redisService.getRegisterVerifiCodeCache(phone);
        if (StringUtils.isEmpty(verifiCodeCache)) {
            return ResultMap.error("验证码不存在，请重新获取");
        }
        if (!verifiCodeCache.equals(verifiCode)) {
            return ResultMap.error("短信验证码错误");
        }

        XyUser xyUser = userRegisterRequest.convertUserEntity();
        try {
            xyUserService.saveUser(xyUser);
            //每个账号注册后，初始化【穿山甲】【广点通】【快手】三个平台，仅做账号管理功能
            xyPlatAccountService.insertDefaultPlatAccount(xyUser.getId());
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error("保存数据错误");
        }

        UserVo vo = new UserVo();
        BeanUtils.copyProperties(xyUser, vo);
        return ResultMap.success(vo);
    }

    @ApiOperation("重置密码")
    @PostMapping(value = {"/resetPassword"})
    @LogInfo(pathName="前台-用户管理-重置密码",plat= XyConstant.FRONT_PLAT,type = XyConstant.LOG_UPDATE)
    public ResultMap resetPassword(@RequestBody @Valid ResetPasswordRequest resetPasswordRequest) {

        String password = resetPasswordRequest.getPassword();
        String passwordRe = resetPasswordRequest.getPasswordRe();
        if (!password.equals(passwordRe)) {
            return ResultMap.error("确认密码不一致");
        }

        XyUser xyUser = xyUserService.getUserByPhone(resetPasswordRequest.getPhone());
        if (xyUser == null) {
            return ResultMap.error("该手机号没有被注册");
        }

        String verifiCode = resetPasswordRequest.getVerifiCode();
        String phone = resetPasswordRequest.getPhone();
        String verifiCodeCache = redisService.getRegisterVerifiCodeCache(phone);
        if (StringUtils.isEmpty(verifiCodeCache)) {
            return ResultMap.error("验证码不存在，请重新获取");
        }
        if (!verifiCodeCache.equals(verifiCode)) {
            return ResultMap.error("短信验证码错误");
        }

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        password = passwordEncoder.encode(password);
        xyUser.setPassword(password);

        try {
            xyUserService.updateById(xyUser);
        } catch (Exception e) {
            e.printStackTrace();
            return ResultMap.error("保存数据错误");
        }

        UserVo vo = new UserVo();
        BeanUtils.copyProperties(xyUser, vo);
        return ResultMap.success(vo);
    }







}
