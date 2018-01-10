package com.lc.nlp4han.dependency;

import com.lc.nlp4han.ml.util.Evaluator;

/**
 * 评估句法分析器的性能【此时句法树中的关系无Null】
 * @author 王馨苇
 *
 */
public class DependencyParseEvaluatorHaveNull extends Evaluator<DependencySample>{

	private int forestcount = 0;
	private DependencyParserME tagger;
	private DependencyParseMeasure measure;
//	private DependencyParsingCount count;

	/**
	 * 构造
	 * @param tagger 模型和特征的组合结果
	 * @param monitors 评估的监听管理
	 */
	public DependencyParseEvaluatorHaveNull(DependencyParserME tagger,DependencyParseEvaluateMonitor... monitors){
		super(monitors);
		this.tagger = tagger;
	}
	/**
	 * 构造
	 * @param tagger 模型和特征的组合结果
	 */
	public DependencyParseEvaluatorHaveNull(DependencyParserME tagger){
		this.tagger = tagger;
	}
	/**
	 * 加载用于计算各种指标的类
	 * @param measure DependencyParsingMeasure对象
	 */
	public void setMeasure(DependencyParseMeasure measure){
		this.measure = measure;
	}
	
	/**
	 * 获取评价指标
	 * @return 评价指标
	 */
	public DependencyParseMeasure getMeasure(){
		return measure;
	}
	
	/**
	 * 获取森林的个数
	 * @return 森林的个数
	 */
	public int getForestCount(){
		return this.forestcount;
	}
	
//	/**
//	 * 设置用于统计语料信息的类
//	 * @param count 计算语料信息的类DependencyParsingCount的对象
//	 */
//	public void setCount(DependencyParsingCount count){
//		this.count = count;
//	}
//	
//	/**
//	 * 获取统计的语料的信息
//	 * @return 语料的信息
//	 */
//	public DependencyParsingCount getCount(){
//		return this.count;
//	}

	/**
	 * 调用evaluate方法时候自动激活执行，用于生成预测结果，并和参考的结果进行对比评估指标
	 * @param sample 样本流
	 */
	@Override
	protected DependencySample processSample(DependencySample sample) {
		String[] wordsRef = sample.getWords();
		String[] posRef = sample.getPos();
		String[] dependencyRef = sample.getDependency();
		String[] dependencyWordsRef = sample.getDependencyWords();
		
		DependencyParseMatrix proba = tagger.tagNull(wordsRef, posRef, sample.getAditionalContext());
		sample = MaxSpanningTree.getMaxTree(proba);
		String[] dependencyWordsPre = sample.getDependencyWords();
		String[] dependencyPre = sample.getDependency();
		String[] dependencyIndicesPre = sample.getDependencyIndices();
//		PhraseAnalysisContext context = new PhraseAnalysisContext(new PhraseAnalysisDependencySample(), sample);
//		context.printRes();
			
		boolean forest = IsHaveForestOrLoop.isHaveForest(wordsRef,dependencyIndicesPre);
		if(forest){
			forestcount++;
		}
//		System.out.println("森林的数量是："+forest);
		measure.updateScore(dependencyWordsRef,dependencyRef,dependencyWordsPre,dependencyPre);
		DependencySample samplePre = new DependencySample(wordsRef, posRef, dependencyPre, dependencyWordsPre, dependencyIndicesPre);
		
		return samplePre;
	}

}
