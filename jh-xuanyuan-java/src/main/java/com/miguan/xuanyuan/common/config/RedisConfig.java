package com.miguan.xuanyuan.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;

@Configuration
public class RedisConfig {

    @Resource
    private Environment environment;

    /**
     * 默认redis数据源
     * @return
     */
    @Bean(name = "defaultPool")
    public JedisPool defaultDB0Pool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-idle")));
        jedisPoolConfig.setMinIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.min-idle")));
        jedisPoolConfig.setMaxTotal(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-active")));
        jedisPoolConfig.setMaxWaitMillis(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-wait")));
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, environment.getProperty("spring.redis.host"),
                Integer.valueOf(environment.getProperty("spring.redis.port")),
                Integer.valueOf(environment.getProperty("spring.redis.timeout")),
                environment.getProperty("spring.redis.password"), Integer.valueOf(environment.getProperty("spring.redis.database")));
        return jedisPool;
    }


    @Bean(name = "authLoginRedisPool")
    public JedisPool authLoginRedisPool(){
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(Integer.valueOf(environment.getProperty("ehr.login.redis.pool.max-idle")));
        jedisPoolConfig.setMinIdle(Integer.valueOf(environment.getProperty("ehr.login.redis.pool.min-idle")));
        jedisPoolConfig.setMaxTotal(Integer.valueOf(environment.getProperty("ehr.login.redis.pool.max-active")));
        jedisPoolConfig.setMaxWaitMillis(Integer.valueOf(environment.getProperty("ehr.login.redis.pool.max-wait")));
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, environment.getProperty("ehr.login.redis.host"),
                Integer.valueOf(environment.getProperty("ehr.login.redis.port")), Integer.valueOf(environment.getProperty("ehr.login.redis.timeout")),environment.getProperty("ehr.login.redis.password"),environment.getProperty("ehr.login.redis.database"));
        return jedisPool;
    }
}
