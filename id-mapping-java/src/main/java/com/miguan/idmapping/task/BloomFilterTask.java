package com.miguan.idmapping.task;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.miguan.idmapping.common.utils.DateUtil;
import com.miguan.idmapping.entity.ClDevice;
import com.miguan.idmapping.service.ClDeviceService;
import com.miguan.idmapping.service.impl.BloomFilterService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.List;

/**
 * @author zhongli
 * @date 2020-08-27 
 *
 */
@Component
@Slf4j
public class BloomFilterTask {
    private static final String device_bloom_lock = "UUID:xydeviceAddBloomLock";
    private static final String device_bloom_lock_rename = "UUID:deviceBloomRenameLock";
    public static volatile boolean isRunning = false;
    @Autowired
    private ClDeviceService clDeviceService;
    @Autowired
    private BloomFilterService bloomFilterService;
    @Autowired
    private RedissonClient redissonClient;

    /**
     * 每天的00:00:00执行
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void sysn() throws InterruptedException {
        RLock lock = redissonClient.getLock(device_bloom_lock);
        isRunning = true;
        if (lock.tryLock()) {
            try {
                String date = DateUtil.yedyyyy_MM_dd();
                String startDate = date.concat(" 00:00:00");
                String endDate = date.concat(" 23:59:59");
                add2Bloom(0L, startDate, endDate);
            } catch (Exception ex) {
                log.error("执行同步注册设备信息至布隆过器失败", ex);
            } finally {
                lock.unlock();
                isRunning = false;
            }
        } else {
            try {
                long pt = System.currentTimeMillis();
                while (lock.isLocked()) {
                    Thread.currentThread().wait(10000L);
                    long timec = System.currentTimeMillis() - pt;
                    //超过10分钟则直接退出循环
                    if (timec > 600000) {
                        break;
                    }
                }
            } catch (Exception e) {
                log.error("检查锁出现异常", e);
            } finally {
                isRunning = false;
            }
        }
    }

    private void add2Bloom(Long maxId, String startDate, String endDate) {
        LambdaQueryWrapper<ClDevice> wrapps = Wrappers.lambdaQuery(ClDevice.class)
                .select(ClDevice::getId, ClDevice::getDeviceId, ClDevice::getAppPackage)
                .ge(ClDevice::getCreateTime, startDate)
                .le(ClDevice::getCreateTime, endDate)
                .gt(ClDevice::getId, maxId)
                .last("limit 5000")
                .orderByAsc(ClDevice::getId);
        List<ClDevice> list = clDeviceService.list(wrapps);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(e -> {
            if (StringUtils.isNoneBlank(e.getDeviceId(), e.getAppPackage())) {
                bloomFilterService.put(e.getDeviceId(), e.getAppPackage());
            }
        });
        log.warn("初始化加载布隆：{}", list.size());
        if (list.size() < 5000) {
            return;
        }
        maxId = list.get(list.size() - 1).getId();
        list.clear();
        list = null;
        add2Bloom(maxId, startDate, endDate);
    }

    /**
     * 每天的00:00:00执行
     */
    @Scheduled(cron = "0 0 5 * * ?")
    public void adjustmentBloomFilter() {
        RLock lock = redissonClient.getLock(device_bloom_lock_rename);
        try {
            if (lock.tryLock()) {
                newAndswtich(bloomFilterService.isThreshold(), 10000000L);
            }
        } catch (Exception ex) {
            log.error("执行扩容布隆过器失败", ex);
        } finally {
            lock.unlock();
        }
    }

    public void newAndswtich(boolean isSwitch, Long expsize) {
        if (isSwitch) {
            bloomFilterService.createNewFilter(expsize);
            String date = DateUtil.yyyy_MM_dd();
            date = date.concat(" 00:00:00");
            add2Bloom(true, 0L, date);
            log.warn("初始化加载布隆完成，进行切换");
            bloomFilterService.switchNewFilter();
            log.warn("初始化加载布隆完成，切换完成");
        }
    }

    public void add2Bloom(boolean isNew, Long maxId, String endDate) {
        LambdaQueryWrapper<ClDevice> wrapps = Wrappers.lambdaQuery(ClDevice.class).select(ClDevice::getId, ClDevice::getDeviceId, ClDevice::getAppPackage)
                .gt(ClDevice::getId, maxId)
                .le(ClDevice::getCreateTime, endDate)
                .last("limit 5000")
                .orderByAsc(ClDevice::getId);
        List<ClDevice> list = clDeviceService.list(wrapps);
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        list.forEach(e -> {
            if (StringUtils.isNoneBlank(e.getDeviceId(), e.getAppPackage())) {
                if (isNew) {
                    bloomFilterService.putNew(e.getDeviceId(), e.getAppPackage());
                } else {
                    bloomFilterService.put(e.getDeviceId(), e.getAppPackage());
                }
            }
        });
        log.warn("初始化加载布隆：{}", list.size());
        if (list.size() < 5000) {
            return;
        }
        maxId = list.get(list.size() - 1).getId();
        list.clear();
        list = null;
        add2Bloom(isNew, maxId, endDate);
    }
}
