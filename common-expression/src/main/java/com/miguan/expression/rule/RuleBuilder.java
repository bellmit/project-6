package com.miguan.expression.rule;


/**
 * @author xujinbang
 * @date 2019-7-30
 */
public interface RuleBuilder<T> {

    /**
     * rule condition collect
     *
     * @return
     */
    ConditionItem newConditionItems();


    /**
     * pass three param which rule must be needed
     *
     * @param id
     * @param column
     * @param conditionItem
     * @return
     */
    RuleConfigurer<T> newRule(String id, String column, ConditionItem conditionItem) throws IllegalAccessException, InstantiationException;


}
