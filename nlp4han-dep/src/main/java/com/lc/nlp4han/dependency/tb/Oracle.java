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
public class Oracle
{
	private ClassificationModel model;
	private DependencyParseContextGenerator contextGenerator;

	public Oracle(File model, DependencyParseContextGenerator contextGenerator) throws IOException
	{
		this(new ModelWrapper(model).getModel(), contextGenerator);
	}

	public Oracle(ModelWrapper modelwrapper, DependencyParseContextGenerator contextGenerator)
	{
		this(modelwrapper.getModel(), contextGenerator);
	}

	public Oracle(ClassificationModel model, DependencyParseContextGenerator contextGenerator)
	{
		this.model = model;
		this.contextGenerator = contextGenerator;
	}

	public ActionType classify(Configuration conf)
	{// 将当前的Configuration分类
		// 利用训练得到模型去判断对当前的conf做怎样的操作
		// System.out.println(contextGenerator.getContext(conf).length);
		// for(String str:contextGenerator.getContext(conf))
		// System.out.println(str);
		
		String[] context = contextGenerator.getContext(conf);
		double allPredicates[] = model.eval(context);
		String tempAllType[] = new String[allPredicates.length];// 存储所有的分类
		
		for (int k = 0; k < allPredicates.length; k++)
		{
			tempAllType[k] = model.getOutcome(k);
		}
		
		int indexOfBestOutcome = getBestIndexOfOutcome(allPredicates);
		while(!SimpleValidator.validate(conf, tempAllType[indexOfBestOutcome])) {//ActionType不符合依存转换关系
			allPredicates[indexOfBestOutcome] = -1;
			indexOfBestOutcome = getBestIndexOfOutcome(allPredicates);
		}

		ActionType action = ActionType.toType(tempAllType[indexOfBestOutcome]);
		return action;
	}

	
	private int getBestIndexOfOutcome(double[] scores) {//ActionType不符合依存转换关系，索引best上的值重置为-1
	      int best = 0;
	      for (int i = 1; i<scores.length; i++)
	          if (scores[i] > scores[best]) best = i;
	      return best;
	}

}
