package com.miguan.ballvideo.common.aop;

import com.alibaba.fastjson.JSON;
import com.miguan.ballvideo.common.util.ResultMap;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
import com.miguan.ballvideo.service.RedisService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.lang.reflect.Method;

/**
 * 对相同URL参数进行缓存，主要用于control层，看业务场景service也可以用
 *
 * @author shixh
 * @date 191018
 */
@Slf4j
@Aspect
@Configuration
public class RequestCacheAspect {

	@Resource
	private RedisService redisService;

	@Pointcut("@annotation(com.miguan.ballvideo.common.aop.RequestCache)")
	public void ServiceAspect() {

	}

	@Around("ServiceAspect()")
	public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
		String cacheKey = getCacheKey(joinPoint);
		String value = redisService.get(RedisKeyConstant.REQUEST_CACHE_KEY+cacheKey,String.class);
		if (StringUtils.isNotEmpty(value)) {
			log.info("cache hit，key [{}]", cacheKey);
			ResultMap map = JSON.parseObject(value,ResultMap.class);
			return map;
		} else {
			log.info("cache miss，key [{}]", cacheKey);
			Object result = joinPoint.proceed(joinPoint.getArgs());//joinPoint.proceed();
			if (result == null) {
				log.error("fail to get data from source，key [{}]", cacheKey);
			} else {
				MethodSignature signature = (MethodSignature) joinPoint.getSignature();
				Method method = signature.getMethod();
				RequestCache requestCache = method.getAnnotation(RequestCache.class);
				redisService.set(RedisKeyConstant.REQUEST_CACHE_KEY + cacheKey, JSON.toJSONString(result) + "", requestCache.expire());
			}
			return result;
		}
	}

	private String getCacheKey(ProceedingJoinPoint joinPoint) {
		return String.format("%s.%s",
				joinPoint.getSignature().toString().split("\\s")[1], StringUtils.join(joinPoint.getArgs(), ","));

	}

}
