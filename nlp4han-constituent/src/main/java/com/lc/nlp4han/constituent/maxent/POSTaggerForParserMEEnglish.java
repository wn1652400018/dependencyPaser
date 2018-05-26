package com.lc.nlp4han.constituent.maxent;

import java.util.List;

import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.pos.word.POSTaggerWordME;

/**
 * 英文的词性标注器
 * 
 * @author 王馨苇
 *
 */
// TODO: 此类和POSTaggerForParserMEChinese大量代码重复，需要重构
public class POSTaggerForParserMEEnglish implements POSTaggerForParser<HeadTreeNode>
{

	private POSTaggerWordME postagger;

	public POSTaggerForParserMEEnglish(ModelWrapper posmodel)
	{
		postagger = new POSTaggerWordME(posmodel);
	}

	/**
	 * 得到词性标注子树序列
	 * 
	 * @param words
	 *            分词数组
	 * @return
	 */
	@Override
	public List<HeadTreeNode> posTree(String[] words)
	{
		String[][] posres = postagger.tag(words, 1);
		List<List<HeadTreeNode>> posTree = HeadTreeNode.toPosTree(words, posres);
		return posTree.get(0);
	}

	/**
	 * 得到词性标注子树序列
	 * 
	 * @param sentence
	 *            分词句子
	 * @return
	 */
	@Override
	public List<HeadTreeNode> posTree(String sentence)
	{
		String[] words = sentence.split(" ");
		return posTree(words);
	}

	/**
	 * 得到最好的K个词性标注子树序列
	 * 
	 * @param words
	 *            分词数组
	 * @param k
	 *            最好的K个结果
	 * @return
	 */
	@Override
	public List<List<HeadTreeNode>> posTree(String[] words, int k)
	{
		String[][] posres = postagger.tag(words, k);
		List<List<HeadTreeNode>> posTree = HeadTreeNode.toPosTree(words, posres);
		return posTree;
	}

	/**
	 * 得到最好的K个词性标注子树序列
	 * 
	 * @param k
	 *            最好的K个结果
	 * @param sentece
	 *            分词句子
	 * @return
	 */
	@Override
	public List<List<HeadTreeNode>> posTree(String sentence, int k)
	{
		String[] words = sentence.split(" ");
		return posTree(words, k);
	}
}
