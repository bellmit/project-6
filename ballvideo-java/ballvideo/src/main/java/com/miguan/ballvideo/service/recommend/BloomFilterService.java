package com.miguan.ballvideo.service.recommend;

import com.google.common.collect.Lists;
import io.rebloom.client.Client;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author zhongli
 * @date 2020-08-12
 *
 */
@Service
@Slf4j
public class BloomFilterService {
    @Autowired
    @Qualifier("redisBloomClient")
    private Client client;
    @Value("${spring.redis.bloom.init-capacity}")
    private long initCapacity;
    @Value("${spring.redis.bloom.error-rate}")
    private double errorRate;
    @Resource(name = "recDB9Pool")
    private JedisPool recDB9Pool;
    @Autowired
    private RecommendDisruptorService recommendDisruptorService;

    private final String bn1 = "bgsUpBloomCA";
    private final String bn2 = "bgsUpBloomP1";
    private final String KEY_PREFIX = "rec:";

    @PostConstruct
    public void init() {
        try (Jedis con = recDB9Pool.getResource()) {
            if (!con.exists(bn1)) {
                client.createFilter(bn1, initCapacity, errorRate);
            }
            if (!con.exists(bn2)) {
                client.createFilter(bn2, initCapacity, errorRate);
            }
        } catch (Exception e) {
            log.warn("初始化redis布隆过滤器异常：{}", e.getMessage());
        }
    }

    public List<String> containMuil(int getSize, String uuid, List<String> vlist) {
        String[] keys = new String[vlist.size()];
        for (int i = 0; i < vlist.size(); i++) {
            String v = vlist.get(i);
            String key = getKey(uuid, v);
            keys[i] = key;
        }
        //校验第一个布隆过滤器
        boolean[] b = null;
        boolean isPass = false;
        try{
            b = client.existsMulti(bn1, keys);
        } catch (Exception e){
            log.error("布隆过滤器校验异常");
            b = new boolean[keys.length];
            isPass = true;
        }

        //存储视频id
        List<String> bv = Lists.newArrayList();
        //存储视频id进来时的数组索引位置
        List<Integer> bvI = Lists.newArrayList();
        for (int i = 0; i < b.length; i++) {
            //如果在第一个布隆过滤器中不存在，则暂存至列表中给下个布隆过滤器
            if (isPass || !b[i]) {
                bv.add(keys[i]);
                bvI.add(i);
            }
        }
        //如果视频在第一个过滤器中都过滤了则跳过，否则继续判断下一个布隆过滤器
        if (!bv.isEmpty()) {
            boolean[] b2 = client.existsMulti(bn2, bv.toArray(new String[bv.size()]));
            bv.clear();
            for (int i = 0; i < b2.length; i++) {
                boolean btmp = b2[i];
                if (!btmp) {
                    int index = bvI.get(i);
                    bv.add(vlist.get(index));
                }
            }

        }
        //存储视频id
        List<String> rdata = Lists.newArrayList();
        //过滤30秒缓存
        if (!bv.isEmpty()) {
            try (Jedis con = recDB9Pool.getResource()) {
                keys = bv.stream().map(e -> cacheKey(uuid, e)).toArray(String[]::new);
                List<String> ktmp = con.mget(keys);
                for (int i = 0; i < keys.length; i++) {
                    String k = ktmp.get(i);
                    //存在缓存中则过滤
                    if (k != null) {
                        continue;
                    }
                    rdata.add(bv.get(i));
                    if (getSize <= rdata.size()) {
                        break;
                    }
                }
            }
        }
        return rdata;
    }

    public boolean putAll(String uuid, List<String> videoId) {
        if(CollectionUtils.isEmpty(videoId)){
            return false;
        }
        List<String> keys = Lists.newArrayList();
        String[] list = videoId.stream().flatMap(e -> {
            String key = cacheKey(uuid, e);
            keys.add(key);
            return Stream.of(key, "1");
        }).toArray(String[]::new);
        try (Jedis con = recDB9Pool.getResource()) {
            con.mset(list);
        }
        recommendDisruptorService.pushEvent(1, uuid, keys);
        return true;
    }

    private String getKey(String uuid, String videoId) {
        return uuid.concat(videoId);
    }

    private String cacheKey(String uuid, String videoId) {
        return KEY_PREFIX.concat(DigestUtils.md5Hex(uuid.concat(videoId)));
    }
}
