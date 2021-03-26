package com.miguan.advert.common.util.redis;

import org.springframework.data.redis.core.RedisTemplate;

import java.util.concurrent.TimeUnit;

/**
 * redis分布式锁的实现
 * @author xujinbang
 * @date 2019/3/13
 */
public class RedisLock {

    private RedisTemplate redisTemplate;

    /**
     * 锁的后缀
     */
    private final String LOCK_SUFFIX = "redis_lock";

    /**
     * 锁的key
     */
    private String lockKey;

    /**
     * 锁超时时间，防止线程在入锁以后，防止阻塞后面的线程无法获取锁
     */
    private int expireMsecs = 10;

    /**
     * 线程获取锁的等待时间
     */
    private int timeoutMsecs = 10 * 1000;

    /**
     * 是否锁定标志
     */
    private volatile boolean locked = false;

    /**
     * 构造器
     * @param redisTemplate
     * @param lockKey 锁的key
     */
    public RedisLock(RedisTemplate redisTemplate, String lockKey) {
        this.redisTemplate = redisTemplate;
        this.lockKey = lockKey + LOCK_SUFFIX;
    }

    /**
     * 构造器
     * @param redisTemplate
     * @param lockKey 锁的key
     * @param timeoutMsecs 获取锁的超时时间
     */
    public RedisLock(RedisTemplate redisTemplate, String lockKey, int timeoutMsecs) {
        this(redisTemplate, lockKey);
        this.timeoutMsecs = timeoutMsecs;
    }

    /**
     * 构造器
     * @param redisTemplate
     * @param lockKey 锁的key
     * @param timeoutMsecs 获取锁的超时时间
     * @param expireMsecs 锁的有效期
     */
    public RedisLock(RedisTemplate redisTemplate, String lockKey, int timeoutMsecs, int expireMsecs) {
        this(redisTemplate, lockKey, timeoutMsecs);
        this.expireMsecs = expireMsecs;
    }

    public String getLockKey() {
        return lockKey;
    }

    /**
     * 封装和jedis方法
     * @param key
     * @return
     */
    private String get(final String key) {
        Object obj = redisTemplate.opsForValue().get(key);
        return obj != null ? obj.toString() : null;
    }

    /**
     * 封装和jedis方法
     * @param key
     * @param value
     * @return
     */
    private boolean setNX(final String key, final String value) {
        return redisTemplate.opsForValue().setIfAbsent(key,value);
    }

    /**
     * 封装和jedis方法
     * @param key
     * @param value
     * @return
     */
    private String getSet(final String key, final String value) {
        Object obj = redisTemplate.opsForValue().getAndSet(key,value);
        return obj != null ? (String) obj : null;
    }

    /**
     * 获取锁
     * @author xy.chen
     * @return 获取锁成功返回ture，超时返回false
     * @throws InterruptedException
     */
    public synchronized boolean lock() throws InterruptedException {
        if (!redisTemplate.hasKey(lockKey)) {
            this.set(lockKey, lockKey, expireMsecs);
            return true;
        }
        return false;
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
     * 释放获取到的锁
     */
    public synchronized void unlock() {
        if (locked) {
            redisTemplate.delete(lockKey);
            locked = false;
        }
    }
}
