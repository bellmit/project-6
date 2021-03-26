package com.miguan.ballvideo.common.aop;

import com.cgcg.base.core.exception.CommonException;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.cgcg.redis.core.entity.RedisLock;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 并发锁，用RedisLock实现
 * @author shixh 1101
 * */
@Slf4j
@Component
@Scope
@Aspect
@Order(1)
public class ConcurrentLockAspect {

  @Pointcut("@annotation(com.miguan.ballvideo.common.aop.ConcurrentLock)")
  public void concurrentLock() {
  }

  @Around("concurrentLock()")
  public Object around(ProceedingJoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    ConcurrentLock serviceLock = method.getAnnotation(ConcurrentLock.class);
    String message = serviceLock.message();
    RedisLock redisLock = new RedisLock(RedisKeyConstant.defalut+method, serviceLock.lockTime());
    if (redisLock.lock()) {
      Object obj = null;
      try {
        obj = joinPoint.proceed();
      } catch (Throwable e) {
        throw new CommonException(400,message);
      } finally{
        //redisLock.unlock();
      }
      return obj;
    } else {
      throw new CommonException(400,message);
    }
  }
}
