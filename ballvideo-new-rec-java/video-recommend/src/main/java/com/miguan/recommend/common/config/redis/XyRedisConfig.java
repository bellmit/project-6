package com.miguan.recommend.common.config.redis;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;

@Configuration
public class XyRedisConfig {

    @Resource
    private Environment environment;

    @Bean(name = "xyDB0Pool")
    public JedisPool xyDB0Pool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-idle")));
        jedisPoolConfig.setMinIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.min-idle")));
        jedisPoolConfig.setMaxTotal(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-active")));
        jedisPoolConfig.setMaxWaitMillis(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-wait")));
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, environment.getProperty("spring.redis.xy.host"),
                Integer.valueOf(environment.getProperty("spring.redis.xy.port")),
                Integer.valueOf(environment.getProperty("spring.redis.timeout")),
                environment.getProperty("spring.redis.xy.password"), 0);
        return jedisPool;
    }

    @Bean(name = "xyDB1Pool")
    public JedisPool xyDB1Pool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-idle")));
        jedisPoolConfig.setMinIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.min-idle")));
        jedisPoolConfig.setMaxTotal(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-active")));
        jedisPoolConfig.setMaxWaitMillis(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-wait")));
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, environment.getProperty("spring.redis.xy.host"),
                Integer.valueOf(environment.getProperty("spring.redis.xy.port")),
                Integer.valueOf(environment.getProperty("spring.redis.timeout")),
                environment.getProperty("spring.redis.xy.password"), 1);
        return jedisPool;
    }

    @Bean(name = "xyDB2Pool")
    public JedisPool xyDB2Pool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-idle")));
        jedisPoolConfig.setMinIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.min-idle")));
        jedisPoolConfig.setMaxTotal(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-active")));
        jedisPoolConfig.setMaxWaitMillis(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-wait")));
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, environment.getProperty("spring.redis.xy.host"),
                Integer.valueOf(environment.getProperty("spring.redis.xy.port")),
                Integer.valueOf(environment.getProperty("spring.redis.timeout")),
                environment.getProperty("spring.redis.xy.password"), 2);
        return jedisPool;
    }

    @Bean(name = "xyDB4Pool")
    public JedisPool xyDB4Pool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-idle")));
        jedisPoolConfig.setMinIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.min-idle")));
        jedisPoolConfig.setMaxTotal(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-active")));
        jedisPoolConfig.setMaxWaitMillis(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-wait")));
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, environment.getProperty("spring.redis.xy.host"),
                Integer.valueOf(environment.getProperty("spring.redis.xy.port")),
                Integer.valueOf(environment.getProperty("spring.redis.timeout")),
                environment.getProperty("spring.redis.xy.password"), 4);
        return jedisPool;
    }

    @Bean(name = "xyDB8Pool")
    public JedisPool xyDB8Pool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-idle")));
        jedisPoolConfig.setMinIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.min-idle")));
        jedisPoolConfig.setMaxTotal(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-active")));
        jedisPoolConfig.setMaxWaitMillis(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-wait")));
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, environment.getProperty("spring.redis.xy.host"),
                Integer.valueOf(environment.getProperty("spring.redis.xy.port")),
                Integer.valueOf(environment.getProperty("spring.redis.timeout")),
                environment.getProperty("spring.redis.xy.password"), 8);
        return jedisPool;
    }

}
