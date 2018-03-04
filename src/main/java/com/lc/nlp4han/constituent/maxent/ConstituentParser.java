package com.lc.nlp4han.constituent.maxent;

import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.TreeNode;

/**
 * 句法分析器
 * @author 王馨苇
 *
 */
public interface ConstituentParser<T extends TreeNode> {

	/**
	 * 得到句法树
	 * @param words 分词序列
	 * @param poses 词性标记
	 * @return
	 */
	ConstituentTree parseTree(String[] words,String[] poses);
	/**
	 * 得到句法树
	 * @param words 分词序列
	 * @return
	 */
	ConstituentTree parseTree(String[] words);

	/**
	 * 得到最好的K个句法树
	 * @param k 最好的K个结果
	 * @param words 词语
	 * @param poses 词性标记
	 * @return
	 */
	ConstituentTree[] parseKTree(int k,String[] words,String[] poses);
	
	/**
	 * 得到最好的K个句法树
	 * @param k 最好的K个结果
	 * @param words 分词序列
	 * @return
	 */
	ConstituentTree[] parseKTree(int k,String[] words);
}
