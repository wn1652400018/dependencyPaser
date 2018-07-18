package com.lc.nlp4han.dependency.tb;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lc.nlp4han.dependency.DependencyParser;
import com.lc.nlp4han.dependency.DependencySample;
import com.lc.nlp4han.dependency.DependencySampleParser;
import com.lc.nlp4han.dependency.DependencySampleParserCoNLL;
import com.lc.nlp4han.dependency.DependencySampleStream;
import com.lc.nlp4han.dependency.DependencyTree;
import com.lc.nlp4han.ml.model.ClassificationModel;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.util.BeamSearch;
import com.lc.nlp4han.ml.util.EventTrainer;
import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;
import com.lc.nlp4han.ml.util.TrainerFactory;
import com.lc.nlp4han.ml.util.TrainingParameters;
import com.lc.nlp4han.ml.util.TrainerFactory.TrainerType;



public class DependencyParserME implements DependencyParser{

	
	
    public static final int DEFAULT_BEAM_SIZE = 3;

    /**
     * 上下文产生器
     */
    private DependencyParseContextGenerator contextGenerator;

    
    private ClassificationModel model;


	
	
	public DependencyParserME(ModelWrapper model) throws IOException {
		init(model, new DependencyParseContextGeneratorConf());
	}
	
	public DependencyParserME(ModelWrapper model,DependencyParseContextGenerator contextGenerator) {
		init(model,contextGenerator);
	}
	
	/**
	 * 初始化工作
	 * 
	 * @param model
	 *            模型
	 * @param contextGen
	 *            特征
	 */	
	private void init(ModelWrapper model, DependencyParseContextGenerator contextGenerator)
    {
		this.model = model.getModel();

		this.contextGenerator = contextGenerator;
    }
	
	
	
	 public static ModelWrapper train(ObjectStream<DependencySample> samples, TrainingParameters trainParams) throws IOException{
		 return train(samples,trainParams,new DependencyParseContextGeneratorConf());
	 }
	
	 public static ModelWrapper train(File file, TrainingParameters params, DependencyParseContextGenerator contextGenerator,
				String encoding) throws IOException{
		ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(file), encoding);

		DependencySampleParser sampleParser = new DependencySampleParserCoNLL();
		ObjectStream<DependencySample> sampleStream = new DependencySampleStream(lineStream, sampleParser);
		return train(sampleStream, params, contextGenerator);
	 }
	 
	public static ModelWrapper train(ObjectStream<DependencySample> sampleStream, TrainingParameters params,
			DependencyParseContextGenerator contextGenerator) throws IOException {
		
		String beamSizeString = params.getSettings().get(BeamSearch.BEAM_SIZE_PARAMETER);

		int beamSize = DependencyParserME.DEFAULT_BEAM_SIZE;
		if (beamSizeString != null)
		{
			beamSize = Integer.parseInt(beamSizeString);
		}
		
		ClassificationModel maxentModel = null;
		
		Map<String, String> manifestInfoEntries = new HashMap<String, String>();
		TrainerType trainerType = TrainerFactory.getTrainerType(params.getSettings());
		
		if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType))
		{
			ObjectStream<Event> es = new DependencySampleEventStreamTB(sampleStream, contextGenerator);
			EventTrainer trainer = TrainerFactory.getEventTrainer(params.getSettings(), manifestInfoEntries);
			maxentModel = trainer.train(es);
		}
		else {
			throw new IllegalArgumentException("Trainer type is not supported: " + trainerType); 
		}
		
		return new ModelWrapper(maxentModel, beamSize);
	}
	
	
	
	@Override
	public DependencyTree parse(String sentence) {
		return null;
	}

	@Override
	public DependencyTree parse(String[] words, String[] poses) {
		Oracle oracleMEBased = new Oracle(model,contextGenerator);
		ActionType action = new ActionType();
		Configuration currentConf = Configuration.initialConf(words, poses);
		while (!currentConf.isFinalConf()) {
			action = oracleMEBased.classifyConf(currentConf);
			currentConf.transition(action);
		}
		DependencyTree depTree = getDePendencyTree(currentConf.getArcs());
		return depTree;
	}

	@Override
	public DependencyTree[] parse(String sentence, int k) {
		return null;
	}

	@Override
	public DependencyTree[] parse(String[] words, String[] poses, int k) {
		return null;
	}
	
	public  DependencyTree getDePendencyTree(ArrayList<Arc> arcs) {
		//根据arcs列表获得依存树
		return new DependencyTree();
	}

	@Override
	public DependencyTree parse(String[] wordsandposes) {
		return null;
	}

	@Override
	public DependencyTree[] parse(String[] wordsandposes, int k) {
		return null;
	}

}