package com.miguan.ballvideo.service.recommendlower;

import com.google.common.collect.Lists;
import io.rebloom.client.Client;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import java.util.List;

/**
 * @author zhongli
 * @date 2020-08-12
 *
 */
@Service
@Slf4j
public class LowerBloomFilterService {
    @Autowired
    @Qualifier("redisBloomClient")
    private Client client;
    @Value("${spring.redis.bloom.error-rate}")
    private double errorRate;
    @Resource(name = "recDB9Pool")
    private JedisPool recDB9Pool;

    private final String bn1 = "lowerbgsUpBloomC";
    private final String bn2 = "lowerbgsUpBloomP";

    @PostConstruct
    public void init() {
        try (Jedis con = recDB9Pool.getResource()) {
            if (!con.exists(bn1)) {
                client.createFilter(bn1, 50000000, errorRate);
            }
            if (!con.exists(bn2)) {
                client.createFilter(bn2, 50000000, errorRate);
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
        boolean[] b = client.existsMulti(bn1, keys);
        //存储视频id
        List<String> bv = Lists.newArrayList();
        //存储视频id进来时的数组索引位置
        List<Integer> bvI = Lists.newArrayList();
        for (int i = 0; i < b.length; i++) {
            boolean btmp = b[i];
            //如果在第一个布隆过滤器中不存在，则暂存至列表中给下个布隆过滤器
            if (!btmp) {
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
                if (bv.size() >= getSize) {
                    break;
                }
            }
        }
        return bv;
    }

    public boolean putAll(String uuid, List<String> videoId) {
        String[] keys = videoId.stream().map(e -> getKey(uuid, e)).toArray(String[]::new);
        client.addMulti(bn1, keys);
        return true;
    }

    private String getKey(String uuid, String videoId) {
        return uuid.concat(videoId);
    }

}
