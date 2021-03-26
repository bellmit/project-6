package com.miguan.report.service.report;

import com.miguan.report.entity.BannerRule;

import java.util.Map;

public interface BannerRuleService {

    public Map<String, BannerRule> findDistinctByTodal();

}
