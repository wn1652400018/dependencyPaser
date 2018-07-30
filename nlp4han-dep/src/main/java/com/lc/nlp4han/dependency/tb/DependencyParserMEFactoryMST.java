package com.lc.nlp4han.dependency.tb;

import java.io.IOException;
import java.io.InputStream;

import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.dependency.DependencyTree;
import com.lc.nlp4han.dependency.mst.DependencyParserME;
/**
* @author 作者
* @version 创建时间：2018年7月29日 下午8:01:25
* 类说明
*/
public class DependencyParserMEFactoryMST
{
	public static DependencyParserME getDependencyParser() throws IOException {
		InputStream modelIn = DependencyParserMEFactory.class.getClassLoader().getResourceAsStream("com/lc/nlp4han/dependency/mst.model");
	    ModelWrapper modelWrapper = new ModelWrapper(modelIn);
	    return new DependencyParserME(modelWrapper);
	}
	
	
	public static void main(String[] args)
	{
			DependencyParserME dpME;
			try
			{
				dpME = DependencyParserMEFactoryMST.getDependencyParser();
				
//				String [] words = {"世界","最","先进","的","清真寺","落成"};		   
//				String [] poses = {"n","d","a","u","n","v"};	
//				String [] words = {"化作","电波","传","向","世界","各个","角落"};		   
//				String [] poses = {"v","n","v","p","n","r","n"};	
				
				String [] words = {"给","庄重","的","清真寺","平","添","了","几分","生机"};		   
				String [] poses = {"p","a","u","n","v","v","u","m","n"};
				DependencyTree depTree = dpME.parse(words, poses);
				System.out.println(depTree.getSample().toCoNLLString());
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
	}
}
