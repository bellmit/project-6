package com.miguan.xuanyuan.common.interceptor;

import com.alibaba.fastjson.JSON;
import com.cgcg.base.core.exception.CommonException;
import com.miguan.xuanyuan.common.util.ResultMap;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.connector.ClientAbortException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * @Author shixh
 * @Date 2019/9/18
 **/
@Slf4j
@ControllerAdvice
@RestController
public class ControllerExceptionHandler {
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public String methodNotSupportHandle(HttpServletRequest request, HttpServletResponse response, HttpRequestMethodNotSupportedException e) {
        String ip = request.getHeader("x-forwarded-for");
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        log.error("不支持的请求方法，请求路径:"+request.getRequestURL().toString()+";请求IP："+ip);
        String json = JSON.toJSONString(ResultMap.error("不支持的请求方法!"));
        return json;
    }

    @ExceptionHandler(ClientAbortException.class)
    public String clientAbortHandle(ClientAbortException clientAbortException) {
        log.error("请求无法到达:"+clientAbortException.getMessage());
        String json = JSON.toJSONString(ResultMap.error("请求无法到达!"));
        return json;
    }

    @ExceptionHandler(CommonException.class)
    public String commonHandle(CommonException commonException) {
        String json = JSON.toJSONString(ResultMap.error(commonException.getMessage()));
        return json;
    }

    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    public String jsonParseExceptionHandle(org.springframework.http.converter.HttpMessageNotReadableException ex) {
        log.error("解析错误:"+ ex.getMessage());
        String json = JSON.toJSONString(ResultMap.error("请求参数错误（json格式错误或类型错误等）"));
        return json;
    }



}
