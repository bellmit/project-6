package com.miguan.recommend.service;

import com.google.common.collect.Lists;
import com.miguan.recommend.common.constants.RedisCountConstant;
import com.miguan.recommend.entity.mongo.VideoHotspotVo;
import com.miguan.recommend.service.recommend.RecommendDisruptorService;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.springframework.util.CollectionUtils.isEmpty;

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
    private Client redis10BloomClient;

    @Value("${spring.redis.bloom.init-capacity}")
    private long initCapacity;
    @Value("${spring.redis.bloom.error-rate}")
    private double errorRate;
    @Autowired
    private RecommendDisruptorService recommendDisruptorService;

    public static final String KEY_PREFIX = "rec_bloom_cache:";

    @PostConstruct
    public void init() {
        try (Jedis con = recDB10Pool.getResource()) {
            if (!con.exists(RedisCountConstant.bloom_c)) {
                redis10BloomClient.createFilter(RedisCountConstant.bloom_c, initCapacity, errorRate);
            }
            if (!con.exists(RedisCountConstant.bloom_p)) {
                redis10BloomClient.createFilter(RedisCountConstant.bloom_p, initCapacity, errorRate);
            }
        } catch (Exception e) {
            log.warn("初始化redis布隆过滤器异常：{}", e.getMessage());
        }
    }

    public List<VideoHotspotVo> containMuilEntitySplit(String userTag, List<VideoHotspotVo> sourceList, int getNum){
        List<String> queryList = sourceList.stream().map(VideoHotspotVo::getVideo_id).collect(Collectors.toList());
        if (isEmpty(queryList)) {
            return null;
        }
        // 过滤已曝光过的视频
        List<String> passVideoIds = new ArrayList<>();
        int queryListSize = queryList.size();
        int step = 100;
        int toIndex = Math.min(100, queryList.size());
        int start = 0;
        do {
            toIndex = Math.min(start + toIndex, queryListSize);
            passVideoIds.addAll(this.containMuil(getNum, userTag, queryList.subList(start, toIndex)));
            getNum = getNum - passVideoIds.size();
            start = start + step;
            step = Math.min(step * 2, queryListSize - start);
            toIndex = start + step;

        } while (getNum > 0 && start < queryListSize);

        List<VideoHotspotVo> passEntity = new ArrayList<>();
        sourceList.stream().forEach(e -> {
            if (passVideoIds.contains(e.getVideo_id())) {
                passEntity.add(e);
            }
        });
        return passEntity;
    }

    public List<String> containMuilSplit(int getSize, String uuid, List<String> vlist){
        int start = 0;
        int step = 100;
        int initNeedCount = getSize;
        int maxSize = vlist.size();
        // 最终返回的未曝光视频集合
        List<String> passVideoIds = new ArrayList<>();
        // 开始过滤已曝光过的视频
        do{
            int toIndex = start + step - 1;
            if(toIndex >= maxSize){
                toIndex = maxSize -1;
            }
            passVideoIds.addAll(this.containMuil(initNeedCount, uuid, vlist.subList(start, toIndex)));
            initNeedCount -= passVideoIds.size();
            start += step;
        } while (initNeedCount > 0 && start < maxSize);
        return passVideoIds;
    }

    public List<String> containMuil(int getSize, String uuid, List<String> vlist) {
        if("test".equals(uuid)){
            return vlist;
        }
        long filterStart = System.currentTimeMillis();
        String[] keys = new String[vlist.size()];
        for (int i = 0; i < vlist.size(); i++) {
            String v = vlist.get(i);
            String key = getKey(uuid, v);
            keys[i] = key;
        }
        //校验第一个布隆过滤器
        boolean[] b = null;
        boolean isPass = false;
        try {
            b = redis10BloomClient.existsMulti(RedisCountConstant.bloom_c, keys);
        } catch (Exception e) {
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
            boolean[] b2 = redis10BloomClient.existsMulti(RedisCountConstant.bloom_p, bv.toArray(new String[bv.size()]));
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
            try (Jedis con = recDB10Pool.getResource()) {
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
        log.info("bloom过滤器耗时: {}", System.currentTimeMillis() - filterStart);
        return rdata;
    }

    public Map<String, List<String>> containMuil(String uuid, List<String> videoIds){
        String[] keys = new String[videoIds.size()];
        for (int i = 0; i < videoIds.size(); i++) {
            String v = videoIds.get(i);
            String key = getKey(uuid, v);
            keys[i] = key;
        }

        List<String> noExistsKeys = new ArrayList<String>();
        List<String> noExistsList = new ArrayList<String>();
        List<String> existsList = new ArrayList<String>();

        //校验第一个布隆过滤器
        boolean isPass = false;
        boolean[] b = null;
        try {
            b = redis10BloomClient.existsMulti(RedisCountConstant.bloom_c, keys);
        } catch (Exception e) {
            log.error("布隆过滤器校验异常");
            b = new boolean[keys.length];
            isPass = true;
        }

        for (int i = 0; i < b.length; i++) {
            if (isPass) {
                noExistsKeys.add(keys[i]);
                noExistsList.add(videoIds.get(i));
            } else {
                if (b[i]) {
                    existsList.add(videoIds.get(i));
                } else {
                    noExistsKeys.add(keys[i]);
                    noExistsList.add(videoIds.get(i));
                }
            }
        }
        keys = null;

        if (!noExistsKeys.isEmpty()) {
            boolean[] b2 = redis10BloomClient.existsMulti(RedisCountConstant.bloom_p, noExistsKeys.toArray(new String[noExistsKeys.size()]));
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
        bloomResultMap.put("exists", existsList);
        bloomResultMap.put("noExists", noExistsList);
        return bloomResultMap;
    }

    public boolean putAll(String uuid, List<String> videoId) {
        if (CollectionUtils.isEmpty(videoId)) {
            return false;
        }
        // 先放入redis
        List<String> keys = Lists.newArrayList();
        String[] list = videoId.stream().flatMap(e -> {
            String key = cacheKey(uuid, e);
            keys.add(key);
            return Stream.of(key, "1");
        }).toArray(String[]::new);
        try (Jedis con = recDB10Pool.getResource()) {
            con.mset(list);
        }
        // 加上过期时间
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
