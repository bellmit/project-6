package com.miguan.ballvideo.common.interceptor.argument;

import com.miguan.ballvideo.common.aop.AbTestAdvParams;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

/**
 * rest接口封装AbTestAdvParams参数
 * @author laiyd
 */
@Slf4j
@Component
public class AbTestAdvParamsArgumentResolver implements HandlerMethodArgumentResolver {

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(AbTestAdvParams.class);
	}

	@Override
	public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
		AbTestAdvParamsVo advParamsVo = new AbTestAdvParamsVo();
		HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
		String abTestId = request.getHeader("abTestId");
		if (StringUtils.isNotEmpty(abTestId)) {
			advParamsVo.setAbTestId(abTestId);
			log.info("设备号："+request.getParameter("deviceId")+"；abTestId:"+abTestId);
		}
		return advParamsVo;
	}
}
