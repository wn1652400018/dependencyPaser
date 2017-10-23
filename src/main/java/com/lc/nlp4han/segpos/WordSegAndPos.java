package com.lc.nlp4han.segpos;

/**
 * 组合分词和词性标注接口
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public interface WordSegAndPos {

	/**
	 * 对生语料进行分词和词性标记
	 * @param words 未切分的句子
	 * @return word/tag序列
	 */
	public String[] segmentAndTag(String words);
	
	
	/**
	 * 对生语料进行分词
	 * @param sentence 未切分的句子
	 * @return 切分后的词序列
	 */
	public String[] segment(String sentence);
	
}
