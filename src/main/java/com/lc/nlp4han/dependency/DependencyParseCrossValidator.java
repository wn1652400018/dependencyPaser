package com.lc.nlp4han.dependency;

import java.io.IOException;

import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.eval.CrossValidationPartitioner;

/**
 * 交叉验证
 * @author 王馨苇
 *
 */
public class DependencyParseCrossValidator {
	
	private final String languageCode;
	private final TrainingParameters params;
	private DependencyParseEvaluateMonitor[] listeners;
	private DependencyParseMeasure measure = new DependencyParseMeasure();
//	private DependencyParsingCount count = new DependencyParsingCount();
	
	/**
	 * 构造
	 * @param languageCode 编码格式
	 * @param params 训练的参数
	 * @param listeners 监听器
	 */
	public DependencyParseCrossValidator(String languageCode,TrainingParameters params,
			DependencyParseEvaluateMonitor... listeners){
		this.languageCode = languageCode;
		this.params = params;
		this.listeners = listeners;
	}
	
	/**
	 * 交叉验证十折评估
	 * @param sample 样本流
	 * @param nFolds 折数
	 * @param contextGenerator 上下文
	 * @throws IOException io异常
	 */
	public void evaluate(ObjectStream<DependencySample> sample, int nFolds,
			DependencyParseContextGenerator contextGenerator) throws IOException{
		CrossValidationPartitioner<DependencySample> partitioner = new CrossValidationPartitioner<DependencySample>(sample, nFolds);
		int run = 1;
		//小于折数的时候
		while(partitioner.hasNext()){
			System.out.println("Run"+run+"...");
			CrossValidationPartitioner.TrainingSampleStream<DependencySample> trainingSampleStream = partitioner.next();
	        DependencyParseModel model = DependencyParserME.train(languageCode, trainingSampleStream, params, contextGenerator);

	        DependencyParseEvaluatorNoNull evaluator = new DependencyParseEvaluatorNoNull(new DependencyParserME(model, contextGenerator), listeners);
//	        evaluator.setCount(count);
	        evaluator.setMeasure(measure);
	        //设置测试集（在测试集上进行评价）
	        evaluator.evaluate(trainingSampleStream.getTestSampleStream());
	        
	        System.out.println(measure);
	        run++;
		}
//		System.out.println(count);
		System.out.println(measure);
	}
}
