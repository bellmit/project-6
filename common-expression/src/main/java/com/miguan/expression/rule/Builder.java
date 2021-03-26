package com.miguan.expression.rule;

/**
 * @author xujinbang
 * @date 2019-7-30
 */
public interface Builder {
	

    /**
     * storing the rules which  produced from each thread
     */
    ThreadLocal<RuleBasic> threadLocalRules = new ThreadLocal<RuleBasic>();


    /**
     * return the rule want to build
     *
     * @return
     * @throws Exception
     */
    Rule build() throws Exception;
}
