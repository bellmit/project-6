package com.miguan.flow.mapper;

import com.baomidou.dynamic.datasource.annotation.DS;
import com.miguan.flow.vo.AbTestRuleVo;
import org.apache.ibatis.annotations.Param;

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

    /**
     * 根据ab测试id查询状态
     * @param abTestId
     * @return
     */
    Integer queryOpenStatusByAbTestId(@Param("abTestId") Integer abTestId);
}

