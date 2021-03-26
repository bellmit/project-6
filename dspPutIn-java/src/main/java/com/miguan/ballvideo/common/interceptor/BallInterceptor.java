package com.miguan.ballvideo.common.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.miguan.ballvideo.common.aop.AccessLimit;
import com.miguan.ballvideo.common.util.Global;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 项目拦截器
 */
@Component
@Slf4j
public class BallInterceptor implements HandlerInterceptor {


//	private final String ERROR_NO_TOKEN = "700";  //无token
//
//	private final String ERROR_NEW_DEVICE= "701";  //根据token查询不到用户(您的账号在其他设备上登录，请重新登录)

	/**
	 * 在请求处理之前进行调用（Controller方法调用之前）
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

		return true;
	}


	/**
	 * 请求处理之后进行调用，但是在视图被渲染之前（Controller方法调用之后）s
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


	public static void writeJson(String json,HttpServletResponse resp){
		PrintWriter pw = null;
		try {
			resp.setContentType("application/json");
			resp.setCharacterEncoding("utf8");
			pw = resp.getWriter();
			pw.print(json);
			pw.flush();
			pw.close();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
		} finally {
			pw.close();
		}
	}
}
