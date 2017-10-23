package com.lc.nlp4han.pos.character;


import com.lc.nlp4han.pos.POSTagger;
import com.lc.nlp4han.pos.WordPOSMeasure;

import opennlp.tools.util.eval.Evaluator;

/**
 * 评估器
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class CharPOSEvaluator extends Evaluator<CharPOSSample>{

	private POSTagger tagger;
	private WordPOSMeasure measure;
	
	/**
	 * 构造
	 * @param tagger 训练得到的模型
	 */
	public CharPOSEvaluator(POSTagger tagger) {
		this.tagger = tagger;
	}
	
	/**
	 * 构造
	 * @param tagger 训练得到的模型
	 * @param evaluateMonitors 评估的监控管理器
	 */
	public CharPOSEvaluator(POSTagger tagger,CharPOSEvaluateMonitor... evaluateMonitors) {
		super(evaluateMonitors);
		this.tagger = tagger;
	}
	
	/**
	 * 设置评估指标的对象
	 * @param measure 评估指标计算的对象
	 */
	public void setMeasure(WordPOSMeasure measure){
		this.measure = measure;
	}
	
	/**
	 * 得到评估的指标
	 * @return
	 */
	public WordPOSMeasure getMeasure(){
		return this.measure;
	}
	
	/**
	 * 评估得到指标
	 * @param reference 参考的语料
	 */
	@Override
	protected CharPOSSample processSample(CharPOSSample reference) {
	    String[] wordsRef = reference.getWords();
        String[] posesRef = reference.getPoses();
        String[] charactersRef = reference.getCharacters();
        String[] tagsRef = reference.getTags();

		String[] posesPre = tagger.tag(wordsRef);
		measure.updateScores(wordsRef,posesRef,posesPre);
		
		CharPOSSample prediction = new CharPOSSample(charactersRef,tagsRef,wordsRef,posesPre);
		return prediction;
	}

}
