package com.miguan.expression.rule;

import com.miguan.expression.exception.RuleValueException;

/**
 * @author xujinbang
 * @date 2019-7-30
 */
public interface Rule {


    /**
     * set the obj to be matched
     *
     * @param o
     */
    void matchTo(Object o) throws RuleValueException;


    /**
     * start match logic and give the result
     *
     * @return
     */
    boolean beginMatch() throws RuleValueException;


    /**
     * get id
     *
     * @return
     */
    String getId();

    /**
     * get column name
     *
     * @return
     */
    String getColumn();

    /**
     * get rule name
     *
     * @return
     */
    String getName();
}
