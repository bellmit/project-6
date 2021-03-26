package com.miguan.laidian.service.impl;

import com.miguan.laidian.entity.ActActivityConfig;
import com.miguan.laidian.entity.ActActivityConfigExample;
import com.miguan.laidian.mapper.ActActivityConfigMapper;
import com.miguan.laidian.service.ActivityConfigService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author chenwf
 * @date 2020/5/22
 */
@Service
@Transactional
@Slf4j
public class ActivityConfigServiceImpl implements ActivityConfigService {
    @Resource
    private ActActivityConfigMapper actActivityConfigMapper;

    /**
     * 根据活动id获取奖品配置信息
     *
     * @param activityId
     * @return
     */
    @Override
    public List<ActActivityConfig> getActConfigByActId(Long activityId) {
        ActActivityConfigExample configExample = new ActActivityConfigExample();
        configExample.createCriteria().andActivityIdEqualTo(activityId);
        configExample.setOrderByClause("rotary_sort");
        return actActivityConfigMapper.selectByExample(configExample);
    }
}
