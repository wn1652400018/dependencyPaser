package com.lc.nlp4han.dependency;


public interface DependencySampleParser{

	/**
	 * 从训练语料中读取的要解析的句子
	 * @param sentence 要解析的语句
	 * @return 解析的结果
	 */
	public DependencySample parseIn(String sentence);
	
	/**
	 * 从测试语料中读取的一行记录
	 * @param sentenceTest 要解析的语句
	 * @return 解析的结果
	 */
	public DependencySample parseTest(String sentenceTest);
	
	/**
	 * 打印
	 * @param pas 样本
	 */
	public void printPhraseAnalysisRes(DependencySample pas);

}
