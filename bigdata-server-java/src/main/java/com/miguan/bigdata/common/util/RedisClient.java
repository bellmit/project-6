package com.miguan.bigdata.common.util;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * redis封装类
 *
 * @author xujinbang
 * @date 2019/3/28
 */
@Component("redisClient")
public class RedisClient {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 设置redis值
     *
     * @param key
     * @param value
     */
    public void set(String key, String value) {
        redisTemplate.opsForValue().set(key, value);
    }

    /**
     * 设置redis值，有过期时间
     *
     * @param key
     * @param value
     * @param expireTime
     */
    public void set(String key, String value, long expireTime) {
        redisTemplate.opsForValue().set(key, value, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 如果键不存在则新增,存在则不改变已经有的值
     * @param key
     * @param value
     * @return
     */
    public Boolean setIfAbsent(String key, String value) {
        return redisTemplate.opsForValue().setIfAbsent(key, value);
    }

    /**
     * 如果键不存在则新增,存在则不改变已经有的值,有过期时间(秒)
     * @param key
     * @param value
     * @param expireTime
     * @return
     */
    public Boolean setIfAbsent(String key, String value, long expireTime) {
        return redisTemplate.opsForValue().setIfAbsent(key, value, expireTime, TimeUnit.SECONDS);
    }

    /**
     * 获取redis值
     *
     * @param key
     * @return
     */
    public String get(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value == null ? "" : (String) value;
    }

    /**
     * 判断键值是否存在
     *
     * @param key
     * @return
     */
    public boolean hasKey(String key) {
        final Boolean hasKey = redisTemplate.hasKey(key);
        return hasKey == null ? false : hasKey;
    }

    /**
     * 删除key
     *
     * @auth zhicong.lin
     * @date 2019/4/11
     */
    public void delete(String key) {
        this.redisTemplate.delete(key);
    }

    /**
     * redis自增操作
     * @param key
     * @return
     */
    public Long incr(String key, Long count, Long date) {
        RedisAtomicLong entityIdCounter;
        if(count != null) {
            entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory(),count);
        } else {
            entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        }
        if(date != null) {
            entityIdCounter.expire(date,TimeUnit.SECONDS);
        }
        Long increment = entityIdCounter.incrementAndGet();
        return increment;
    }

    /**
     * 判断redis自增操作是否第一次
     * @param key
     * @return
     */
    public int check(String key) {
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory());
        return entityIdCounter.intValue();
    }
}
