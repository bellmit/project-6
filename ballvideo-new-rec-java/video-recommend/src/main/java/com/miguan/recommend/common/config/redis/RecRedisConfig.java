package com.miguan.recommend.common.config.redis;

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
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, environment.getProperty("spring.redis.rec.host"),
                Integer.valueOf(environment.getProperty("spring.redis.rec.port")),
                Integer.valueOf(environment.getProperty("spring.redis.timeout")),
                environment.getProperty("spring.redis.rec.password"), 0);
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
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, environment.getProperty("spring.redis.rec.host"),
                Integer.valueOf(environment.getProperty("spring.redis.rec.port")),
                Integer.valueOf(environment.getProperty("spring.redis.timeout")),
                environment.getProperty("spring.redis.rec.password"), 1);
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
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, environment.getProperty("spring.redis.rec.host"),
                Integer.valueOf(environment.getProperty("spring.redis.rec.port")),
                Integer.valueOf(environment.getProperty("spring.redis.timeout")),
                environment.getProperty("spring.redis.rec.password"), 2);
        return jedisPool;
    }

    @Bean(name = "recDB3Pool")
    public JedisPool recDB3Pool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-idle")));
        jedisPoolConfig.setMinIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.min-idle")));
        jedisPoolConfig.setMaxTotal(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-active")));
        jedisPoolConfig.setMaxWaitMillis(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-wait")));
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, environment.getProperty("spring.redis.rec.host"),
                Integer.valueOf(environment.getProperty("spring.redis.rec.port")),
                Integer.valueOf(environment.getProperty("spring.redis.timeout")),
                environment.getProperty("spring.redis.rec.password"), 3);
        return jedisPool;
    }

    @Bean(name = "recDB4Pool")
    public JedisPool recDB4Pool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-idle")));
        jedisPoolConfig.setMinIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.min-idle")));
        jedisPoolConfig.setMaxTotal(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-active")));
        jedisPoolConfig.setMaxWaitMillis(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-wait")));
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, environment.getProperty("spring.redis.rec.host"),
                Integer.valueOf(environment.getProperty("spring.redis.rec.port")),
                Integer.valueOf(environment.getProperty("spring.redis.timeout")),
                environment.getProperty("spring.redis.rec.password"), 4);
        return jedisPool;
    }

    /**
     * 视频的相关计算信息
     * @return
     */
    @Bean(name = "recDB9Pool")
    public JedisPool recDB9Pool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-idle")));
        jedisPoolConfig.setMinIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.min-idle")));
        jedisPoolConfig.setMaxTotal(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-active")));
        jedisPoolConfig.setMaxWaitMillis(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-wait")));
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, environment.getProperty("spring.redis.rec.host"),
                Integer.valueOf(environment.getProperty("spring.redis.rec.port")),
                Integer.valueOf(environment.getProperty("spring.redis.timeout")),
                environment.getProperty("spring.redis.rec.password"), 9);
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
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, environment.getProperty("spring.redis.rec.host"),
                Integer.valueOf(environment.getProperty("spring.redis.rec.port")),
                Integer.valueOf(environment.getProperty("spring.redis.timeout")),
                environment.getProperty("spring.redis.rec.password"), 10);
        return jedisPool;
    }

    /**
     * 用户的相关计算信息
     * @return
     */
    @Bean(name = "recDB11Pool")
    public JedisPool recDB11Pool() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        jedisPoolConfig.setMaxIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-idle")));
        jedisPoolConfig.setMinIdle(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.min-idle")));
        jedisPoolConfig.setMaxTotal(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-active")));
        jedisPoolConfig.setMaxWaitMillis(Integer.valueOf(environment.getProperty("spring.redis.jedis.pool.max-wait")));
        JedisPool jedisPool = new JedisPool(jedisPoolConfig, environment.getProperty("spring.redis.rec.host"),
                Integer.valueOf(environment.getProperty("spring.redis.rec.port")),
                Integer.valueOf(environment.getProperty("spring.redis.timeout")),
                environment.getProperty("spring.redis.rec.password"), 11);
        return jedisPool;
    }

    @Bean(name = "redisBloomClient")
    public Client dbClient() {
        return new Client(recDB9Pool());
    }

    @Bean(name = "redis10BloomClient")
    public Client redis10BloomClient() {
        return new Client(recDB10Pool());
    }
}
