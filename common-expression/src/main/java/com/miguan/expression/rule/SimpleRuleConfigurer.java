package com.miguan.expression.rule;


import java.util.Map;

/**
 * @author xujinbang
 * @date 2019-7-30
 */
public class SimpleRuleConfigurer<H, O extends Rule, B extends AbstractRuleBuilder<H, O>> extends RuleConfigurerAdapter<H, O, B> {

    @Override
    public RuleConfigurer<H> rulePolicy(RulePolicy rulePolicy) {
        RuleBasic ruleBasic = threadLocalRules.get();
        ruleBasic.setRulePolicy(rulePolicy);
        return this;
    }

    @Override
    @SuppressWarnings("All")
    public RuleConfigurer<H> preLoad(Map<H, Integer> map) {
        RuleBasic<H> ruleBasic = threadLocalRules.get();
        ruleBasic.setPreLoad(map);
        return this;
    }

    @Override
    public RuleConfigurer<H> name(String name) {
        RuleBasic<H> ruleBasic = threadLocalRules.get();
        ruleBasic.setName(name);
        return this;
    }

}
