package com.miguan.expression.rule;

import java.util.List;
import java.util.Map;

/**
 * @author xujinbang
 * @date 2019-7-30
 */
public interface RuleBasic<T> {

    void setId(String id);

    void setColumn(String column);

    void setName(String name);

    void setValueType(Class<T> clazz);

    void setConditions(List<Condition<T>> conditions);

    void setRulePolicy(RulePolicy rulePolicy);

    void setPreLoad(Map<T, Integer> preLoad);
}
