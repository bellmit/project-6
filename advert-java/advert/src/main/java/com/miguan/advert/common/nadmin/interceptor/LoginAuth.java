package com.miguan.advert.common.nadmin.interceptor;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

/**
 * 增加用户登录权限校验注解
 * @author suhongju
 * @date 2020/09/24
 */
@Retention(RUNTIME)
@Target(METHOD)
public @interface LoginAuth {

}
