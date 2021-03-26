package com.miguan.bigdata.common.config;

import io.rebloom.client.Client;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;

@Configuration
public class RecRedisConfig {

    @Resource
    private Environment environment;

    /**
     * 推荐接口缓存
     * @return
     */
    @Bean(name = "recDB0Pool")
    public JedisPool recDB0Pool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-idle")));
        jedisPoolConfig.setMinIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.min-idle")));
        jedisPoolConfig.setMaxTotal(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-active")));
        jedisPoolConfig.setMaxWaitMillis(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-wait")));
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, environment.getProperty("spring.redis.host"),
                Integer.valueOf(environment.getProperty("spring.redis.port")),
                Integer.valueOf(environment.getProperty("spring.redis.timeout")),
                environment.getProperty("spring.redis.password"), 0);
        return jedisPool;
    }

    /**
     * 离线推荐视频
     * @return
     */
    @Bean(name = "recDB1Pool")
    public JedisPool recDB1Pool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-idle")));
        jedisPoolConfig.setMinIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.min-idle")));
        jedisPoolConfig.setMaxTotal(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-active")));
        jedisPoolConfig.setMaxWaitMillis(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-wait")));
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, environment.getProperty("spring.redis.host"),
                Integer.valueOf(environment.getProperty("spring.redis.port")),
                Integer.valueOf(environment.getProperty("spring.redis.timeout")),
                environment.getProperty("spring.redis.password"), 1);
        return jedisPool;
    }

    /**
     * 特征
     * @return
     */
    @Bean(name = "recDB2Pool")
    public JedisPool recDB2Pool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-idle")));
        jedisPoolConfig.setMinIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.min-idle")));
        jedisPoolConfig.setMaxTotal(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-active")));
        jedisPoolConfig.setMaxWaitMillis(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-wait")));
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, environment.getProperty("spring.redis.host"),
                Integer.valueOf(environment.getProperty("spring.redis.port")),
                Integer.valueOf(environment.getProperty("spring.redis.timeout")),
                environment.getProperty("spring.redis.password"), 2);
        return jedisPool;
    }

    /**
     * bloom曝光过滤器、视频的相关计算信息
     * @return
     */
    @Bean(name = "recDB9Pool")
    public JedisPool recDB9Pool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-idle")));
        jedisPoolConfig.setMinIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.min-idle")));
        jedisPoolConfig.setMaxTotal(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-active")));
        jedisPoolConfig.setMaxWaitMillis(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-wait")));
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, environment.getProperty("spring.redis.host"),
                Integer.valueOf(environment.getProperty("spring.redis.port")),
                Integer.valueOf(environment.getProperty("spring.redis.timeout")),
                environment.getProperty("spring.redis.password"), 9);
        return jedisPool;
    }

    /**
     * bloom曝光过滤器
     * @return
     */
    @Bean(name = "recDB10Pool")
    public JedisPool recDB10Pool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-idle")));
        jedisPoolConfig.setMinIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.min-idle")));
        jedisPoolConfig.setMaxTotal(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-active")));
        jedisPoolConfig.setMaxWaitMillis(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-wait")));
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, environment.getProperty("spring.redis.host"),
                Integer.valueOf(environment.getProperty("spring.redis.port")),
                Integer.valueOf(environment.getProperty("spring.redis.timeout")),
                environment.getProperty("spring.redis.password"), 10);
        return jedisPool;
    }

    @Bean(name = "redis10BloomClient")
    public Client dbClient() {
        return new Client(recDB10Pool());
    }

}
