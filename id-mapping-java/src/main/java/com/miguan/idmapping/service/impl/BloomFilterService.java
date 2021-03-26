package com.miguan.idmapping.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.DigestUtils;
import org.redisson.api.RBloomFilter;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

/**
 * @author zhongli
 * @date 2020-08-12 
 *
 */
@Service
@Slf4j
public class BloomFilterService {
    private RBloomFilter<Object> redisbdDeviceReg;
    @Autowired
    private RedissonClient redissonClient;
    private RBloomFilter<Object> newredisbdDeviceReg;
    private final String FILTER_KEY = "bdDeviceReg";

    @PostConstruct
    public void init() {
        redisbdDeviceReg = redissonClient.getBloomFilter(FILTER_KEY);
        long initsize = redisbdDeviceReg.isExists() ? redisbdDeviceReg.getExpectedInsertions() : 10000000;
        redisbdDeviceReg.tryInit(initsize, 0.0001);
    }

    public boolean contain(String deviceId, String packageName) {
        return redisbdDeviceReg.contains(getKey(deviceId, packageName));
    }

    public boolean put(String deviceId, String packageName) {
        return redisbdDeviceReg.add(getKey(deviceId, packageName));
    }

    public String getKey(String deviceId, String packageName) {
        return DigestUtils.md5Hex(deviceId);
    }

    public boolean isThreshold() {
        long size = redisbdDeviceReg.getExpectedInsertions();
        long count = redisbdDeviceReg.count();
        double rate = Long.valueOf(count).doubleValue() / Long.valueOf(size).doubleValue();
        if (rate >= 0.95) {
            return true;
        }
        return false;
    }

    public boolean createNewFilter(Long expsize) {
        newredisbdDeviceReg = redissonClient.getBloomFilter(FILTER_KEY.concat("New"));
        long size = redisbdDeviceReg.getExpectedInsertions() + (expsize == null ? 0 : expsize.longValue());
        return newredisbdDeviceReg.tryInit(size, 0.0001);
    }

    public void switchNewFilter() {
        long size = redisbdDeviceReg.getExpectedInsertions();
        redisbdDeviceReg.delete();
        log.warn("初始化加载布隆完成，进行切换,删除成功");
        newredisbdDeviceReg.rename(FILTER_KEY);
        log.warn("初始化加载布隆完成，进行切换,重命名成功");
        redisbdDeviceReg = redissonClient.getBloomFilter(FILTER_KEY);
        log.warn("初始化加载布隆完成，进行切换,重新加载成功");
        redisbdDeviceReg.tryInit(size, 0.0001);
        log.warn("初始化加载布隆完成，进行切换,重新初始化成功");
        newredisbdDeviceReg = null;
    }

    public boolean putNew(String deviceId, String packageName) {
        return newredisbdDeviceReg.add(getKey(deviceId, packageName));
    }

}
