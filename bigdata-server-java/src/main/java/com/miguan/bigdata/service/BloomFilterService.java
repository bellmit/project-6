package com.miguan.bigdata.service;

import com.miguan.bigdata.common.constant.RedisCountConstant;
import com.miguan.bigdata.entity.dw.UserInfo;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author zhongli
 * @date 2020-08-12
 */
@Service
@Slf4j
public class BloomFilterService {

    @Resource(name = "recDB10Pool")
    private JedisPool recDB10Pool;
    @Autowired
    @Qualifier("redis10BloomClient")
    private Client client;
    @Value("${spring.redis.bloom.init-capacity}")
    private long initCapacity;
    @Value("${spring.redis.bloom.error-rate}")
    private double errorRate;

    public final static String exists = "exists";
    public final static String not_exists = "not_exists";

    @PostConstruct
    public void init() {
        try (Jedis con = recDB10Pool.getResource()) {
            if (!con.exists(RedisCountConstant.bloom_npush_c)) {
                client.createFilter(RedisCountConstant.bloom_npush_c, initCapacity, errorRate);
            }
            if (!con.exists(RedisCountConstant.bloom_npush_p)) {
                client.createFilter(RedisCountConstant.bloom_npush_p, initCapacity, errorRate);
            }
        } catch (Exception e) {
            log.warn("初始化npush redis布隆过滤器异常：{}", e.getMessage());
        }
    }

    public Map<String, List<String>> existsMuilByDistinctId(List<String> distinctIdList, String partOfKeys) {
        String[] keys = new String[distinctIdList.size()];
        for (int i = 0; i < distinctIdList.size(); i++) {
            keys[i] = getKey(distinctIdList.get(i), partOfKeys);
        }

        List<String> noExistsKeys = new ArrayList<String>();
        List<String> noExistsList = new ArrayList<String>();
        List<String> existsList = new ArrayList<String>();

        //校验第一个布隆过滤器
        boolean isPass = false;
        boolean[] b = null;
        try {
            b = client.existsMulti(RedisCountConstant.bloom_npush_c, keys);
        } catch (Exception e) {
            log.error("布隆过滤器校验异常");
            b = new boolean[keys.length];
            isPass = true;
        }

        for (int i = 0; i < b.length; i++) {
            if (isPass) {
                noExistsKeys.add(keys[i]);
                noExistsList.add(distinctIdList.get(i));
            } else {
                if (b[i]) {
                    existsList.add(distinctIdList.get(i));
                } else {
                    noExistsKeys.add(keys[i]);
                    noExistsList.add(distinctIdList.get(i));
                }
            }
        }
        keys = null;

        if (!noExistsKeys.isEmpty()) {
            boolean[] b2 = client.existsMulti(RedisCountConstant.bloom_npush_p, noExistsKeys.toArray(new String[noExistsKeys.size()]));
            List<Integer> needRemove4NoExistsList = new ArrayList<Integer>();
            for (int i = 0; i < b2.length; i++) {
                if (b2[i]) {
                    needRemove4NoExistsList.add(i);
                    existsList.add(noExistsList.get(i));
                }
            }

            needRemove4NoExistsList.forEach(e -> {
                noExistsList.remove(e);
            });
        }
        noExistsKeys.clear();

        Map<String, List<String>> bloomResultMap = new HashMap<String, List<String>>();
        bloomResultMap.put(exists, existsList);
        bloomResultMap.put(not_exists, noExistsList);
        return bloomResultMap;
    }

    public String getKey(String uuid, String videoId) {
        return uuid.concat(videoId);
    }

}
