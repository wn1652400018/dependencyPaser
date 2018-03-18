package com.lc.nlp4han.dependency;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

/**
 * 解析Stanford语料的单元测试
 * 测试解析后样本输出为CoNLL格式、输出为Stanford样式
 * 
 * @author 王馨苇
 *
 */
public class DependencySampleParserStanfordTest {

	private String corpus;
	private DependencySampleParser parser;
	private DependencySample sample;
	
	@Before
	public void setUp(){
		corpus =
				"核心成分(核心-0,惩治-2)" + "\n" +
				"方式(惩治-2,坚决-1)" + "\n" +
				"限定(犯罪-7,贪污-3)" + "\n" +
				"连接依存(贪污-3,贿赂-4)" + "\n" +
				"连接依存(贪污-3,等-5)" + "\n" +
				"限定(犯罪-7,经济-6)" + "\n" +
				"受事(惩治-2,犯罪-7)" + "\n" ;

		parser = new DependencySampleParserStanford();
		sample = parser.parse(corpus);
	}
	
	/**
	 * 测试输出为CoNLL格式
	 */
	@Test
	public void testStanford(){
		assertEquals(corpus, sample.toStanfordSample());
	}
	
	/**
	 * 输出为Stanford样式
	 */
	@Test
	public void testCoNLL(){
		String result = 
				"1	坚决	坚决	_	2	方式	_	_" + "\n" +
			    "2	惩治	惩治	_	0	核心成分	_	_" + "\n" +
			    "3	贪污	贪污	_	7	限定	_	_" + "\n" +
			    "4	贿赂	贿赂	_	3	连接依存	_	_" + "\n" +
			    "5	等	等	_	3	连接依存	_	_" + "\n" +
			    "6	经济	经济	_	7	限定	_	_" + "\n" +
			    "7	犯罪	犯罪	_	2	受事	_	_" + "\n" ;
		assertEquals(result, sample.toCoNLLSample());
	}
}
