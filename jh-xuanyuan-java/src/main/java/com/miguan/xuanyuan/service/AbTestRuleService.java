package com.miguan.xuanyuan.service;

import com.miguan.xuanyuan.vo.sdk.AbTestRuleVo;

import java.util.List;

/**
 * @Description AB测试service
 **/
public interface AbTestRuleService {

    /**
     * 查询AB实验数据
     * @param positionKey 广告位KEY
     * @param appKey appKey
     * @param mobileType  手机类型1*应用端:1-安卓，2-ios
     * @return
     */
    List<AbTestRuleVo> getABTextAdversByRule(String positionKey, String appKey, String mobileType);
}
