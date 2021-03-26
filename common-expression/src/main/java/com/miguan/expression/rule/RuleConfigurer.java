package com.miguan.expression.rule;

import java.util.Map;

/**
 * @author xujinbang
 * @date 2019-7-30
 */
public interface RuleConfigurer<H> extends Builder {

    /**
     * define the rule policy , is match all or match one
     *
     * @param rulePolicy
     * @return
     */
    RuleConfigurer<H> rulePolicy(RulePolicy rulePolicy);


    /**
     * load the string data to range the match value ,help machine to recognize
     *
     * @param map
     * @return
     */
    RuleConfigurer<H> preLoad(Map<H, Integer> map);


    RuleConfigurer<H> name(String name);


}
