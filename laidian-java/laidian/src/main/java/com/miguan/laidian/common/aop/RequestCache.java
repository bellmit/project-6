package com.miguan.laidian.common.aop;

import java.lang.annotation.*;


@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
@Documented    
public  @interface RequestCache {

	int expire() default 300;//单位 秒


}
