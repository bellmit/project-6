package com.miguan.ballvideo.common.interceptor.argument;

import com.miguan.ballvideo.common.aop.CommonParams;
import com.miguan.ballvideo.common.interceptor.argument.params.CommonParamsVo;
import com.miguan.ballvideo.common.util.ChannelUtil;
import com.miguan.ballvideo.common.util.VersionUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * rest接口封装CommonParams参数
 * @author shixh
 */
@Component
public class CommonParamsArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(CommonParams.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
				HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();

				//用于1.8版本以后
				String channelId = request.getParameter("channelId");
				String appType = request.getParameter("appType");
				String appPackage = request.getParameter("appPackage");
				String mobileType = request.getParameter("mobileType");
				String deviceId = request.getParameter("deviceId");
				String userId = request.getParameter("userId");
				String token = request.getParameter("token");
				String appVersion = request.getParameter("appVersion");
				String currentPage = request.getParameter("currentPage");
				String pageSize = request.getParameter("pageSize");
				String abTestId = request.getHeader("abTestId");
				CommonParamsVo commonParamsVo = new CommonParamsVo();
				commonParamsVo.setChannelId(channelId);
				commonParamsVo.setParentChannelId(ChannelUtil.filter(channelId));
				commonParamsVo.setAppType(appType);
				commonParamsVo.setAppPackage(appPackage);
				commonParamsVo.setMobileType(mobileType);
				commonParamsVo.setDeviceId(deviceId);
				commonParamsVo.setUserId(userId);
				commonParamsVo.setToken(token);
				commonParamsVo.setAppVersion(VersionUtil.getVersion(appVersion));
				if (StringUtils.isNotBlank(currentPage)){
					commonParamsVo.setCurrentPage(Integer.parseInt(currentPage));
				}
				if (StringUtils.isNotBlank(pageSize)){
					commonParamsVo.setPageSize(Integer.parseInt(pageSize));
				}
				if (StringUtils.isNotEmpty(abTestId)) {
					commonParamsVo.setAbTestId(abTestId);
				}
				return commonParamsVo;

	}

}
