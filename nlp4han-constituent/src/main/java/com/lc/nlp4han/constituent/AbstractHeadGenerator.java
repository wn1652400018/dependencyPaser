package com.lc.nlp4han.constituent;

import java.util.HashMap;
import java.util.List;

/**
 * 生成头结点
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public abstract class AbstractHeadGenerator
{

	/**
	 * 为并列结构生成头结点和词性
	 * 
	 * @param node
	 *            子节点带头结点，父节点不带头结点的树
	 * @return
	 */
	protected abstract String generateHeadPosForCordinator(HeadTreeNode node);

	/**
	 * 为特殊规则生成头结点和词性
	 * 
	 * @param node
	 *            子节点带头结点，父节点不带头结点的树
	 * @param specialRules
	 *            生成头结点的特殊规则
	 * @return
	 */
	protected abstract String generateHeadPosForSpecialRules(HeadTreeNode node,
			HashMap<String, List<HeadRule>> specialRules);

	/**
	 * 为一般规则生成头结点和词性
	 * 
	 * @param node
	 *            子节点带头结点，父节点不带头结点的树
	 * @param normalRules
	 *            生成头结点的一般规则
	 * @return
	 */
	protected abstract String generateHeadPosForNormalRules(HeadTreeNode node, HashMap<String, HeadRule> normalRules);

	/**
	 * 提取头结点和头结点对应的词性
	 * 
	 * 头节点词和词性间_分隔
	 * 
	 * @param node
	 *            子节点带头结点，父节点不带头结点的树
	 * @param normalRules
	 *            生成头结点的一般规则
	 * @param specialRules
	 *            生成头结点的特殊规则
	 * @return 头结点和头结点对应的词性
	 */
	private String extractHeadWordAndPos(HeadTreeNode node, HashMap<String, HeadRule> normalRules,
			HashMap<String, List<HeadRule>> specialRules)
	{
		String headWithPOS = null;
		headWithPOS = generateHeadPosForCordinator(node);

		if (headWithPOS == null && specialRules != null)
		{
			headWithPOS = generateHeadPosForSpecialRules(node, specialRules);
		}

		if (headWithPOS == null && normalRules != null)
		{
			headWithPOS = generateHeadPosForNormalRules(node, normalRules);
		}
		
		return headWithPOS;
	}

	/**
	 * 提取头结点词
	 * 
	 * @param node
	 *            子节点带头结点，父节点不带头结点的树
	 * @param normalRules
	 *            生成头结点的一般规则
	 * @param specialRules
	 *            生成头结点的特殊规则
	 * @return 头结点词
	 */
	public String extractHeadWord(HeadTreeNode node, HashMap<String, HeadRule> normalRules,
			HashMap<String, List<HeadRule>> specialRules)
	{

		return extractHeadWordAndPos(node, normalRules, specialRules).split("_")[0];
	}

	/**
	 * 提取头结点的词性
	 * 
	 * @param node
	 *            子节点带头结点，父节点不带头结点的树
	 * @param normalRules
	 *            生成头结点的一般规则
	 * @param specialRules
	 *            生成头结点的特殊规则
	 * @return 头结点的词性
	 */
	public String extractHeadPos(HeadTreeNode node, HashMap<String, HeadRule> normalRules,
			HashMap<String, List<HeadRule>> specialRules)
	{

		return extractHeadWordAndPos(node, normalRules, specialRules).split("_")[1];
	}
}
