package com.lc.nlp4han.dependency.tb;

/**
 * 特征生成的接口
 * 
 * @author 王宁
 * 
 *
 */
public interface DependencyParseContextGenerator{

	/**
	 * 获取特征
	 * 
	 * @param conf arceager过程中的配置
	 * 
	 * @return
	 */
	 public String[] getContext(Configuration conf);
}
