package com.lc.nlp4han.constituent.maxent;

import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.ml.util.EvaluationMonitor;

/**
 * 评估的监测类
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisEvaluateMonitor implements EvaluationMonitor<ConstituentTreeSample<HeadTreeNode>>{

	/**
	 * 预测正确的时候执行
	 * @param arg0 参考的结果
	 * @param arg1 预测的结果
	 */
	@Override
	public void correctlyClassified(ConstituentTreeSample<HeadTreeNode> arg0, ConstituentTreeSample<HeadTreeNode> arg1) {
		
	}

	/**
	 * 预测正确的时候执行
	 * @param arg0 参考的结果
	 * @param arg1 预测的结果
	 */
	@Override
	public void missclassified(ConstituentTreeSample<HeadTreeNode> arg0, ConstituentTreeSample<HeadTreeNode> arg1) {
		
	}

}
