package com.lc.nlp4han.ner;

import com.lc.nlp4han.ml.util.EvaluationMonitor;

public class NEREvaluateMonitor implements EvaluationMonitor<NERWordOrCharacterSample>{

	/**
	 * 预测正确的时候执行
	 * @param ref 参考的结果
	 * @param pre 预测的结果
	 */
	public void correctlyClassified(NERWordOrCharacterSample ref, NERWordOrCharacterSample pre) {
		
	}

	/**
	 * 预测正确的时候执行
	 * @param ref 参考的结果
	 * @param pre 预测的结果
	 */
	public void missclassified(NERWordOrCharacterSample ref, NERWordOrCharacterSample pre) {
		
	}

}
