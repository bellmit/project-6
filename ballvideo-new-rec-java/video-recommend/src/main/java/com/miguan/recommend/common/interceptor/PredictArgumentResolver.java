package com.miguan.recommend.common.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.miguan.recommend.bo.PredictDto;
import com.miguan.recommend.bo.UserFeature;
import com.miguan.recommend.common.aop.Predict;
import com.miguan.recommend.common.constants.SymbolConstants;
import com.miguan.recommend.service.recommend.FeatureService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.servlet.http.HttpServletRequest;

import static org.apache.commons.lang3.StringUtils.isEmpty;

@Slf4j
@Component
public class PredictArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Predict.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String device_id = request.getParameter("device_id");
        String channel = request.getParameter("channel");
        String package_name = request.getParameter("package_name");
        String model = request.getParameter("model");
        String os = request.getParameter("os");
        String is_first_app = request.getParameter("is_first_app");
        String is_first = request.getParameter("is_first");
        String ip = request.getParameter("ip");

        BeanFactory factory = WebApplicationContextUtils.getRequiredWebApplicationContext(request.getServletContext());
        FeatureService featureService = (FeatureService) factory.getBean("featureService");
        UserFeature userFeature = featureService.initUserFeature(device_id, ip);
        log.info("PredictArgumentResolver userFeature:{}", JSONObject.toJSONString(userFeature));
        return new PredictDto(device_id, channel, package_name, model, os, Boolean.parseBoolean(is_first_app), Boolean.parseBoolean(is_first), userFeature);
    }

}
