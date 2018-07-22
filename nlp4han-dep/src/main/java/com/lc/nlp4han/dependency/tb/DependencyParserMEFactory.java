package com.lc.nlp4han.dependency.tb;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

import com.lc.nlp4han.ml.util.ModelWrapper;

/**
* @author 王宁
* @version 创建时间：2018年7月22日 下午5:40:18
* 用来创建DependencyParserMe类的实例
*/
public class DependencyParserMEFactory
{
	
	
	public static DependencyParserME getDependencyParser() throws IOException {
		InputStream modelIn = DependencyParserMEFactory.class.getClassLoader().getResourceAsStream("com/lc/nlp4han/dependency/tb.model");
	    ModelWrapper modelWrapper = new ModelWrapper(modelIn);
		return new DependencyParserME(modelWrapper);
	}
	
	
	public static void main(String[] args)
	{
		
			DependencyParserME dpME;
			try
			{
				dpME = DependencyParserMEFactory.getDependencyParser();
				String [] words = {"世界","最","先进","的","寺庙","建成"};		   
				String [] poses = {"n","d","a","u","n","v"};	    
				dpME.parse(words, poses);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		    
		
		
		
	}

}
