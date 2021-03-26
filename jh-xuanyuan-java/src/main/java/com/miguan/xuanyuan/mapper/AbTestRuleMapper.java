package com.miguan.xuanyuan.mapper;

import com.miguan.xuanyuan.vo.sdk.AbTestRuleVo;

import java.util.List;
import java.util.Map;

/**
 * @Description AB测试mapper
 **/
public interface AbTestRuleMapper {

    /**
     * 查询AB实验数据
     * @param params
     * @return
     */
    List<AbTestRuleVo> getABTextAdversByRule(Map<String, Object> params);
}

