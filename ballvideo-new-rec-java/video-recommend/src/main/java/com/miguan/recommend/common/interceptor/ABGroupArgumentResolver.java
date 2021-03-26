package com.miguan.recommend.common.interceptor;

import com.miguan.recommend.bo.ABGroupDto;
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
public class ABGroupArgumentResolver implements HandlerMethodArgumentResolver {

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(Predict.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        HttpServletRequest request = (HttpServletRequest) webRequest.getNativeRequest();
        String ab_exp = request.getParameter("ab_exp");

        return new ABGroupDto();
    }

    /**
     * 判断客户端请求头ab_exp是否包含实验组
     *
     * @param abExp             客户端请求头ab_exp
     * @param experimentalGroup 实验组
     * @return
     */
    private boolean isContainsExperimentalGroup(String abExp, String experimentalGroup) {
        if (isEmpty(abExp) || isEmpty(experimentalGroup)) {
            return false;
        }
        return StringUtils.containsAny(abExp, experimentalGroup.split(SymbolConstants.comma));
    }

}
