package com.lc.nlp4han.ner;

/**
 * NER语料解析策略接口
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public interface NERParseStrategy {

	/**
	 * 为基于字的命名实体解析文本
	 * 
	 * @param sentence 原始/分词/词性标注的句子
	 * @return
	 */
	public NERWordOrCharacterSample parse(String sentence);
	
}
