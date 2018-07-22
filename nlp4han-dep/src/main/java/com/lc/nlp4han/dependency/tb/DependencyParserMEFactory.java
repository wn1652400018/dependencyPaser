package com.lc.nlp4han.dependency.tb;

import java.io.File;
import java.io.IOException;

import com.lc.nlp4han.ml.util.ModelWrapper;

/**
* @author 王宁
* @version 创建时间：2018年7月22日 下午5:40:18
* 用来创建DependencyParserMe类的实例
*/
public class DependencyParserMEFactory
{
	
	
	public static DependencyParserME getDependencyParser() throws IOException {
		ModelWrapper modelWrapper = new ModelWrapper(new File("C:\\Users\\hp\\Desktop\\tb.model"));
		return new DependencyParserME(modelWrapper);
	}
	
	
	public static void main(String[] args)
	{
		try
		{
			DependencyParserME dpME =  DependencyParserMEFactory.getDependencyParser();
		    String [] words = {"世界","第","八","大","奇迹","出现"};
		    String [] poses = {"n","m","m","a","n","v"};
		    dpME.parse(words, poses);
		
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		
	}

}
