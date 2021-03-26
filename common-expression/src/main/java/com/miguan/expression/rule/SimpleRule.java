package com.miguan.expression.rule;

import lombok.Data;

/**
 * 基础规则信息，默认规则为结果导向
 * @author xujinbang
 * @date 2019-7-30
 */
@Data
public class SimpleRule {

	/**
	 * 数字类型
	 */
	public static final String NUMERIC = "int";
	
	/**
	 * 文字类型
	 */
	public static final String TEXT = "string";

	/**
	 * 对比结果1匹配
	 */
	public static final int COMPAR_PASS = 1;
	/**
	 * 对比结果0不匹配
	 */
	public static final int COMPAR_FAIL = 0;
	
	/**
	 * 规则唯一标识
	 */
	public String ruleId;
	
	/**
	 * 规则名称
	 */
	public String name; 
	
	/**
	 * 规则表达式 
	 */
	public String formula;
	
	/**
	 * 需要匹配的值
	 */
	public String value;
	
	/**
	 * 取值范围
	 */
	public String range;
	
	/**
	 * 对比类型   Numeric  Text
	 */
	public String type;
	
	/**
	 * 结果类型 10 不通过   20 待人工复审  30 通过
	 */
	public String resultType;
	
	/**
	 * 对比结果 0不匹配1匹配
	 */
	public int comparResult;
	
	public SimpleRule() {
		super();
	}

	/**
	 * 构造规则对象
	 * @param ruleId 规则唯一标识
	 * @param name 规则对比的参数名称
	 * @param formula 规则对比使用的表达式 
	 * @param value 需要做比对的值
	 * @param range 表达式的取值范围
	 * @param type 表达式的类型   Numeric(数字类型)  Text(文本类型)
	 * @param resultType 结果类型 10 不通过   20 待人工复审  30 通过 (表达式匹配时代表的结果)
	 */
	public SimpleRule(String ruleId, String name, String formula, String value,
                      String range, String type, String resultType) {
		super();
		this.ruleId = ruleId;
		this.name = name;
		this.formula = formula;
		this.value = value;
		this.range = range;
		this.type = type;
		this.resultType = resultType;
	}
}