package com.miguan.report.mapper;

import com.miguan.report.entity.BannerRule;

import java.util.List;

public interface BannerRuleMapper {

    public List<BannerRule> findDistinctByTotalName();
}
