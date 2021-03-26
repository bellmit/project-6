package com.miguan.flow.service;

import com.miguan.flow.vo.AbTestRuleVo;

import java.util.List;

/**
 * @Description AB测试service
 **/
public interface AbTestRuleService {

    /**
     * 查询AB实验数据
     * @param positionType 广告位key
     * @param appPackage 包名
     * @param mobileType  手机类型1*应用端:1-ios，2-安卓
     * @return
     */
    List<AbTestRuleVo> getABTextAdversByRule(String positionType, String appPackage, String mobileType);
}
