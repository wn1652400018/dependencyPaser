package com.lc.nlp4han.dependency.tb;

import java.io.IOException;
import java.io.InputStream;

import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.dependency.mst.DependencyParserME;

/**
* @author 作者
* @version 创建时间：2018年7月23日 上午1:20:42
* 类说明
*/
public class TestMST
{

	public static void main(String[] args)
	{
		
		try
		{
			InputStream modelIn = TestMST.class.getClassLoader().getResourceAsStream("com/lc/nlp4han/dependency/mst.model");	    
			ModelWrapper modelWrapper;
			modelWrapper = new ModelWrapper(modelIn);
			DependencyParserME dp = new DependencyParserME(modelWrapper);
			String [] words = {"世界","最","先进","的","寺庙","建成"};		   
			String [] poses = {"n","d","a","u","n","v"};
			String str = dp.parse(words,poses).getSample().toCoNLLString();
			System.out.println(str);
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	    
	}

}
