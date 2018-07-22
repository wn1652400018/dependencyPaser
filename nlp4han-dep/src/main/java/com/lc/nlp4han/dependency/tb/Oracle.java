package com.lc.nlp4han.dependency.tb;

import java.io.File;
import java.io.IOException;

import com.lc.nlp4han.ml.model.ClassificationModel;
import com.lc.nlp4han.ml.util.ModelWrapper;

/**
 * actionԤ����
 * 
 * TODO: 通过Configuration得到ActionType
 * 
 * @author 王宁
 *
 */
public class Oracle {
	private ClassificationModel model; 
	private DependencyParseContextGenerator contextGenerator;
	
	
	public Oracle(File model,DependencyParseContextGenerator contextGenerator) throws IOException {
		this(new ModelWrapper(model).getModel(),contextGenerator);
	}
	public Oracle(ModelWrapper modelwrapper,DependencyParseContextGenerator contextGenerator) {
		this(modelwrapper.getModel(),contextGenerator);
	}
	public Oracle(ClassificationModel model,DependencyParseContextGenerator contextGenerator) {
		this.model = model;
		this.contextGenerator = contextGenerator;
	}
	
	
	public ActionType classify(Configuration conf) {//将当前的Configuration分类
		//利用训练得到模型去判断对当前的conf做怎样的操作
//		System.out.println(contextGenerator.getContext(conf).length);
//		for(String str:contextGenerator.getContext(conf))
//			System.out.println(str);
		String StrOfType = bestOutcome(contextGenerator.getContext(conf));
		System.out.println(StrOfType);
		return ActionType.toType(StrOfType);
	}
	
	
	
	
	
	/**
	 * @param conf
	 * 				配置
	 * @return
	 * 				最可能的类型,  relation/baseAction
	 */				
	public String bestOutcome(Configuration conf) {
		String[] context = contextGenerator.getContext(conf);
		return bestOutcome(context);
	}
	
	/**
	 * @param context
	 *       		特征
	 * @return
	 * 				最可能的类型,  relation/baseAction
	 */
	public String bestOutcome(String[] context) {
		double allPredicates[] = model.eval(context);
		String tempAllType[] = new String[allPredicates.length];//存储所有的分类
	
		double max = -1;
		int record = -1;
		for (int k = 0; k < allPredicates.length; k++)
		{
			tempAllType[k] = model.getOutcome(k);
			if ((allPredicates[k] > max) )
			{
				max = allPredicates[k];
				record = k;
			}
		}
		// 根据最大的下标获取对应的依赖关系
		return tempAllType[record];
	}

}
