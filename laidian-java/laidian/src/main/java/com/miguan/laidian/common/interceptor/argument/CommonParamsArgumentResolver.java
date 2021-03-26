package com.miguan.laidian.common.interceptor.argument;

import com.cgcg.base.core.exception.CommonException;
import com.miguan.laidian.common.annotation.CommonParams;
import com.miguan.laidian.common.params.CommonParamsVo;
import com.miguan.laidian.common.util.ChannelUtil;
import com.miguan.laidian.common.util.LaidianUtils;
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
 * rest接口封装CommonParams参数
 *
 * @author shixh
 */
@Slf4j
@Component
public class CommonParamsArgumentResolver implements HandlerMethodArgumentResolver {

    protected final String Params_error_MSG = "参数异常!";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(CommonParams.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        try {
            HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
            String appType = LaidianUtils.checkAppType(request.getParameter("appType"));
            String userId = request.getParameter("userId");
            String currentPage = request.getParameter("currentPage");
            String pageSize = request.getParameter("pageSize");
            String mobileType = request.getParameter("mobileType");
            String deviceId = request.getParameter("deviceId");
            String appVersion = request.getParameter("appVersion");
            String channelId = ChannelUtil.filter(request.getParameter("channelId"));
            CommonParamsVo commonParamsVo = new CommonParamsVo();
            commonParamsVo.setAppType(LaidianUtils.checkAppType(appType));
            commonParamsVo.setUserId(userId);
            commonParamsVo.setCurrentPage(StringUtils.isNotEmpty(currentPage) ? Integer.parseInt(currentPage) : 0);
            commonParamsVo.setPageSize(StringUtils.isNotEmpty(pageSize) ? Integer.parseInt(pageSize) : 0);
            commonParamsVo.setMobileType(mobileType);
            commonParamsVo.setDeviceId(deviceId);
            commonParamsVo.setAppVersion(StringUtils.isBlank(appVersion) ? "1.9.0" : appVersion);
            commonParamsVo.setChannelId(channelId);
            return commonParamsVo;
        } catch (Exception e) {
            throw new CommonException(Params_error_MSG);
        }

    }

}
