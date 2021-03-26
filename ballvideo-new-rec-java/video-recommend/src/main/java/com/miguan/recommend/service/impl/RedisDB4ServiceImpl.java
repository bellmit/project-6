package com.miguan.recommend.service.impl;

import com.miguan.recommend.service.RedisService;
import org.springframework.stereotype.Service;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

@Service("redisDB4Service")
public class RedisDB4ServiceImpl extends RedisService {

    @Resource(name = "recDB4Pool")
    private JedisPool jedisPool;

    @Override
    public JedisPool getJedisPool() {
        return null;
    }
}
