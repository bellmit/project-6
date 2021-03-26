package com.miguan.xuanyuan.common.security.handler;

import com.miguan.xuanyuan.common.constant.RedisConstant;
import com.miguan.xuanyuan.common.security.context.UserContext;
import com.miguan.xuanyuan.common.util.ResultMap;
import com.miguan.xuanyuan.common.security.model.JwtUser;
import com.miguan.xuanyuan.common.security.utils.JwtTokenUtil;
import com.miguan.xuanyuan.service.common.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import com.alibaba.fastjson.JSON;
import org.springframework.stereotype.Component;

/**
 * 登录成功
 */
@Component
@Slf4j
public class MyAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(MyAuthenticationSuccessHandler.class);


    @Resource
    private RedisService redisService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {

        JwtUser userDetails = (JwtUser)authentication.getPrincipal();
//        SecurityContextHolder.getContext().setAuthentication(authentication);

        //生成token
        String token = JwtTokenUtil.generateToken(userDetails);
        //保存redis
//        redisService.set(redisService.getUserTokenKey(userDetails.getUserId()), token, RedisConstant.USER_TOKEN_EXPIRES);

        Map<String, String> data = new HashMap<>();
        data.put("token", token);
        data.put("username", userDetails.getUsername());
        ResultMap resultMap = ResultMap.success(data);
        response.setContentType("Application/json;charset=UTF-8");
        Writer writer = response.getWriter();
        writer.write(JSON.toJSONString(resultMap));
        writer.flush();
        writer.close();
    }
}
