package com.lc.nlp4han.dependency;


import opennlp.tools.util.eval.EvaluationMonitor;

/**
 * 评估
 * @author 王馨苇
 *
 */
public class DependencyParseEvaluateMonitor implements EvaluationMonitor<DependencySample>{

	/**
	 * 预测正确的时候执行
	 * @param arg0 参考的结果
	 * @param arg1 预测的结果
	 */
	public void correctlyClassified(DependencySample arg0, DependencySample arg1) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 预测错误的时候执行
	 * @param arg0 参考的结果
	 * @param arg1 预测的结果
	 */
	public void missclassified(DependencySample arg0, DependencySample arg1) {
		// TODO Auto-generated method stub
		
	}

}
