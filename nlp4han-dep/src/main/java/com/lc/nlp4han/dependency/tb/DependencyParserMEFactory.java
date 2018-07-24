package com.lc.nlp4han.dependency.tb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.lc.nlp4han.dependency.DependencyTree;
import com.lc.nlp4han.ml.util.ModelWrapper;

/**
* @author 王宁
* @version 创建时间：2018年7月22日 下午5:40:18
* 用来创建DependencyParserMe类的实例
*/
public class DependencyParserMEFactory
{
	
	
	public static DependencyParserME getDependencyParser() throws IOException {
		InputStream modelIn = DependencyParserMEFactory.class.getClassLoader().getResourceAsStream("com/lc/nlp4han/dependency/tb_postag.model");
	    ModelWrapper modelWrapper = new ModelWrapper(modelIn);
		return new DependencyParserME(modelWrapper);
	}
	
	
	public static void main(String[] args)
	{
			DependencyParserME dpME;
			try
			{
				dpME = DependencyParserMEFactory.getDependencyParser();
				String [] words = {"世界","第","八","大","奇迹","出现"};		   
				String [] poses = {"n","m","m","a","n","v"};	
				DependencyTree depTree = dpME.parse(words, poses);
				System.out.println(depTree.getSample().toCoNLLString());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
	}
}

//String [] words = {"世界","最","先进","的","清真寺","落成"};		   
//String [] poses = {"n","d","a","u","n","v"};	
////String [] poses = {"n","d","a","ude1","n","vi"};

//1	世界	世界	n	n	_	5	限定	
//
//2	最	最	d	d	_	3	程度	
//
//3	先进	先进	a	a	_	5	描述	
//
//4	的	的	u	ude1	_	3	“的”字依存	
//
//5	清真寺	清真寺	n	n	_	6	经验者	
//
//6	落成	落成	v	vi	_	0	核心成分

//粗粒度

//null/SHIFT
//null/SHIFT
//程度/LEFTARC_REDUCE
//处所/LEFTARC_REDUCE
//null/SHIFT
//“的”字依存/RIGHTARC_SHIFT
//null/REDUCE
//限定/LEFTARC_REDUCE
//null/SHIFT
//受事/LEFTARC_REDUCE
//核心成分/RIGHTARC_SHIFT

//细粒度

//null/SHIFT
//null/SHIFT
//程度/LEFTARC_REDUCE
//处所/LEFTARC_REDUCE
//null/SHIFT
//“的”字依存/RIGHTARC_SHIFT
//null/REDUCE
//限定/LEFTARC_REDUCE
//null/SHIFT
//经验者/LEFTARC_REDUCE
//核心成分/RIGHTARC_SHIFT


//1	世界	世界	null	null	_	3	处所	_	_
//2	最	最	null	null	_	3	程度	_	_
//3	先进	先进	null	null	_	5	限定	_	_
//4	的	的	null	null	_	3	“的”字依存	_	_
//5	清真寺	清真寺	null	null	_	6	受事	_	_
//6	落成	落成	null	null	_	0	EXTRAROOT	_	_
