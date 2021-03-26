package com.miguan.bigdata.task;

import com.miguan.bigdata.common.constant.RedisCountConstant;
import com.miguan.bigdata.common.constant.RedisKeyConstants;
import io.rebloom.client.Client;
import lombok.extern.slf4j.Slf4j;
import org.cgcg.redis.core.entity.RedisLock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.Resource;

/**
 * @Description 到了下个月，需要将上个月的布隆过滤器删除，将当月的布隆过滤器key改成上个月的key，生成一个当月的布隆过滤器。
 * @Author zhangbinglin
 * @Date 2020/8/14 16:37
 **/
@Slf4j
@Component
public class BloomFilterTask {

    @Resource(name = "recDB10Pool")
    JedisPool recDB10Pool;
    @Autowired
    @Qualifier("redis10BloomClient")
    private Client redis10BloomClient;

    @Value("${spring.redis.bloom.init-capacity}")
    private long initCapacity;
    @Value("${spring.redis.bloom.error-rate}")
    private double errorRate;

    /**
     * 每月1/15号的00:00:01执行
     */
    @Scheduled(cron = "1 0 0 1,15 * ?")
    public void changeBloomFilter() {
        RedisLock redisLock = new RedisLock(RedisKeyConstants.npush_bloom_init_lock, 60000);
        if (redisLock.lock()) {
            try {
                //删除上上个月的布隆过滤器
                redis10BloomClient.delete(RedisCountConstant.bloom_npush_p);
                //将当月的布隆过滤器key改成上个月的key
                try (Jedis jedis = recDB10Pool.getResource()) {
                    jedis.rename(RedisCountConstant.bloom_npush_c, RedisCountConstant.bloom_npush_p);
                }
                try {
                    redis10BloomClient.createFilter(RedisCountConstant.bloom_npush_c, initCapacity, errorRate);
                } catch (Exception e) {
                }
                log.warn("Npush 已调整了布隆过滤器");
            } catch (Exception e) {
                log.error("Npush 调整布隆过滤器失败", e);
            } finally {
                redisLock.unlock();
            }
        }
    }
}
