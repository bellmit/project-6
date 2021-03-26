package com.miguan.laidian.common.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.miguan.laidian.common.util.Global;
import com.miguan.laidian.common.util.LaidianUtils;
import com.miguan.laidian.redis.service.RedisClient;
import com.miguan.laidian.redis.util.RedisKeyConstant;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 来电项目拦截器
 */
@Component
public class LaidianInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(LaidianInterceptor.class);

    private static final String ERROR_NO_TOKEN = "700";  //无token

    private static final String ERROR_NEW_DEVICE = "701";  //根据token查询不到用户(您的账号在其他设备上登录，请重新登录)


    /**
     * 在请求处理之前进行调用（Controller方法调用之前）
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String appType = LaidianUtils.checkAppType(request);
        request.setAttribute("appType", appType);
        String appEnvironment = Global.getValue("app_environment", appType);
        String url = request.getRequestURI();
        //只有测试环境才暴露swagger的接口文档，正式环境不暴露
        if (url.indexOf("swagger") >= 0 && "prod".equals(appEnvironment)) {
            return false;
        } else if (url.indexOf("swagger") >= 0) {
            return true;
        }

        String tokenWwitch = Global.getValue("token_switch", appType);  //token开关，10：开，20：关
        if ("20".equals(tokenWwitch)) {
            return true;
        }

        BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
        RedisClient redisClient = (RedisClient) factory.getBean("redisClient");

        String channel = request.getHeader("channel");
        if ("ios".equals(channel)) {
            //ios 不需要做登录，所以不用传token
            return true;
        }
        JSONObject errorJson = new JSONObject();
        String token = request.getParameter("token");
        String userIdStr = request.getParameter("userId") == null ? "" : request.getParameter("userId");

        if (StringUtils.isBlank(token)) {
            //接口传来的token为空
            errorJson.put("code", ERROR_NO_TOKEN);
            errorJson.put("message", "缺少token");
            this.writeJson(errorJson.toJSONString(), response);
            return false;
        }

        String serviceToken = redisClient.get(RedisKeyConstant.USER_TOKEN_KEY + userIdStr);
        if (!token.equals(serviceToken)) {
            errorJson.put("code", ERROR_NEW_DEVICE);
            errorJson.put("message", "您的账号在其他设备上登录，请重新登录");
            this.writeJson(errorJson.toJSONString(), response);
            return false;
        }
        return true;
    }

    /**
     * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）
     */
    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
    }

    /**
     * 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
    }


    public static void writeJson(String json, HttpServletResponse resp) {
        PrintWriter pw = null;
        try {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("utf8");
            pw = resp.getWriter();
            pw.print(json);
            pw.flush();
            pw.close();
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            pw.close();
        }
    }


    public static Map<String, Object> getParams(HttpServletRequest request) {
        Map<String, String[]> rec = request.getParameterMap();
        Map<String, Object> result = new LinkedHashMap<String, Object>();

        for (Map.Entry<String, String[]> entry : rec.entrySet()) {
            String name = entry.getKey();
            Object value = entry.getValue()[0];
            result.put(name, value);
        }
        return result;
    }
}
