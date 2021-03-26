package com.miguan.xuanyuan.common.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.google.common.collect.Lists;
import com.miguan.xuanyuan.common.util.DateUtils;
import com.miguan.xuanyuan.common.util.MapUtil;
import com.miguan.xuanyuan.common.util.ResultCode;
import com.miguan.xuanyuan.common.util.StringUtil;
import com.miguan.xuanyuan.service.XyAppService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.DigestUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

/**
 * SDK项目拦截器
 */
@Component
@Slf4j
public class SDKInterceptor implements HandlerInterceptor {

	private  final String TIMESTAMP = "timestamp";
	private  final String NONCE = "nonce";
	private  final String SIGN = "sign";
	private  final String APP_KEY = "appKey";
	private  final String SECRET_KEY = "secretKey";
	private  final String SHA = "SHA1";

	private final String CITY = "city";

	private XyAppService service;

	public SDKInterceptor() {
	}

	public SDKInterceptor(XyAppService service) {
		this.service = service;
	}


	/**
	 * 在请求处理之前进行调用（Controller方法调用之前）
	 */
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
		return validateAppInfo(request, response);
	}

	private boolean validateAppInfo(HttpServletRequest request, HttpServletResponse response) {
		// 获取request的URL，用于判断该请求是否超时
		String timestamp = request.getParameter(TIMESTAMP);
		// 获取请求nonce
		String nonce = request.getParameter(NONCE);
		// 获取sign,通过timestamp,nonce,token合并进行加密后密文，需要注意加密顺序，平台必须与APP一致
		String sign = request.getParameter(SIGN);

		// 获取appKey
		String appKey = request.getParameter(APP_KEY);
		// 获取secretKey
		String secretKey = request.getParameter(SECRET_KEY);
		// 获取SHA1
		String SHA1 = request.getParameter(SHA);
		// 数据简易校验
		JSONObject errorJson= new JSONObject();
		if(StringUtils.isEmpty(timestamp) || StringUtils.isEmpty(nonce) || StringUtils.isEmpty(sign)){
			//接口传来的token为空
			setErrorJson(errorJson, ResultCode.SDK_PARAMS_ERROR);
			this.writeJson(errorJson.toJSONString(), response);
			return false;
		}

		if(StringUtils.isEmpty(appKey) || (StringUtils.isEmpty(secretKey) && StringUtils.isEmpty(SHA1))){
			setErrorJson(errorJson, ResultCode.SDK_PARAMS_ERROR);
			this.writeJson(errorJson.toJSONString(), response);
			return false;
		}

		//判断是否超时
//		boolean isDelay = DateUtils.delayTime(timestamp, 30 * 60 * 1000);
//		if(isDelay){
//			//接口时间戳超时
//			errorJson.put("code", "701");
//			errorJson.put("message", "该时间戳已超时");
//			this.writeJson(errorJson.toJSONString(), response);
//			return false;
//		}
		List<String> decodeParam = Lists.newArrayList();
		decodeParam.add("city");
		//获取排序好的参数
		String param = "";
		try {
			param = getOrderParam(request,decodeParam);
		} catch (Exception e) {
			//参数错误
			setErrorJson(errorJson, ResultCode.SDK_PT_PARAMS_ERROR);
			this.writeJson(errorJson.toJSONString(), response);
			return false;
		}
		String md5Sign = DigestUtils.md5DigestAsHex(param.getBytes());
		if(StringUtils.isEmpty(sign) || !sign.equals(md5Sign)){
			//验证失败
			setErrorJson(errorJson, ResultCode.SDK_AUTH_ERROR);
			this.writeJson(errorJson.toJSONString(), response);
			return false;
		}
		if(!service.existAppInfo(appKey,secretKey,SHA1)){
			//不存在应用
			setErrorJson(errorJson, ResultCode.SDK_APP_NOT_EXIST);
			this.writeJson(errorJson.toJSONString(), response);
			return false;
		}
		return true;
	}

	//获取排序好的参数
	private String getOrderParam(HttpServletRequest request, List<String> decodeParam) throws UnsupportedEncodingException {
		//获取所有的请求参数
		Map<String, String[]> parameterMap = request.getParameterMap();
		Map<String, String[]> map = MapUtil.sortByKey(parameterMap);
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for (Map.Entry<String, String[]> entry:map.entrySet()) {
			String value =  (entry.getValue() == null && entry.getValue().length > 0? "" : StringUtil.toString(Lists.newArrayList(entry.getValue())));
			if(decodeParam.contains(entry.getKey())){
				value = URLEncoder.encode(value, "UTF-8");
			}
			if(SIGN.equals(entry.getKey())){
				continue;
			}
			if(first){
				sb.append(entry.getKey()+"=" + value);
				first = false;
			} else {
				sb.append("&" + entry.getKey()+"="+ value);
			}
		}
		return sb.toString();
	}

	/**
	 * 在整个请求结束之后被调用，也就是在DispatcherServlet 渲染了对应的视图之后执行（主要是用于进行资源清理工作）
	 */
	@Override
	public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
	}


	public static void setErrorJson(JSONObject errorJson, ResultCode resultCode) {
		errorJson.put("code", resultCode.getCode());
		errorJson.put("message", resultCode.getMsg());
	}


	public static void writeJson(String json, HttpServletResponse resp){
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
