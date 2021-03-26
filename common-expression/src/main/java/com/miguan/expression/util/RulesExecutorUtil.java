package com.miguan.expression.util;


import com.miguan.expression.rule.GenerateRule;
import com.miguan.expression.rule.Rule;
import com.miguan.expression.rule.SimpleRule;

/**
 * 规则执行工具类
 * @author xujinbang
 * @date 2019-7-30
 */
public class RulesExecutorUtil {

	/**
	 * 单条规则匹配
	 * @param simpleRule
	 * @return
	 */
	public static SimpleRule singleRuleResult(SimpleRule simpleRule){
		boolean result = false;
		if(SimpleRule.TEXT.equals(simpleRule.getType())){
			//字符类型匹配
			result = GenerateRule.genTextResult(simpleRule);
		}else if(SimpleRule.NUMERIC.equals(simpleRule.getType())){
			//生成数字类型的规则
			Rule rule = GenerateRule.genNumRule(simpleRule);
			result = rule.beginMatch();
		}
		if(result){
			simpleRule.setComparResult(SimpleRule.COMPAR_PASS);
		}else{
			simpleRule.setComparResult(SimpleRule.COMPAR_FAIL);
		}
		
		return simpleRule;
	}
	
	/**
	 * 单条规则匹配
	 * @param id 唯一标识
	 * @param name 名称
	 * @param formula 表达式
	 * @param range 范围
	 * @param value 需要做匹配的值
	 * @param type 类型 int  string
	 * @param resultType 1 通过 0 不通过
	 * @return
	 */
	public static SimpleRule singleRuleResult(String id,String name,String formula,String range,String value,String type,String resultType){
		SimpleRule simpleRule = new SimpleRule(id, name, formula, value, range, type, resultType);
		return singleRuleResult(simpleRule);
	}
}
