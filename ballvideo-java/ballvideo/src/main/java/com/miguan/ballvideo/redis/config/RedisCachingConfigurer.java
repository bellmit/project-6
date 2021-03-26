package com.miguan.ballvideo.redis.config;

import com.miguan.ballvideo.common.util.DateUtil;
import com.miguan.ballvideo.redis.util.CacheConstant;
import com.miguan.ballvideo.redis.util.RedisKeyConstant;
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
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * spring
 * @Author shixh
 * @Date 2019/11/13
 **/
@Configuration
@EnableCaching
public class RedisCachingConfigurer extends CachingConfigurerSupport {

    public final static Map<String,String> cacheKey_clear = new HashMap<String,String>();
    static {
        //哪些参数不需要作为KEY，这里设置
        cacheKey_clear.put(CacheConstant.QUERY_ADERT_LIST, "deviceId,excludeIds,userId,id");//广告查询过滤无用参数deviceId
        cacheKey_clear.put(CacheConstant.GET_ADVERSWITHCACHE, "marketChannelId,userId,deviceId,excludeIds,showedIds,num,queryNumber,otherCatIds,catId,id,weekRecommendPool");//广告查询过滤无用参数deviceId
        cacheKey_clear.put(CacheConstant.GET_ABTEXTADVERSBYRULE, "marketChannelId,userId,deviceId,excludeIds,showedIds,num,queryNumber,otherCatIds,catId,id,weekRecommendPool");//广告查询过滤无用参数deviceId

    }

    @Bean
    public KeyGenerator keyGenerator() {
        return (o, method, params) ->{
            String clearParams = getClearParams(method.getName());
            StringBuilder sb = new StringBuilder();
            sb.append(RedisKeyConstant.CACHE_ABLE_KEY);
            sb.append(method.getName()+":");
            for(Object param: params){
                if(param==null)continue;
                if(param instanceof Map){
                    Map m = (Map)param;
                    Iterator it = m.keySet().iterator();
                    while(it.hasNext()){
                        String key = it.next()+"";
                        if(m.get(key)!=null){
                            //哪些参数不需要作为KEY，这里过滤
                            if(clearParams!=null && clearParams.contains(key))continue;
                            sb.append(key).append("=").append(m.get(key)).append("&");
                        }
                    }
                }else{
                    sb.append(param.toString());
                }
            }
            return sb.toString();
        };
    }

    private String getClearParams(String name) {
        if(cacheKey_clear.containsKey(name)){
            return cacheKey_clear.get(name);
        }
        return null;
    }

    @Bean
    public RedisCacheManager cacheManager(RedisConnectionFactory connectionFactory) {
        return new RedisCacheManager(
                RedisCacheWriter.nonLockingRedisCacheWriter(connectionFactory),
                this.getConfigWithTtl(RedisKeyConstant.defalut_seconds), // 默认5分钟
                this.getOtherConfigWithTtl() //指定策略
        );
    }

    private Map<String, RedisCacheConfiguration> getOtherConfigWithTtl() {
        int seconds = DateUtil.getLastSecondsByDay(new Date(System.currentTimeMillis()));
        Map<String, RedisCacheConfiguration> redisCacheConfigurationMap = new HashMap<>();
        //新老用户判断，可以保存到当天24点 add shixh0306
        redisCacheConfigurationMap.put(CacheConstant.BURYINGPOINT_USER, this.getConfigWithTtl(seconds));
        //阶梯广告缓存时间为-1
        redisCacheConfigurationMap.put(CacheConstant.QUERY_LADDER_ADERT_LIST, this.getConfigWithTtl(-1));
        //根据视频Id查询视频分类保存4小时
        redisCacheConfigurationMap.put(CacheConstant.CATID_BY_VIDEOID, this.getConfigWithTtl(RedisKeyConstant.CAT_IDS_SECONDS));

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
