package com.miguan.ballvideo.common.util.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.support.atomic.RedisAtomicLong;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * redis封装类
 * @author xujinbang
 * @date 2019/3/28
 */
@Component("redisClient")
public class RedisClient {

    @Resource
    private RedisTemplate redisTemplate;

    /**
     * 设置redis值
     * @param key
     * @param value
     */
    public void set(String key,String value) {
        redisTemplate.opsForValue().set(key,value);
    }

    /**
     * 设置redis值，有过期时间
     * @param key
     * @param value
     * @param expireTime
     */
    public void set(String key,String value,long expireTime) {
        redisTemplate.opsForValue().set(key,value,expireTime, TimeUnit.SECONDS);
    }

    /**
     * 获取redis值
     * @param key
     * @return
     */
    public String get(String key) {
        Object value = redisTemplate.opsForValue().get(key);
        return value == null ? "" : (String)value;
    }

    /**
     * 判断键值是否存在
     * @param key
     * @return
     */
    public boolean hasKey(String key) {
        return redisTemplate.hasKey(key);
    }

    /**
     * 删除key
     * @auth zhicong.lin
     * @date 2019/4/11
     */
    public void delete(String key) {
        this.redisTemplate.delete(key);
    }

    public Map getHashMap(final String key) {
        Map value = redisTemplate.boundHashOps(key).entries();
        return value;
    }

    public Map setHashMap(final String key, final Map newMap) {
        redisTemplate.opsForHash().putAll(key, newMap);
        return newMap;
    }

    /**
     * redis自增操作
     * @param key
     * @return
     */
    public Long incr(String key,Long watchCount) {
        RedisAtomicLong entityIdCounter = new RedisAtomicLong(key, redisTemplate.getConnectionFactory(),watchCount);
        Long increment = entityIdCounter.incrementAndGet();
        return increment;
    }
}
