package com.miguan.ballvideo.springTask.handler;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;

import java.lang.reflect.Method;

/**
 * @Author shixh
 * @Date 2019/9/9
 **/
public class SpringAsyncExceptionHandler implements AsyncUncaughtExceptionHandler {

    public Log log = LogFactory.getLog(SpringAsyncExceptionHandler.class);

    @Override
    public void handleUncaughtException(Throwable arg0, Method arg1, Object... arg2) {
        log.error("Exception occurs in async method", arg0);
    }
}
