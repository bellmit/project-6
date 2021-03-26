package com.miguan.ballvideo.common.aop;

import com.cgcg.base.core.exception.CommonException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Scope;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 并发控制，类似悲观锁，主要用于service层，control看业务场景也可以用
 * @author shixh
 * @date 191018
 * */
@Slf4j
@Component
@Scope
@Aspect
@Order(1)
public class ServicelockAspect {

  private Lock lock = new ReentrantLock(true);

  @Pointcut("@annotation(com.miguan.ballvideo.common.aop.ServiceLock)")
  public void lockAspect() {
  }

  @Around("lockAspect()")
  public Object around(ProceedingJoinPoint joinPoint){
    lock.lock();
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    ServiceLock serviceLock = method.getAnnotation(ServiceLock.class);
    Object obj = null;
    try {
      obj = joinPoint.proceed();
    } catch (Throwable e) {
        log.error("加锁业务逻辑错误：{}",e);
        throw new CommonException(400,serviceLock.msg());
    } finally {
        lock.unlock();
    }
    return obj;
  }
}
