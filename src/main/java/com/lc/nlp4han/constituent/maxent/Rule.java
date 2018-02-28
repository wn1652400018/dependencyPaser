package com.lc.nlp4han.constituent.maxent;

import java.util.List;

/**
 * 规则的结构设计类
 * 说明：该规则包含规则的右部和遍历的方向
 * @author 王馨苇
 *
 */
public class Rule {

	private List<String> rightRules;
	private String direction;
	
	public Rule(List<String> rightRules, String direction){
		this.direction = direction;
		this.rightRules = rightRules;
	}
	
	public List<String> getRightRules(){
		return this.rightRules;
	}
	
	public String getDirection(){
		return this.direction;
	}
}
