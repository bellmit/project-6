package com.miguan.recommend.task;

import com.miguan.recommend.common.constants.ExistConstants;
import com.miguan.recommend.common.constants.RedisCountConstant;
import com.miguan.recommend.common.constants.RedisLockConstant;
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
    @Resource(name = "recDB9Pool")
    JedisPool recDB9Pool;
    @Autowired
    @Qualifier("redisBloomClient")
    private Client client;
    @Value("${spring.redis.bloom.init-capacity}")
    private long initCapacity;
    @Value("${spring.redis.bloom.error-rate}")
    private double errorRate;

    /**
     * 每月1/15号的00:00:01执行
     */
    @Scheduled(cron = "1 0 0 1,15 * ?")
    public void changeBloomFilter() {

//        RedisLock redisLock = new RedisLock(RedisLockConstant.BLOOM_FILTER_FOR_DAY_LOCK, 60000);
//        if (redisLock.lock()) {
        long incr = recDB10Pool.getResource().incr(RedisLockConstant.BLOOM_FILTER_FOR_DAY_LOCK);
        if (incr == 1) {
            recDB10Pool.getResource().expire(RedisLockConstant.BLOOM_FILTER_FOR_DAY_LOCK, ExistConstants.five_minutes_seconds);
            try {
                // 9库
                //删除上上个月的布隆过滤器
                client.delete(RedisCountConstant.bloom_p);
                //将当月的布隆过滤器key改成上个月的key
                try (Jedis jedis = recDB9Pool.getResource()) {
                    jedis.rename(RedisCountConstant.bloom_c, RedisCountConstant.bloom_p);
                }
                try {
                    client.createFilter(RedisCountConstant.bloom_c, initCapacity, errorRate);
                } catch (Exception e) {
                }

                // 10库
                //删除上上个月的布隆过滤器
                redis10BloomClient.delete(RedisCountConstant.bloom_p);
                //将当月的布隆过滤器key改成上个月的key
                try (Jedis jedis = recDB10Pool.getResource()) {
                    jedis.rename(RedisCountConstant.bloom_c, RedisCountConstant.bloom_p);
                }
                try {
                    redis10BloomClient.createFilter(RedisCountConstant.bloom_c, initCapacity, errorRate);
                } catch (Exception e) {
                }
                log.warn("推荐0.1已调整了布隆过滤器");
            } catch (Exception e) {
                log.error("推荐0.1调整布隆过滤器失败", e);
            } finally {
                //redisLock.unlock();
            }
        }
    }

    /**
     * 每天的00:00:01执行
     */
    @Scheduled(cron = "1 0 0 * * ?")
    public void changeLowerBloomFilter() {
        RedisLock redisLock = new RedisLock(RedisLockConstant.BLOOM_FILTER_FOR_DAY_LOCK, 60000);
        if (redisLock.lock()) {
            try {
                String bn1 = "lowerbgsUpBloomC";
                String bn2 = "lowerbgsUpBloomP";
                //删除上上个月的布隆过滤器
                client.delete("bn2");
                //将当月的布隆过滤器key改成上个月的key
                try (Jedis jedis = recDB9Pool.getResource()) {
                    jedis.rename(bn1, bn2);
                }
                try {
                    client.createFilter(bn1, 50000000, errorRate);
                } catch (Exception e) {
                }
                log.warn("推荐0.0已调整了布隆过滤器");
            } catch (Exception e) {
                log.error("推荐0.0调整布隆过滤器失败", e);
            } finally {
                redisLock.unlock();
            }
        }
    }
}
