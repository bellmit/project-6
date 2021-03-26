package com.miguan.expression.rule;

import com.miguan.expression.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 规则校验
 * @author xujinbang
 * @date 2019-7-30
 */
public class GenerateRule {

	private static final Logger logger = LoggerFactory.getLogger(GenerateRule.class);

	/**
	 * 比较字符串类型的执行结果
	 * @param formula
	 * @param scop
	 * @param value
	 * @return
	 */
	public static boolean comparText(String formula,String scop,String value){
    	boolean result = false;
    	if(Formula.include.getFormula().equals(formula)){
    		result = StringUtils.contains(scop, value) || StringUtils.contains(value, scop);
    	} else if(Formula.in.getFormula().equals(formula)) {
    		result = StringUtils.contains(scop, value);
		} else if(Formula.exclude.getFormula().equals(formula)){
    		result = !StringUtils.contains(scop, value);
    	} else if(Formula.equal.getFormula().equals(formula)){
        	result = StringUtils.equals(scop, value);
        } else if(Formula.not_equal.getFormula().equals(formula)){
        	result = !StringUtils.equals(scop, value);
    	} else if(Formula.startWith.getFormula().equals(formula)){
    		result = value.startsWith(scop);
		} else if(Formula.endWith.getFormula().equals(formula)){
			result = value.endsWith(scop);
		} else{
			// 如果要判断的这的值为数字类型，可比较值大小,生成数字类型的规则
			if (StringUtils.isNumber(scop)) {
				SimpleRule simpleRule = new SimpleRule("0", "name",formula, value, scop, "int", "");
				Rule rule = GenerateRule.genNumRule(simpleRule);
				if (rule!=null) {
					result = rule.beginMatch();
				}
			}
    	}
		logger.info("规则校验:表达式>>{}, 规则值>>{}, 用户风控值>>{}, 结果>>{}",
				formula, scop, value, result ? "通过" : "不通过");
    	return result;
    }
    
    /**
     * 获得字符类型的匹配结果
     * @param rule
     * @return
     */
    public static boolean genTextResult(SimpleRule rule){
    	return comparText(rule.getFormula(),rule.getRange(),rule.getValue());
    }
    
    /**
     * 数字类型的对比规则
     * @param rule 对比规则
     * @return
     */
    public static Rule genNumRule(SimpleRule rule){
    	
    	RuleBuilder<Double> builder = RuleBuilderCreator.numRuleBuilder();
    	ConditionItem items = buildNumItems(builder, rule.getFormula(), rule.getRange());
		Rule rtRule = null;
		try {
			rtRule = builder.newRule(rule.getRuleId(), rule.getName(), items).rulePolicy(RulePolicy.MATCHALL).build();
			rtRule.matchTo(Double.valueOf(rule.getValue()));
		} catch (IllegalAccessException e) {
			logger.error(e.getMessage(),e);
		} catch (InstantiationException e) {
			logger.error(e.getMessage(),e);
		} catch (Exception e) {
			logger.error(e.getMessage(),e);
		}
		
		return rtRule;
    }
    
    /**
     * 组装数字类型的匹配表达式
     * @param builder
     * @param formula
     * @param range
     * @return
     */
    public static ConditionItem buildNumItems(RuleBuilder<Double> builder,String formula,String range){
    	ConditionItem items = builder.newConditionItems();
    	if(Formula.greater.getFormula().equals(formula)){
    		items.add(Formula.greater.getFormula(), Double.valueOf(range));
    	}else if(Formula.less.getFormula().equals(formula)){
    		items.add(Formula.less.getFormula(), Double.valueOf(range));
    	}else if(Formula.equal.getFormula().equals(formula)){
    		items.add(Formula.equal.getFormula(), Double.valueOf(range));
    	}else if(Formula.not_equal.getFormula().equals(formula)){
    		items.add(Formula.not_equal.getFormula(), Double.valueOf(range));
    	}else if(Formula.greater_equal.getFormula().equals(formula)){
    		items.add(Formula.greater_equal.getFormula(), Double.valueOf(range));
    	}else if(Formula.less_equal.getFormula().equals(formula)){
    		items.add(Formula.less_equal.getFormula(), Double.valueOf(range));
    	}else if(Formula.greater_equal_and_less_equal.getFormula().equals(formula)){
    		String[] ranges = range.trim().split(",");
    		items.add(Formula.greater_equal.getFormula(), Double.valueOf(ranges[0]));
    		items.add(Formula.less_equal.getFormula(), Double.valueOf(ranges[1]));
    	}else if(Formula.greater_equal_and_less.getFormula().equals(formula)){
    		String[] ranges = range.trim().split(",");
    		items.add(Formula.greater_equal.getFormula(), Double.valueOf(ranges[0]));
    		items.add(Formula.less.getFormula(), Double.valueOf(ranges[1]));
    	}else if(Formula.greater_and_less_equal.getFormula().equals(formula)){
    		String[] ranges = range.trim().split(",");
    		items.add(Formula.greater.getFormula(), Double.valueOf(ranges[0]));
    		items.add(Formula.less_equal.getFormula(), Double.valueOf(ranges[1]));
    	}else if(Formula.greater_and_less.getFormula().equals(formula)){
    		String[] ranges = range.trim().split(",");
    		items.add(Formula.greater.getFormula(), Double.valueOf(ranges[0]));
    		items.add(Formula.less.getFormula(), Double.valueOf(ranges[1]));
    	}else{
    		//没有匹配的表达式
    	}
    	
    	return items;
    }
    
    
}
