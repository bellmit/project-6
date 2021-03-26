package com.miguan.ballvideo.common.aop;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 访问限制
 * @author xujinbang
 * @date 2019/12/16.
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface AccessLimit {

    int seconds() default 60;

    int maxCount() default 40;

    boolean needLogin()default true;

}
