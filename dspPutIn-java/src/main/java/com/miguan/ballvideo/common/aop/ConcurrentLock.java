package com.miguan.ballvideo.common.aop;
import java.lang.annotation.*;

/**
 * 并发锁控制
 * @author shixh 1101
 */
@Target({ElementType.PARAMETER, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ConcurrentLock {
    int lockTime() default 100;//锁时间（秒）
    String message() default "请求用户过多，请稍后再试！";
}
