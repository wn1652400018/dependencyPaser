package com.lc.nlp4han.segpos;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;


import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.TrainingParameters;
import opennlp.tools.util.eval.CrossValidationPartitioner;

/**
 * 交叉验证
 * @author 王馨苇
 *
 */
public class WordSegAndPosCrossValidation {

	private final TrainingParameters params;
	private WordSegAndPosEvaluateMonitor[] monitor;
	
	/**
	 * 构造
	 * @param languageCode 编码格式
	 * @param params 训练的参数
	 * @param listeners 监听器
	 */
	public WordSegAndPosCrossValidation(TrainingParameters params,WordSegAndPosEvaluateMonitor... monitor){
		this.params = params;
		this.monitor = monitor;
	}
	
	/**
	 * 交叉验证十折评估
	 * @param sample 样本流
	 * @param nFolds 折数
	 * @param contextGenerator 上下文
	 * @throws IOException io异常
	 */
	public void evaluate(ObjectStream<WordSegAndPosSample> sample, int nFolds,
			WordSegAndPosContextGenerator contextGenerator) throws IOException{
		CrossValidationPartitioner<WordSegAndPosSample> partitioner = new CrossValidationPartitioner<WordSegAndPosSample>(sample, nFolds);
		int run = 1;
		//小于折数的时候
		while(partitioner.hasNext()){
			System.out.println("Run"+run+"...");
			CrossValidationPartitioner.TrainingSampleStream<WordSegAndPosSample> trainingSampleStream = partitioner.next();
			//生成词语-词性词典
			HashMap<String,List<String>> dict = WordSegAndPosME.bulidDictionary(trainingSampleStream);
			//训练模型
			trainingSampleStream.reset();
			WordSegAndPosModel model = WordSegAndPosME.train("zh", trainingSampleStream, params, contextGenerator);

			WordSegAndPosEvaluator evaluator = new WordSegAndPosEvaluator(new WordSegAndPosME(model, contextGenerator), monitor);
			WordSegAndPosMeasure measure = new WordSegAndPosMeasure(dict);
			
			evaluator.setMeasure(measure);
	        //设置测试集（在测试集上进行评价）
	        evaluator.evaluate(trainingSampleStream.getTestSampleStream());
	        
	        System.out.println(measure);
	        run++;
		}
//		System.out.println(measure);
	}
}
