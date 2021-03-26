package com.miguan.recommend.service.xy.impl;

import com.miguan.recommend.service.RedisService;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

@Service("xyRedisDB8Service")
public class RedisDB8ServiceImpl extends RedisService {

    @Resource(name = "xyDB8Pool")
    private JedisPool jedisPool;


    @Override
    public JedisPool getJedisPool() {
        return jedisPool;
    }
}
