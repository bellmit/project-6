package com.miguan.xuanyuan.controller.front;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.miguan.xuanyuan.common.util.CaptchaUtil;
import com.miguan.xuanyuan.common.util.ResultCode;
import com.miguan.xuanyuan.common.util.ResultMap;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.google.code.kaptcha.impl.DefaultKaptcha;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "验证按接口", tags = {"验证按接口"})
@Slf4j
@RestController
@RequestMapping("/api/front")
public class CaptchaController {

    @Resource
    private DefaultKaptcha captchaProducer;

    @Resource
    private CaptchaUtil captchaUtil;

    /**
     * 登录验证码图片
     */
    @RequestMapping(value = {"/captcha"})
    public void loginValidateCode(HttpServletRequest request, HttpServletResponse response) throws Exception{
//        ResponseCookie cookie = ResponseCookie.from("JSESSIONID", UUID.randomUUID().toString()) // key & value
//                .httpOnly(true)		// 禁止js读取
//                .secure(false)		// 在http下也传输
//                .domain("localhost")// 域名
//                .path("/")			// path
//                .maxAge(Duration.ofHours(1))	// 1个小时候过期
//                .sameSite("None")	// 大多数情况也是不发送第三方 Cookie，但是导航到目标网址的 Get 请求除外
//                .build()
//                ;

        captchaUtil.validateCode(request,response,captchaProducer, CaptchaUtil.LOGIN_VALIDATE_CODE);
    }

    /**
     * 检查验证码是否正确
     */
    @RequestMapping("/checkValidateCode")
    @ResponseBody
    public ResultMap checkLoginValidateCode(HttpServletRequest request, @RequestParam("validateCode")String validateCode) {
        if (StringUtils.isEmpty(validateCode)) {
            return ResultMap.error(ResultCode.CAPTCHA_ERROR);
        }
        boolean validateCodeResult = captchaUtil.checkLoginValidateCode(request, validateCode);
        if (validateCodeResult) {
            return ResultMap.success();
        }
        return ResultMap.error(ResultCode.CAPTCHA_ERROR);
    }
}
