package com.miguan.advert.config.redis.config;

import com.miguan.advert.config.redis.util.CacheConstant;
import com.miguan.advert.config.redis.util.RedisKeyConstant;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.*;

/**
 * <p>
 *     Redis Cache配置
 * </p>
 * @author halo
 * @date 2018-11-08
 */
@Configuration
@EnableCaching
public class RedisCacheConfig extends CachingConfigurerSupport {

    @Bean
    @Override
    public KeyGenerator keyGenerator() {
        return (target, method, params) -> {
            StringBuffer redisKey = new StringBuffer();
            redisKey.append(target.getClass().getName()).append("-");
            redisKey.append(method.getName());
            if (params.length > 0) {
                redisKey.append("-").append(Arrays.deepToString(params));
            }
            return redisKey.toString();
        };
    }


    /*@Bean
    public CacheManager cacheManager(RedisConnectionFactory factory) {
        // 生成一个默认配置，通过config对象即可对缓存进行自定义配置
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig();
        // 设置缓存的默认过期时间，也是使用Duration设置
        // 过期时间5分钟
        config = config.entryTtl(Duration.ofMinutes(5));

        // 设置一个初始化的缓存空间set集合
        Set<String> cacheNames =  new HashSet<>();
        cacheNames.add("my-redis-cache1");
        cacheNames.add("my-redis-cache2");

        // 对每个缓存空间应用不同的配置
        Map<String, RedisCacheConfiguration> configMap = new HashMap<>(10);
        configMap.put("my-redis-cache1", config);
        configMap.put("my-redis-cache2", config.entryTtl(Duration.ofSeconds(120)));

        // 使用自定义的缓存配置初始化一个cacheManager
        RedisCacheManager cacheManager = RedisCacheManager.builder(factory)
                // 注意这两句的调用顺序，一定要先调用该方法设置初始化的缓存名，再初始化相关的配置
                .initialCacheNames(cacheNames)
                .withInitialCacheConfigurations(configMap)
                .build();

        return cacheManager;
    }*/

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return new RedisCacheManager(
                RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory),
                this.getConfigWithTtl(RedisKeyConstant.defalut_seconds), // 默认5分钟
                this.getOtherConfigWithTtl() //指定策略
        );
    }

    private Map<String, RedisCacheConfiguration> getOtherConfigWithTtl() {
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        //新老用户判断、任务类型图片路径，可以保存7天
        //redisCacheConfigurationMap.put(CacheConstant.NEW_USER_CHECK, this.getConfigWithTtl(RedisKeyConstant.NEW_USER_CHECK_SECONDS));
        //redisCacheConfigurationMap.put(CacheConstant.TASK_IMG_URL, this.getConfigWithTtl(RedisKeyConstant.NEW_USER_CHECK_SECONDS));
        return redisCacheConfigurationMap;
    }

    /**
     * 动态设置超时时间
     * @param seconds
     * @return
     */
    public RedisCacheConfiguration getConfigWithTtl(int seconds){
        RedisCacheConfiguration config = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(seconds))
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer()))
                .disableCachingNullValues();
        return config;
    }

    private RedisSerializer<String> keySerializer() {
        return new StringRedisSerializer();
    }

    private GenericJackson2JsonRedisSerializer valueSerializer(){
        return new GenericJackson2JsonRedisSerializer();
    }

}
