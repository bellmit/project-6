package com.miguan.recommend.service.impl;

import com.miguan.recommend.service.RedisService;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

@Service("redisDB3Service")
public class RedisDB3ServiceImpl extends RedisService {

    @Resource(name = "recDB3Pool")
    private JedisPool jedisPool;

    @Override
    public JedisPool getJedisPool() {
        return null;
    }
}
