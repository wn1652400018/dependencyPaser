package com.lc.nlp4han.dependency;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;

import com.lc.nlp4han.ml.model.ClassificationModel;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.model.SequenceClassificationModel;
import com.lc.nlp4han.ml.util.BeamSearch;
import com.lc.nlp4han.ml.util.EventTrainer;
import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.TrainerFactory;
import com.lc.nlp4han.ml.util.TrainerFactory.TrainerType;
import com.lc.nlp4han.ml.util.TrainingParameters;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;

/**
 * 训练模型
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class DependencyParserME implements DependencyParser {

	public static final int DEFAULT_BEAM_SIZE = 3;

	private DependencyParseContextGenerator contextGenerator;

	private ClassificationModel mm;

	public DependencyParserME() {

	}

	/**
	 * 构造函数，初始化工作
	 * 
	 * @param model
	 *            模型
	 * @param contextGen
	 *            特征
	 */
	public DependencyParserME(ModelWrapper model, DependencyParseContextGenerator contextGen) {
		init(model, contextGen);
	}

	/**
	 * 初始化工作
	 * 
	 * @param model
	 *            模型
	 * @param contextGen
	 *            特征
	 */
	private void init(ModelWrapper model, DependencyParseContextGenerator contextGen) {
		int beamSize = DependencyParserME.DEFAULT_BEAM_SIZE;


		mm = model.getModel();

		contextGenerator = contextGen;
	}

	/**
	 * 训练模型并输出
	 * 
	 * @param file
	 *            训练语料的文件
	 * @param modelbinaryFile
	 *            持久化模型文件，二进制
	 * @param modeltxtFile
	 *            输出模型文件
	 * @param params
	 *            训练参数
	 * @param contextGen
	 *            特征
	 * @param encoding
	 *            编码
	 * @return PhraseAnalysisModel类，包装模型的信息
	 * @throws IOException
	 */
	public static ModelWrapper train(File file, File modelbinaryFile, File modeltxtFile,
			TrainingParameters params, DependencyParseContextGenerator contextGen, String encoding) throws IOException {
		ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(file), encoding);
		DependencySampleParser sampleParser = new DependencySampleParserCoNLL();
		ObjectStream<DependencySample> sampleStream = new DependencySampleStream(lineStream, sampleParser);
		ModelWrapper model = DependencyParserME.train("zh", sampleStream, params, contextGen);
		return model;

	}

	/**
	 * 训练模型
	 * 
	 * @param file
	 *            训练文件
	 * @param params
	 *            训练参数
	 * @param contextGen
	 *            特征
	 * @param encoding
	 *            编码
	 * @return 模型和模型信息的包裹结果
	 * @throws IOException
	 */
	public static ModelWrapper train(File file, TrainingParameters params,
			DependencyParseContextGenerator contextGen, String encoding) throws IOException {
		ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(file), encoding);
		DependencySampleParser sampleParser = new DependencySampleParserCoNLL();
		ObjectStream<DependencySample> sampleStream = new DependencySampleStream(lineStream, sampleParser);
		ModelWrapper model = DependencyParserME.train("zh", sampleStream, params, contextGen);
		return model;
	}

	/**
	 * 训练模型
	 * 
	 * @param languageCode
	 *            编码
	 * @param sampleStream
	 *            样本流
	 * @param params
	 *            训练参数
	 * @param contextGen
	 *            特征
	 * @return 模型
	 * @throws IOException
	 *             IO异常
	 */
	public static ModelWrapper train(String languageCode, ObjectStream<DependencySample> sampleStream,
			TrainingParameters params, DependencyParseContextGenerator contextGen) throws IOException {
		// beamSizeString为空
		String beamSizeString = params.getSettings().get(BeamSearch.BEAM_SIZE_PARAMETER);

		int beamSize = DependencyParserME.DEFAULT_BEAM_SIZE;
		if (beamSizeString != null) {
			beamSize = Integer.parseInt(beamSizeString);
		}
		ClassificationModel maxentModel = null;

		Map<String, String> manifestInfoEntries = new HashMap<String, String>();
		TrainerType trainerType = TrainerFactory.getTrainerType(params.getSettings());
		SequenceClassificationModel<String> seqPosModel = null;
		if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType)) {
			ObjectStream<Event> es = new DependencySampleEventStream(sampleStream, contextGen);
			EventTrainer trainer = TrainerFactory.getEventTrainer(params.getSettings(), manifestInfoEntries);
			maxentModel = trainer.train(es);
		}

		if (maxentModel != null) {
			return new ModelWrapper(maxentModel, beamSize);
		} else {
			return new ModelWrapper(seqPosModel);
		}
	}

	/**
	 * 获得最大概率与最大概率对应的依赖标记，为最大生成树做准备
	 * 
	 * @param sentence
	 *            语句
	 * @param pos
	 *            词性标注
	 * @param additionaContext
	 *            额外的信息
	 * @return 对象包装概率和依存关系的信息
	 */
	public DependencyParseMatrix tagNull(String[] sentence, String[] pos, Object[] additionaContext) {
		int i = 1, j = 0;
		double[][] proba = new double[sentence.length][sentence.length];
		String[][] dependencyRelation = new String[sentence.length][sentence.length];
		for (int m = 0; m < sentence.length; m++) {
			for (int n = 0; n < sentence.length; n++) {
				proba[m][n] = 0.0;
				dependencyRelation[m][n] = "null";
			}
		}
		int lenLeft, lenRight;
		if (DependencyParseContextGeneratorConf.LEFT == -1 && DependencyParseContextGeneratorConf.RIGHT == -1) {
			lenLeft = -sentence.length;
			lenRight = sentence.length;
		} else {
			lenLeft = DependencyParseContextGeneratorConf.LEFT;
			lenRight = DependencyParseContextGeneratorConf.RIGHT;
		}
		// if()
		while (i < sentence.length) {
			while (j < sentence.length) {
				// while(j - i <= lenRight && j - i >= lenLeft && j < sentence.length){
				if (i != j) {
					String[] context = contextGenerator.getContext(i, j, sentence, pos, additionaContext);
					double temp[] = mm.eval(context);
					String str = mm.getBestOutcome(temp);
					// System.out.println(mm.getAllOutcomes(temp));
					// for (int k = 0; k < temp.length; k++) {
					// System.out.println(temp[k]+":"+mm.getOutcome(k));
					// }

					Arrays.sort(temp);
					// 根据最大的下标获取对应的依赖关系
					dependencyRelation[i][j] = str;
					// 最大的概率
					proba[i][j] = temp[temp.length - 1];
					// System.out.println(sentence[i]+" "+sentence[j]+":"+str);
				}
				j++;
			}
			i++;
			j = 0;
		}

		return new DependencyParseMatrix(sentence, pos, proba, dependencyRelation);
	}

	/**
	 * 获得关系非null的最大概率及其对应的关系
	 * 
	 * @param sentence
	 *            词语
	 * @param pos
	 *            词性
	 * @param additionaContext
	 *            额外的信息
	 * @return 对象包装概率和依存关系的信息
	 */
	public DependencyParseMatrix tagNoNull(String[] sentence, String[] pos, Object[] additionaContext) {
		int i = 1, j = 0;
		double[][] proba = new double[sentence.length][sentence.length];
		String[][] dependencyRelation = new String[sentence.length][sentence.length];
		for (int m = 0; m < sentence.length; m++) {
			for (int n = 0; n < sentence.length; n++) {
				proba[m][n] = 0.0;
				dependencyRelation[m][n] = "null";
			}
		}

		int lenLeft, lenRight;
		if (DependencyParseContextGeneratorConf.LEFT == -1 && DependencyParseContextGeneratorConf.RIGHT == -1) {
			lenLeft = -sentence.length;
			lenRight = sentence.length;
		} else {
			lenLeft = DependencyParseContextGeneratorConf.LEFT;
			lenRight = DependencyParseContextGeneratorConf.RIGHT;
		}
		// if()
		while (i < sentence.length) {
			// while(j < sentence.length){
			while (j - i <= lenRight && j - i >= lenLeft && j < sentence.length) {
				if (i != j) {
					String[] context = contextGenerator.getContext(i, j, sentence, pos, additionaContext);
					double temp[] = mm.eval(context);
					String tempDependency[] = new String[temp.length];
					for (int k = 0; k < temp.length; k++) {
						tempDependency[k] = mm.getOutcome(k);
					}
					double max = -1;
					int record = -1;
					for (int k = 0; k < temp.length; k++) {
						if ((temp[k] > max) && (tempDependency[k].compareTo("null") != 0)) {
							max = temp[k];
							record = k;
						}
					}
					// 根据最大的下标获取对应的依赖关系
					dependencyRelation[i][j] = tempDependency[record];
					// 最大的概率
					proba[i][j] = temp[record];
				}
				j++;
			}
			i++;
			j = 0;
		}

		return new DependencyParseMatrix(sentence, pos, proba, dependencyRelation);
	}

	/**
	 * 获得关系非null的最大K个概率及其对应的关系
	 * 
	 * @param kRes
	 *            获得的最好的结果的个数
	 * @param sentence
	 *            词语
	 * @param pos
	 *            词性
	 * @param additionaContext
	 *            额外的信息
	 * @return 对象包装概率和依存关系的信息
	 */
	public DependencyParseMatrix tagK(int kRes, String[] sentence, String[] pos, Object[] additionaContext) {

		int i = 1, j = 0;
		String[][] proba = new String[sentence.length][sentence.length];
		String[][] dependencyRelation = new String[sentence.length][sentence.length];
		// for (int m = 0; m < sentence.length; m++) {
		// for (int n = 0; n < sentence.length; n++) {
		// proba[m][n] = 0.0;
		// dependencyRelation[m][n] = "null";
		// }
		// }
		int lenLeft, lenRight;
		if (DependencyParseContextGeneratorConf.LEFT == -1 && DependencyParseContextGeneratorConf.RIGHT == -1) {
			lenLeft = -sentence.length;
			lenRight = sentence.length;
		} else if (DependencyParseContextGeneratorConf.LEFT == -1 && DependencyParseContextGeneratorConf.RIGHT != -1) {
			lenLeft = -sentence.length;
			lenRight = DependencyParseContextGeneratorConf.RIGHT;
		} else if (DependencyParseContextGeneratorConf.LEFT != -1 && DependencyParseContextGeneratorConf.RIGHT == -1) {
			lenLeft = DependencyParseContextGeneratorConf.LEFT;
			lenRight = sentence.length;
		} else {
			lenLeft = DependencyParseContextGeneratorConf.LEFT;
			lenRight = DependencyParseContextGeneratorConf.RIGHT;
		}

		while (i < sentence.length) {
			// while(j < sentence.length){
			while (j - i <= lenRight && j - i >= lenLeft && j < sentence.length) {
				if (i != j) {
					Queue<Data> queue = new PriorityQueue<>();
					String[] context = contextGenerator.getContext(i, j, sentence, pos, additionaContext);
					double temp[] = mm.eval(context);
					String tempDependency[] = new String[temp.length];
					for (int k = 0; k < temp.length; k++) {
						if (mm.getOutcome(k).compareTo("null") != 0) {
							tempDependency[k] = mm.getOutcome(k);
							queue.add(new Data(temp[k], k));
						}
					}
					String tempProba = "";
					String tempDepen = "";
					for (int k = 0; k < kRes; k++) {
						Data data = queue.poll();
						if (k == kRes - 1) {
							tempProba += data.getValue();
							tempDepen += tempDependency[data.getIndex()];
						} else {
							tempProba += data.getValue() + "_";
							tempDepen += tempDependency[data.getIndex()] + "_";
						}
					}
					// 根据最大的下标获取对应的依赖关系
					dependencyRelation[i][j] = tempDepen;
					// 最大的概率
					proba[i][j] = tempProba;
				}
				j++;
			}
			i++;
			j = 0;
		}

		return new DependencyParseMatrix(sentence, pos, proba, dependencyRelation);
	}

	/**
	 * 获得关系非null的最大概率及其对应的关系
	 * 
	 * @param sentence
	 *            词语
	 * @param pos
	 *            词性
	 * @return 对象包装概率和依存关系的信息
	 */
	public DependencyParseMatrix tagNoNull(String[] sentence, String[] pos) {

		return tagNoNull(sentence, pos, null);
	}

	/**
	 * 获得关系非null的最大K个概率及其对应的关系
	 * 
	 * @param k
	 *            获得的最好的结果的个数
	 * @param sentence
	 *            词语
	 * @param pos
	 *            词性
	 * @return 对象包装概率和依存关系的信息
	 */
	public DependencyParseMatrix tagK(int k, String[] sentence, String[] pos) {

		return tagK(k, sentence, pos, null);
	}

	/**
	 * 得到依存分析后的输出的样本样式
	 * 
	 * @param sentence
	 *            分词+词性标注之后的句子
	 */
	@Override
	public DependencyTree parse(String sentence) {
		String[] wordsandposes = sentence.split("\\s+");
		String[] words = new String[wordsandposes.length];
		String[] poses = new String[wordsandposes.length];

		//假设词语和词性之间的/分割的
		for (int i = 0; i < wordsandposes.length; i++) {
			String[] temp = wordsandposes[i].split("/");

			words[i] = temp[0];
			poses[i] = temp[1];
		}
		
		return parse(words, poses);
	}

	/**
	 * 解析语句得到依存分析的结果
	 * 
	 * @param words
	 *            分词之后的词语
	 * @param poses
	 *            词性标记
	 * @return 依存分析之后的结果
	 */
	@Override
	public DependencyTree parse(String[] words, String[] poses) {
		List<String> wordslist = Arrays.asList(words);
		List<String> allwords = new ArrayList<>();
		allwords.add(0, "核心");
		allwords.addAll(wordslist);
		List<String> poseslist = Arrays.asList(poses);
		List<String> allposes = new ArrayList<>();
		allposes.add(0, "root");
		allposes.addAll(poseslist);
		
		DependencyParseMatrix proba = tagNoNull(allwords.toArray(new String[allwords.size()]),
				allposes.toArray(new String[allposes.size()]));
		
		DependencySample sample;
		sample = MaxSpanningTree.getMaxTree(proba);
		DependencyTree parse = new DependencyTree(sample);
		
		return parse;
	}

	/**
	 * 解析语句得到依存分析的结果
	 * 
	 * @param wordsandposes
	 *            分词+词性标记的词语组成的数组
	 * @return 依存分析之后的结果
	 */
	@Override
	public DependencyTree parse(String[] wordsandposes) {
		String[] words = new String[wordsandposes.length];
		String[] poses = new String[wordsandposes.length];

		//假设词语和词性之间的/分割的
		for (int i = 0; i < wordsandposes.length; i++) {
			String[] temp = wordsandposes[i].split("/");

			words[i] = temp[0];
			poses[i] = temp[1];
		}

		return parse(words, poses);
	}

	@Override
	public DependencyTree[] parse(int k, String sentence) {
		String[] wordsandposes = sentence.split("\\s+");
		String[] words = new String[wordsandposes.length];
		String[] poses = new String[wordsandposes.length];

		//假设词语和词性之间的/分割的
		for (int i = 0; i < wordsandposes.length; i++) {
			String[] temp = wordsandposes[i].split("/");

			words[i] = temp[0];
			poses[i] = temp[1];
		}

		return parse(k, words, poses);
	}

	@Override
	public DependencyTree[] parse(int k, String[] words, String[] poses) {
		List<String> wordslist = Arrays.asList(words);
		List<String> allwords = new ArrayList<>();
		allwords.add(0, "核心");
		allwords.addAll(wordslist);
		List<String> poseslist = Arrays.asList(poses);
		List<String> allposes = new ArrayList<>();
		allposes.add(0, "root");
		allposes.addAll(poseslist);
		
		DependencyParseMatrix proba = tagK(k, allwords.toArray(new String[allwords.size()]),
				allposes.toArray(new String[allposes.size()]));

		DependencyTree[] parse = new DependencyTree[k];
		parse = MaxSpanningTree.getMaxFromKres(k, proba);
		return parse;
	}

	@Override
	public DependencyTree[] parse(int k, String[] wordsandposes) {
		String[] words = new String[wordsandposes.length];
		String[] poses = new String[wordsandposes.length];

		//假设词语和词性之间的/分割的
		for (int i = 0; i < wordsandposes.length; i++) {
			String[] temp = wordsandposes[i].split("/");

			words[i] = temp[0];
			poses[i] = temp[1];
		}

		return parse(k, words, poses);
	}
}
