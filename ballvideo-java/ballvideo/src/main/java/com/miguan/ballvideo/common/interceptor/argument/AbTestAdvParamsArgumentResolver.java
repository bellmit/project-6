package com.miguan.ballvideo.common.interceptor.argument;

import com.miguan.ballvideo.common.aop.AbTestAdvParams;
import com.miguan.ballvideo.common.interceptor.argument.params.AbTestAdvParamsVo;
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
		String abExp = request.getHeader("ab-exp");
		if (StringUtils.isNotEmpty(abTestId)) {
			advParamsVo.setAbTestId(abTestId);
		}
		if(StringUtils.isNotEmpty(abExp)){
			advParamsVo.setAbExp(abExp);
		}
		return advParamsVo;
	}
}
