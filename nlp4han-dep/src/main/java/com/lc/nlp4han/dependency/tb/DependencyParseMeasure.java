package com.lc.nlp4han.dependency.tb;

/**
 * @author 王宁
 * @version 创建时间：2018年7月24日 下午6:23:49 依存句法分析结果的评价指标，所有的统计均不会统计认为添加的“核心”
 *          以下所指的“根”是指整个原始句子的中心词，“标记”是指改词与中心词的具体依存关系
 */
public class DependencyParseMeasure
{

	// 所有词的个数，也就是句子的长度，含原始句子的中心词
	private double countAllWords = 0;

	// 标记和中心词都正确的词的个数
	private double countWordsAndDep = 0;

	// 中心词正确的词的个数
	private double countWords = 0;

	// 中心词正确的非根词的个数
	private double countWordsAndDepNotRoot = 0;

	// 非根总词数
	private double countAllWordsNotRoot = 0;

	// 句子数
	private double countSentence = 0;

	// 中心词和标记正确的根的个数
	private double countWordsAndDepRoot = 0;

	// 整个句子的所有词的中心词都正确的句子个数
	private double countAllDependency = 0;

	// 整个句子的所有词的标记和中心词都正确的句子个数
	private double countAllWordsAndDep = 0;

	/**
	 * 输出打印的格式
	 */
	@Override
	public String toString()
	{
		return "UAS:" + getUAS() + "\n" + "LAS:" + getLAS() + "\n" + "DA:" + getDA() + "\n" + "RA:" + getRA() + "\n"
				+ "CM:" + getCM() + "\n" + "CMS':" + getCMS() + "\n";
	}

	/**
	 * 统计正确的依赖词数，和正确的依赖词和依赖关系数
	 * 
	 * @param dependencyWordsRef
	 *            参考依存词
	 * @param dependencyRef
	 *            参考依存关系
	 * @param dependencyWordsPre
	 *            预测依存词
	 * @param dependencyPre
	 *            预测依存关系
	 */
	public void updateScore(String[] dependencyWordsRef, String[] dependencyRef, String[] dependencyWordsPre,
			String[] dependencyPre)
	{
		int dependencyCount = 0;
		int wordsAndDepCount = 0;

		countSentence++;
		countAllWords += dependencyPre.length;
		for (int i = 0; i < dependencyPre.length; i++)
		{
			if (dependencyPre[i].compareTo("ROOT") != 0 || dependencyPre[i].compareTo("核心成分") != 0)
			{
				countAllWordsNotRoot++;
			}
		}

		for (int i = 0; i < dependencyPre.length; i++)
		{
			// 无标记匹配
			if (dependencyWordsPre[i].compareTo(dependencyWordsRef[i]) == 0)
			{
				countWords++;
				dependencyCount++;
			} // 带标记匹配
			if ((dependencyWordsPre[i].compareTo(dependencyWordsRef[i]) == 0)
					&& (dependencyPre[i].compareTo(dependencyRef[i]) == 0))
			{
				countWordsAndDep++;
				wordsAndDepCount++;
			}

			if ((dependencyWordsPre[i].compareTo(dependencyWordsRef[i]) == 0)
					&& (dependencyPre[i].compareTo(dependencyRef[i]) == 0)
					&& (dependencyPre[i].compareTo("ROOT") != 0 || dependencyPre[i].compareTo("核心成分") != 0))
			{
				countWordsAndDepNotRoot++;
			}

			if ((dependencyWordsPre[i].compareTo(dependencyWordsRef[i]) == 0)
					&& (dependencyPre[i].compareTo(dependencyRef[i]) == 0)
					&& ((dependencyPre[i].compareTo("ROOT") == 0) || dependencyPre[i].compareTo("核心成分") == 0))
			{
				countWordsAndDepRoot++;
			}

		}

		if (dependencyCount == dependencyWordsPre.length)
		{
			countAllDependency++;
		}

		if (wordsAndDepCount == dependencyPre.length)
		{
			countAllWordsAndDep++;
		}

	}

	/**
	 * 无标记依存正确率
	 * 
	 * @return
	 */
	public double getUAS()
	{
		return countWords / countAllWords;
	}

	/**
	 * 带标记正确依存率
	 * 
	 * @return
	 */
	public double getLAS()
	{
		return countWordsAndDep / countAllWords;
	}

	/**
	 * 非根依存正确率(带标记)
	 * 
	 * @return
	 */
	public double getDA()
	{
		return countWordsAndDepNotRoot / countAllWordsNotRoot;
	}

	/**
	 * 根正确率(带标记)
	 * 
	 * @return
	 */
	public double getRA()
	{
		return countWordsAndDepRoot / countSentence;
	}

	/**
	 * 完全匹配率(不带标记)
	 * 
	 * @return
	 */
	public double getCM()
	{
		return countAllDependency / countSentence;
	}

	/**
	 * 改进的CM指标(带标记)
	 * 
	 * @return
	 */
	public double getCMS()
	{
		return countAllWordsAndDep / countSentence;
	}

}