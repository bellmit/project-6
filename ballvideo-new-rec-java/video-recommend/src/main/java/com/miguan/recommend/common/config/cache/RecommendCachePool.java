package com.miguan.recommend.common.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.miguan.recommend.common.constants.ExistConstants;
import lombok.Data;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.caffeine.CaffeineCache;
import org.springframework.cache.support.SimpleCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Configuration
@EnableCaching
public class RecommendCachePool {
    private static volatile CacheManager cacheManager = null;

    public static CacheManager getRecommendCachePool() {
        if (cacheManager == null) {
            synchronized (RecommendCachePool.class) {
                if (cacheManager == null) {
                    RecommendCachePool recommendCachePool = new RecommendCachePool();
                    cacheManager = recommendCachePool.getCacheManager();
                }
            }
        }
        return cacheManager;
    }

    public Map<String, CaffeineConfig> cacheSpecs() {
        return new HashMap<String, CaffeineConfig>() {{
            put("default", new CaffeineConfig(10485760, 10000, ExistConstants.ten_minutes_seconds));
            put("video_stat", new CaffeineConfig(268435456, 500000, ExistConstants.one_and_a_half_minutes_seconds));
            put("video_hotspot", new CaffeineConfig(268435456, 200000, ExistConstants.one_and_a_half_minutes_seconds));
            put("incentive_video_hotspot", new CaffeineConfig(33554432, 10000, ExistConstants.five_minutes_seconds));
            //put("user_offline_label", new CaffeineConfig(33554432, 50000, ExistConstants.ten_minutes_seconds));
            put("user_raw_tags", new CaffeineConfig(33554432, 50000, ExistConstants.ten_minutes_seconds));
            //put("video_scenario_similar", new CaffeineConfig(33554432, 50000, 600));
            put("video_scenario", new CaffeineConfig(10485760, 50000, ExistConstants.one_day_seconds));
            put("scenario_num", new CaffeineConfig(10485760, 10000, ExistConstants.one_day_seconds));
            put("select_videos", new CaffeineConfig(10240, 1000, ExistConstants.one_and_a_half_minutes_seconds));
            put("embedding_video", new CaffeineConfig(10485760, 5000, ExistConstants.thirty_minutes_seconds));
            put("relevant_video_of_title", new CaffeineConfig(10485760, 5000, ExistConstants.thirty_minutes_seconds));
            put("video_embedding", new CaffeineConfig(409600, 2000, ExistConstants.one_day_seconds));
            put("user_choose_cat", new CaffeineConfig(409600, 50000, ExistConstants.one_day_seconds));
        }};
    }

    @Bean
    public CacheManager getCacheManager() {
        Map<String, CaffeineConfig> cacheSpecs = cacheSpecs();
        SimpleCaffeineCacheManager manager = new SimpleCaffeineCacheManager(cacheSpecs);
        if (!cacheSpecs.isEmpty()) {
            List<CaffeineCache> caches = cacheSpecs.entrySet().stream()
                    .map(entry -> buildCache(entry.getKey(), entry.getValue())).collect(Collectors.toList());
            manager.setCaches(caches);
        }
        return manager;
    }

    private CaffeineCache buildCache(String name, CaffeineConfig cfg) {
        final Caffeine<Object, Object> caffeineBuilder = Caffeine.newBuilder()
                .expireAfterWrite(cfg.getExpireAfterWrite(), TimeUnit.SECONDS).maximumSize(cfg.getMaximnumSize());
        return new CaffeineCache(name, caffeineBuilder.build());
    }

    private class SimpleCaffeineCacheManager extends SimpleCacheManager {

        private Map<String, CaffeineConfig> cacheSpecs;

        public SimpleCaffeineCacheManager(Map<String, CaffeineConfig> cacheSpecs) {
            super();
            this.cacheSpecs = cacheSpecs;
        }

        @Override
        protected Cache getMissingCache(String name) {
            CaffeineConfig cfg = this.cacheSpecs.get(name);
            if (!cacheSpecs.containsKey(name)) {
                cfg = this.cacheSpecs.get("default");
                if (cfg == null)
                    throw new IllegalArgumentException(String.format("Undefined [default] caffeine cache"));
            }
            return buildCache(name, cfg);
        }
    }

    @Data
    private class CaffeineConfig {
        private int initialCapacity; //初始缓存空间大小
        private long maximnumSize; //缓存的最大条数
        private long maximunmWeight; //缓存的最大权重
        private long expireAfterAccess; //最后一次写入或访问后经过固定的时间过期
        private long expireAfterWrite; //最后一次写入后经过固定的时间过期
        private long refreshAfterWrite; //写入后经过固定的时间刷新缓存
        private boolean weakKeys; // key为弱引用
        private boolean weakValues; // value为弱引用
        private boolean softValues; //value 为软引用
        private boolean recordStats; //统计功能

        public CaffeineConfig(int initialCapacity, long maximnumSize, long expireAfterWrite) {
            this.initialCapacity = initialCapacity;
            this.maximnumSize = maximnumSize;
            this.expireAfterWrite = expireAfterWrite;
        }
    }

}