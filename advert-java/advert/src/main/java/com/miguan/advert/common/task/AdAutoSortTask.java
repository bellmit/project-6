package com.miguan.advert.common.task;

import com.cgcg.context.util.StringUtils;
import com.miguan.advert.common.util.HttpUtil;
import com.miguan.advert.common.util.ResultMap;
import com.miguan.advert.common.util.RobotUtil;
import com.miguan.advert.common.util.redis.RedisService;
import com.miguan.advert.config.redis.util.RedisKeyConstant;
import com.miguan.advert.domain.service.AdAutoSortService;
import lombok.extern.slf4j.Slf4j;
import org.cgcg.redis.core.entity.RedisLock;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import tool.util.StringUtil;

import javax.annotation.Resource;

/**
 * 代码位自动排序定时器
 */
@Slf4j
@Component
public class AdAutoSortTask {

    @Resource
    private AdAutoSortService adAutoSortService;
    @Resource
    private RedisService redisService;

    /**
     * 代码位自动排序
     */
    @Scheduled(cron = "${task.scheduled.cron.adIdAutoSort}")
    public void adIdAutoSort() {
        if(StringUtil.isNotBlank(redisService.get("todayAutoSortTag"))) {
            //当天的自动排序已经排序过
            return;
        }
        log.info("代码位自动排序（start）");
        adAutoSortService.adAutoSort();
        log.info("代码位自动排序（end）");
    }

    /**
     * 当今天的代码位的展现量超过阀值后，更新代码位的排序为昨天的排序
     */
    @Scheduled(cron = "${task.scheduled.cron.updateAutoSort}")
    public void updateSortWhenGtThreshold() {
        if(StringUtil.isBlank(redisService.get("todayAutoSortTag"))) {
            //当天还没进行自动排序的话，则不进行代码位是否超过阀值检查
            return;
        }
        log.info("当今天的代码位的展现量超过阀值后，更新代码位的排序为昨天的排序（start）");
        adAutoSortService.updateSortWhenGtThreshold();
        log.info("当今天的代码位的展现量超过阀值后，更新代码位的排序为昨天的排序（end）");
    }
}
