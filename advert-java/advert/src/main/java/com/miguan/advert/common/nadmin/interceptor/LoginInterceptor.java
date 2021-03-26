package com.miguan.advert.common.nadmin.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.miguan.advert.common.nadmin.AuthRPCService;
import com.miguan.advert.common.util.ResultMap;
import lombok.extern.slf4j.Slf4j;
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
 * 登录拦截器
 */
@Component
@Slf4j
public class LoginInterceptor implements HandlerInterceptor {

	/**
	 * 在请求处理之前进行调用
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		//判断请求是否属于方法的请求
		if(!(handler instanceof HandlerMethod)) {
			return true;
		}
		HandlerMethod handlerMethod = (HandlerMethod) handler;
		//获取方法中的注解,看是否有该注解
		LoginAuth loginAuth = handlerMethod.getMethodAnnotation(LoginAuth.class);
		if(loginAuth == null){
			return true;
		}

		String authorization = request.getHeader("Authorization");
		String username = request.getParameter("username");
		String platForm = request.getParameter("platForm");
		BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
		AuthRPCService authRPCService = (AuthRPCService) factory.getBean("authRPCService");
		//统一登录鉴权
		ResultMap authRet = authRPCService.existAdmin(authorization, username, platForm);
		if (authRet.getCode() != 200) {
			this.writeJson(JSONObject.toJSONString(authRet), response);
			return false;
		}
		return true;
	}



	@Override
	public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
	}

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
