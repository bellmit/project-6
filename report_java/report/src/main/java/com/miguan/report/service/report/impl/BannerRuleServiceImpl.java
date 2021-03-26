package com.miguan.report.service.report.impl;

import com.miguan.report.entity.BannerRule;
import com.miguan.report.mapper.BannerRuleMapper;
import com.miguan.report.service.report.BannerRuleService;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BannerRuleServiceImpl implements BannerRuleService {

    @Resource
    private BannerRuleMapper bannerRuleMapper;

    @Override
    public Map<String, BannerRule> findDistinctByTodal() {
        List<BannerRule> ruleList = bannerRuleMapper.findDistinctByTotalName();
        Map<String, BannerRule> resultMap = new HashMap<String, BannerRule>();
        if (CollectionUtils.isNotEmpty(ruleList)) {
            ruleList.stream().forEach(t -> {
                resultMap.put(t.getTotalName() + "-" + t.getAppType(), t);
            });
        }
        return resultMap;
    }
}
