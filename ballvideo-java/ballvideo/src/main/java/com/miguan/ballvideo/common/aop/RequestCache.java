package com.miguan.ballvideo.common.aop;

import java.lang.annotation.*;

/**
 * 缓存标签，返回必须是ResultMap
 * */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented
public @interface RequestCache {

	int expire() default 300;


}
