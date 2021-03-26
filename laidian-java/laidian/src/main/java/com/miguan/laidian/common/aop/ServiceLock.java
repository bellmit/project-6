package com.miguan.laidian.common.aop;
import java.lang.annotation.*;

@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)    
@Documented    
public @interface ServiceLock {
    String msg() default "您的手速太快，请稍后再试哦！";
}
