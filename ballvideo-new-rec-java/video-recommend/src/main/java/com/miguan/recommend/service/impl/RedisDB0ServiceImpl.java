package com.miguan.recommend.service.impl;

import com.miguan.recommend.service.RedisService;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

@Service("redisDB0Service")
public class RedisDB0ServiceImpl extends RedisService {

    @Resource(name = "recDB0Pool")
    private JedisPool jedisPool;

    @Override
    public JedisPool getJedisPool() {
        return jedisPool;
    }
}
