package com.miguan.recommend.service.xy.impl;

import com.alibaba.fastjson.JSON;
import com.miguan.recommend.common.util.SerializeUtil;
import com.miguan.recommend.service.RedisService;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("xyRedisDB0Service")
public class RedisDB0ServiceImpl extends RedisService {

    @Resource(name = "xyDB0Pool")
    private JedisPool jedisPool;


    @Override
    public JedisPool getJedisPool() {
        return jedisPool;
    }
}
