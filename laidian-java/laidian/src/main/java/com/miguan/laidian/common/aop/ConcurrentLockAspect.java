package com.miguan.laidian.common.aop;

import com.alibaba.fastjson.JSONObject;
import com.cgcg.base.core.exception.CommonException;
import com.miguan.laidian.common.params.CommonParamsVo;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.cgcg.redis.core.entity.RedisLock;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

/**
 * 并发锁，用RedisLock实现
 * @author shixh 1101
 * */
@Slf4j
@Aspect
@Configuration
public class ConcurrentLockAspect {

  public final String EXCEPTION = "请求用户过多，请稍后再试！";

  @Pointcut("@annotation(com.miguan.laidian.common.aop.ConcurrentLock)")
  public void concurrentLock() {
  }

  @Around("concurrentLock()")
  public Object around(ProceedingJoinPoint joinPoint) {
    MethodSignature signature = (MethodSignature) joinPoint.getSignature();
    Method method = signature.getMethod();
    ConcurrentLock serviceLock = method.getAnnotation(ConcurrentLock.class);
    String key = "";
    Object[] args = joinPoint.getArgs();
    if(args.length > 0){
      for (Object arg : args) {
        if(arg instanceof CommonParamsVo){
            key = JSONObject.toJSONString(arg);
        }
      }
    }
    RedisLock redisLock = new RedisLock("laidian:"+method+":"+key, serviceLock.lockTime());
    if (redisLock.lock()) {
      Object obj = null;
      try {
        obj = joinPoint.proceed();
      } catch (Throwable e) {
        throw new CommonException(400,EXCEPTION);
      }finally {
        redisLock.unlock();
      }
      return obj;
    } else {
      throw new CommonException(400,EXCEPTION);
    }
  }
}
