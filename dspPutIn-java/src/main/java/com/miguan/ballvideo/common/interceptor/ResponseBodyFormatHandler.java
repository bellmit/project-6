package com.miguan.ballvideo.common.interceptor;

import com.alibaba.fastjson.JSON;
import com.cgcg.base.util.AnnotationUtil;
import com.cgcg.context.util.ReflectionUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.Order;
import org.springframework.http.MediaType;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.Objects;

/**
 * @Author shixh
 * @Date 2020/3/4
 **/
@Slf4j
@Order(1)
@ControllerAdvice
public class ResponseBodyFormatHandler implements ResponseBodyAdvice {

    @Override
    public boolean supports(MethodParameter returnType, Class aClass) {
        Class<?> declaringClass = ((Method) Objects.requireNonNull(returnType.getMethod())).getDeclaringClass();
        return returnType.hasMethodAnnotation(ResponseBody.class) || AnnotationUtil.hasResponseBody(declaringClass);
    }

    @Override
    public Object beforeBodyWrite(Object body, MethodParameter returnType, MediaType mediaType, Class selectedConverterType, ServerHttpRequest request, ServerHttpResponse response) {
        URI uri = request.getURI();
        String path = uri.getPath();
        if(!path.contains("/swagger-resources") && !path.equals("/error") && !path.equals("/v2/api-docs")) {
            if(returnType.hasMethodAnnotation(ExceptionHandler.class)) {
                return body;
            } else {
                return this.getFormatResult(body, selectedConverterType);
            }
        } else {
            return body;
        }
    }

    private Object getFormatResult(Object body, Class selectedConverterType) {
        Class<?> formatClass = com.miguan.ballvideo.common.util.ResultMap.class;
        if(body != null && body.getClass() == formatClass) {
            return body;
        } else {
            try {
                Object result = formatClass.newInstance();
                ReflectionUtils.setFieldValue(result, "data", body);
                return selectedConverterType == StringHttpMessageConverter.class? JSON.toJSONString(result):result;
            } catch (Exception var6) {
                log.warn(var6.getMessage(), var6);
                return body;
            }
        }
    }
}
