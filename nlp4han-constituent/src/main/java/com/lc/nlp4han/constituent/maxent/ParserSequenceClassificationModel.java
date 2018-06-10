package com.lc.nlp4han.constituent.maxent;

import java.util.List;

import com.lc.nlp4han.constituent.HeadTreeNode;

/**
 * 句法分析序列分类模型
 * 
 * @author 王馨苇
 *
 */
public interface ParserSequenceClassificationModel
{

	/**
	 * 得到最好的chunk结果
	 * 
	 * @param posTree
	 *            pos步得到的最好的K棵树
	 * @param ac
	 *            额外的信息
	 * @param generator
	 *            特征生成器
	 * @param validator
	 *            序列验证
	 * @return
	 */
	ChunkSequence bestSequenceForChunk(List<List<HeadTreeNode>> posTree, Object[] ac,
			ParserContextGenerator generator, ParserSequenceValidator validator);

	/**
	 * 得到最好的num个chunk结果
	 * 
	 * @param num
	 *            最好的num个序列
	 * @param posTree
	 *            pos步得到的最好的K棵树
	 * @param ac
	 *            额外的信息
	 * @param min
	 *            得分最低的限制
	 * @param generator
	 *            特征生成器
	 * @param validator
	 *            序列验证
	 * @return
	 */
	ChunkSequence[] bestSequencesForChunk(int num, List<List<HeadTreeNode>> posTree, Object[] ac, double min,
			ParserContextGenerator generator, ParserSequenceValidator validator);

	/**
	 * 得到最好的num个chunk结果
	 * 
	 * @param num
	 *            最好的num个序列
	 * @param posTree
	 *            pos步得到的最好的K棵树
	 * @param ac
	 *            额外的信息
	 * @param generator
	 *            特征生成器
	 * @param validator
	 *            序列验证
	 * @return
	 */
	ChunkSequence[] bestSequencesForChunk(int num, List<List<HeadTreeNode>> posTree, Object[] ac,
			ParserContextGenerator generator, ParserSequenceValidator validator);

	/**
	 * 得到最好的BuildAndCheck结果
	 * 
	 * @param comnineChunkTree
	 *            chunk步得到的最好的K棵树合并之后
	 * @param ac
	 *            额外的信息
	 * @param generator
	 *            特征生成器
	 * @param validator
	 *            序列验证
	 * @return
	 */
	BuildAndCheckSequence<HeadTreeNode> bestSequenceForBuildAndCheck(List<List<HeadTreeNode>> comnineChunkTree,
			Object[] ac, ParserContextGenerator generator, ParserSequenceValidator validator);

	/**
	 * 得到最好的num个BuildAndCheck结果
	 * 
	 * @param num
	 *            最好的num个序列
	 * @param comnineChunkTree
	 *            chunk步得到的最好的K棵树合并之后
	 * @param ac
	 *            额外的信息
	 * @param min
	 *            得分最低的限制
	 * @param generator
	 *            特征生成器
	 * @param validator
	 *            序列验证
	 * @return
	 */
	BuildAndCheckSequence<HeadTreeNode>[] bestSequencesForBuildAndCheck(int num,
			List<List<HeadTreeNode>> comnineChunkTree, Object[] ac, double min, ParserContextGenerator generator,
			ParserSequenceValidator validator);

	/**
	 * 得到最好的num个BuildAndCheck结果
	 * 
	 * @param num
	 *            最好的num个序列
	 * @param comnineChunkTree
	 *            chunk步得到的最好的K棵树合并之后
	 * @param ac
	 *            额外的信息
	 * @param generator
	 *            特征生成器
	 * @param validator
	 *            序列验证
	 * @return
	 */
	BuildAndCheckSequence<HeadTreeNode>[] bestSequencesForBuildAndCheck(int num,
			List<List<HeadTreeNode>> comnineChunkTree, Object[] ac, ParserContextGenerator generator,
			ParserSequenceValidator validator);
}
