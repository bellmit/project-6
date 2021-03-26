package com.miguan.expression.exception;

/**
 * 自定义规则执行异常类
 * @author xujinbang
 * @date 2019-7-30
 */
public class RuleValueException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public RuleValueException(String message) {
        super(message);
    }
}
