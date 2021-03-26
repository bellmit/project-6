package com.miguan.expression.rule;


/**
 * @author xujinbang
 * @date 2019-7-30
 */
public interface Condition<T> {

    /**
     * 设置操作符
     *
     * @param opt
     * @return
     */
    Condition<T> opt(ConditionOpt opt);


    /**
     * 设置条件的值
     *
     * @param t
     * @return
     */
    Condition<T> value(T t);


    ConditionOpt getOpt();


    T getValue();

}
