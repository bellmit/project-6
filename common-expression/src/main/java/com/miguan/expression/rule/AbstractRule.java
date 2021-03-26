package com.miguan.expression.rule;

import com.miguan.expression.exception.RuleValueException;

import java.util.List;
import java.util.Map;

/**
 * @author xujinbang
 * @date 2019-7-30
 */
public abstract class AbstractRule<T> implements Rule, RuleBasic<T> {


    protected List<Condition<T>> conditions;

    protected RulePolicy policy = RulePolicy.MATCHALL;


    protected Map<T, Integer> preLoad;

    protected Class<T> valueClass;

    protected String id;

    protected String column;

    protected String name;

    protected T matchTo;


    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public void setColumn(String column) {
        this.column = column;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setValueType(Class<T> clazz) {
        this.valueClass = clazz;
    }

    @Override
    @SuppressWarnings("All")
    public void matchTo(Object o) throws RuleValueException {
        if (o instanceof Integer || o instanceof Long || o instanceof Float) {
            o = Double.valueOf(o.toString());
        }
        if (o.getClass() != valueClass) {
            throw new RuleValueException("the matchTo Object: " + o + " which type is " + o.getClass().getName() + ",is not fit for the " + valueClass.getName());
        }
        this.matchTo = (T) o;
    }


    @Override
    public String getId() {
        return this.id;
    }

    @Override
    public String getColumn() {
        return this.column;
    }

    @Override
    public String getName() {
        return this.name;
    }


    @Override
    public void setConditions(List<Condition<T>> conditions) {
        this.conditions = conditions;
    }

    @Override
    public void setRulePolicy(RulePolicy rulePolicy) {
        this.policy = rulePolicy;
    }

    @Override
    public void setPreLoad(Map<T, Integer> preLoad) {
        this.preLoad = preLoad;
    }
}
