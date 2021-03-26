package com.miguan.ballvideo.redis.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConfiguration;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import redis.clients.jedis.JedisPoolConfig;

import javax.annotation.Resource;
import java.time.Duration;

/**
 * 根据不同数据源配置不同bean
 * @Author shixh
 * @Date 2019/9/20
 **/
@Configuration
public class RedisDataBaseConfig {
    @Resource
    private ShangbaoRedisProperties redisProperties;

    @Bean
    public JedisPoolConfig getJedisPoolConfig() {
        JedisPoolConfig jedisPoolConfig = new JedisPoolConfig();
        return jedisPoolConfig;
    }

    /**
     * 上报redis数据源配置
     * @Author shixh
     * */
    @Bean(name = "shangbaoRedisTemplate")
    public RedisTemplate getRedisDBTemplate() {
        LettuceConnectionFactory connectionFactory =
        createLettuceConnectionFactory(
                redisProperties.getShangbaoDatabase(),
                redisProperties.getShangbaoHost(),
                redisProperties.getShangbaoPort(),
                redisProperties.getShangbaoPassword(),
                redisProperties.getShangbaoTimeOut());
        RedisTemplate redisTemplate = new RedisTemplate();
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
  }

    /**
     * spring 2.0以上使用LettuceConnectionFactory
     * @Author shixh
     * */
    private LettuceConnectionFactory createLettuceConnectionFactory(
        int dbIndex, String hostName, int port, String password, long timeOut) {
        RedisConfiguration redisConfiguration = new RedisStandaloneConfiguration(hostName, port);
        ((RedisStandaloneConfiguration) redisConfiguration).setDatabase(dbIndex);
        ((RedisStandaloneConfiguration) redisConfiguration).setPassword(password);
        GenericObjectPoolConfig genericObjectPoolConfig = new GenericObjectPoolConfig();
        // redis客户端配置
        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder =
        LettucePoolingClientConfiguration.builder().commandTimeout(Duration.ofMillis(timeOut));
        builder.shutdownTimeout(Duration.ofMillis(timeOut + 5000));
        builder.poolConfig(genericObjectPoolConfig);
        LettuceClientConfiguration lettuceClientConfiguration = builder.build();
        // 根据配置和客户端配置创建连接
        LettuceConnectionFactory lettuceConnectionFactory =
            new LettuceConnectionFactory(redisConfiguration, lettuceClientConfiguration);
        lettuceConnectionFactory.afterPropertiesSet();
        return lettuceConnectionFactory;
    }
}
