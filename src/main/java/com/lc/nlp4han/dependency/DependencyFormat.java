package com.lc.nlp4han.dependency;


/**
 * 依存样本格式解析
 * 
 * 策略模式上下文类
 * 
 * @author 王馨苇
 *
 */
public class DependencyFormat {

	private DependencySampleParser parser ;
	private DependencySample pas;
	private String sentence;
	
	/**
	 * 构造
	 * @param parser 样本解析器
	 * @param sentence 要解析的语句
	 */
	public DependencyFormat(DependencySampleParser parser,String sentence){
		this.parser = parser;
		this.sentence = sentence;
	}
	
	public DependencyFormat(DependencySampleParser parser,DependencySample pas){
		this.parser = parser;
		this.pas = pas;
	}
	
	/**
	 * 解析预料
	 * @return 解析后的样本流
	 */
	public DependencySample sampleParse(){
		return this.parser.parseIn(sentence);
	}
	
	/**
	 * 解析测试语料
	 */
	public DependencySample testParse(){
		return this.parser.parseTest(sentence);
	}
	
	/**
	 * 打印结果
	 */
	public void printRes(){
		
		this.parser.printPhraseAnalysisRes(pas);
	}
}
