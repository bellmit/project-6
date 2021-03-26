package com.miguan.laidian.common.aop;

import com.alibaba.fastjson.JSON;
import com.miguan.laidian.common.util.ResultMap;
import com.miguan.laidian.entity.Video;
import com.miguan.laidian.redis.service.RedisClient;
import com.miguan.laidian.redis.service.RedisService;
import com.miguan.laidian.redis.util.RedisKeyConstant;
import com.miguan.laidian.service.VideoService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

/**
 * 接口缓存
 *
 * @author shixh
 */
@Slf4j
@Aspect
@Configuration
public class RequestCacheAspect {

    @Autowired
    private RedisClient redisClient;

    @Resource
    private RedisService redisService;

    @Resource
    private VideoService videoService;

    //缓存接口为视频查询列表
    private String videosList = "com.miguan.laidian.controller.VideoController.videosList";

    @Pointcut("@annotation(com.miguan.laidian.common.aop.RequestCache)")
    public void ServiceAspect() {

    }

    @Around("ServiceAspect()")
    public Object around(ProceedingJoinPoint joinPoint) throws Throwable {
        String cacheKey = getCacheKey(joinPoint);
        String value = redisService.get(RedisKeyConstant.REQUEST_CACHE_KEY + cacheKey);
        if (StringUtils.isNotEmpty(value)) {
            log.info("cache hit，key [{}]", cacheKey);
            ResultMap map = JSON.parseObject(value, ResultMap.class);
            if (cacheKey.contains(videosList) && map.getData() != null) {
                Map mapData = (Map) map.getData();
                List<Video> videoList = (List<Video>) mapData.get("data");
                if (CollectionUtils.isNotEmpty(videoList)) {
                    videoService.videoExposureSendToMQ(videoList);
                }
            }
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
