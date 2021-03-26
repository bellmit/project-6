package com.miguan.laidian.redis.config;

import com.miguan.laidian.redis.util.CacheConstant;
import com.miguan.laidian.redis.util.RedisKeyConstant;
import org.springframework.cache.annotation.CachingConfigurerSupport;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * @Author shixh
 * @Date 2019/11/13
 **/
@Configuration
@EnableCaching
public class RedisCachingConfigurer extends CachingConfigurerSupport {

    public final static Map<String, String> cacheKey_clear = new HashMap<String, String>();

    static {
        //哪些参数不需要作为KEY，这里设置
        cacheKey_clear.put(CacheConstant.QUERY_ADERT_LIST, "state");//广告查询过滤无用参数state
        cacheKey_clear.put(CacheConstant.GET_ADVERSWITHCACHE, "marketChannelId,userId,deviceId,excludeIds,showedIds,num,queryNumber,otherCatIds,catId,id,weekRecommendPool");//广告查询过滤无用参数
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return (o, method, params) -> {
            String clearParams = getClearParams(method.getName());
            StringBuilder sb = new StringBuilder();
            sb.append(RedisKeyConstant.CACHE_ABLE_KEY);
            sb.append(method.getName() + ":");
            for (Object param : params) {
                if (param instanceof Map) {
                    Map m = (Map) param;
                    Iterator it = m.keySet().iterator();
                    while (it.hasNext()) {
                        String key = it.next() + "";
                        if (m.get(key) != null) {
                            //哪些参数不需要作为KEY，这里过滤
                            if (clearParams != null && clearParams.contains(key)) continue;
                            sb.append(key).append("=").append(m.get(key));
                        }
                    }
                } else {
                    sb.append(param.toString());
                }
            }
            return sb.toString();
        };
    }

    private String getClearParams(String name) {
        if (cacheKey_clear.containsKey(name)) {
            return cacheKey_clear.get(name);
        }
        return null;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheManager redisCacheManager = RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(getConfigWithTtl(5 * 60))
                .transactionAware()
                .build();
        return redisCacheManager;
    }

/*    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return new RedisCacheManager(
                RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory),
                this.getConfigWithTtl(60), // 默认策略
                this.getOtherConfigWithTtl() //指定策略
        );
    }

    private Map<String, RedisCacheConfiguration> getOtherConfigWithTtl() {
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        redisCacheConfigurationMap.put("5minutes", this.getConfigWithTtl(5*60));
        redisCacheConfigurationMap.put("10minutes", this.getConfigWithTtl(10*60));
        redisCacheConfigurationMap.put("30minutes", this.getConfigWithTtl(30*60));
        return redisCacheConfigurationMap;
    }*/

    /**
     * 动态设置超时时间
     *
     * @param seconds
     * @return
     */
    public RedisCacheConfiguration getConfigWithTtl(int seconds) {
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

    private GenericJackson2JsonRedisSerializer valueSerializer() {
        return new GenericJackson2JsonRedisSerializer();
    }

}
