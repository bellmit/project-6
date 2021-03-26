package com.miguan.expression.rule;

/**
 * @author xujinbang
 * @date 2019-7-30
 */
public class AbstractCondition<T> implements Condition<T> {


    protected ConditionOpt conditionOpt;


    protected T value;


    @Override
    public Condition<T> opt(ConditionOpt opt) {
        this.conditionOpt = opt;
        return this;
    }


    @Override
    public Condition<T> value(T t) {
        this.value = t;
        return this;
    }

    @Override
    public ConditionOpt getOpt() {
        return conditionOpt;
    }


    @Override
    public T getValue() {
        return value;
    }
}
