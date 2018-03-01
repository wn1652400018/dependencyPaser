package com.lc.nlp4han.constituent.maxent;

import java.util.HashMap;
import java.util.List;

import com.lc.nlp4han.constituent.HeadTreeNode;

/**
 * 生成头结点的抽象模板类
 * @author 王馨苇
 *
 */
public abstract class AbstractGenerateHeadWords{
	
	/**
	 * 为并列结构生成头结点
	 * @param node 子节点带头结点，父节点不带头结点的树
	 * @return
	 */
	protected abstract String generateHeadWordsForCordinator(HeadTreeNode node);

	/**
	 * 为特殊规则生成头结点
	 * @param node 子节点带头结点，父节点不带头结点的树
	 * @param specialRules 生成头结点的特殊规则
	 * @return
	 */
	protected abstract String generateHeadWordsForSpecialRules(HeadTreeNode node,HashMap<String,List<Rule>> specialRules);
	
	/**
	 * 为一般规则生成头结点
	 * @param node 子节点带头结点，父节点不带头结点的树
	 * @param normalRules 生成头结点的一般规则
	 * @return
	 */
	protected abstract String generateHeadWordsForNormalRules(HeadTreeNode node,HashMap<String,Rule> normalRules);

	/**
	 * 合并生成头结点的所有方法，提取头结点
	 * @param node 子节点带头结点，父节点不带头结点的树
	 * @param normalRules 生成头结点的一般规则
	 * @param specialRules 生成头结点的特殊规则
	 * @return
	 */
	public String extractHeadWords(HeadTreeNode node, HashMap<String,Rule> normalRules,HashMap<String,List<Rule>> specialRules){
		String headWords = null;
		headWords = generateHeadWordsForCordinator(node);
		
		if(headWords == null && specialRules != null){			
			headWords = generateHeadWordsForSpecialRules(node,specialRules);	
		}
		
		if(headWords == null && normalRules != null){
			headWords = generateHeadWordsForNormalRules(node,normalRules);
		}
		return headWords;
	}
}
