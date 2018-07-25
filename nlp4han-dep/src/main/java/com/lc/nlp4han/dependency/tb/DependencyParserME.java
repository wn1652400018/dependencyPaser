package com.lc.nlp4han.dependency.tb;


import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import com.lc.nlp4han.dependency.DependencyParser;
import com.lc.nlp4han.dependency.DependencySample;
import com.lc.nlp4han.dependency.DependencySampleParser;
import com.lc.nlp4han.dependency.DependencySampleParserCoNLL;
import com.lc.nlp4han.dependency.DependencySampleStream;
import com.lc.nlp4han.dependency.DependencyTree;
import com.lc.nlp4han.dependency.PlainTextBySpaceLineStream;
import com.lc.nlp4han.ml.model.ClassificationModel;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.model.SequenceClassificationModel;
import com.lc.nlp4han.ml.util.BeamSearch;
import com.lc.nlp4han.ml.util.EventModelSequenceTrainer;
import com.lc.nlp4han.ml.util.EventTrainer;
import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.SequenceTrainer;
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

    
    
    public DependencyParserME(String modelPath) throws IOException {
    	this(new File(modelPath));
    }
    
    public DependencyParserME(String modelPath,DependencyParseContextGenerator contextGenerator) throws IOException {
    	this(new File(modelPath),contextGenerator);
    }
    
    public DependencyParserME(File file) throws IOException {
    	this(new ModelWrapper(file));
    }
    
	public DependencyParserME(File file,DependencyParseContextGenerator contextGenerator) throws IOException {
		this(new ModelWrapper(file),contextGenerator);
	}
	
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
	
	
	public static ModelWrapper train(String trainDatePath, TrainingParameters params, DependencyParseContextGenerator contextGenerator,
			String encoding) throws IOException {
		return train(new File(trainDatePath),params,contextGenerator,encoding);
	}
	
	 public static ModelWrapper train(ObjectStream<DependencySample> samples, TrainingParameters trainParams) throws IOException{
		 return train(samples,trainParams,new DependencyParseContextGeneratorConf());
	 }
	
	 public static ModelWrapper train(File fileData, TrainingParameters params, DependencyParseContextGenerator contextGenerator,
				String encoding) throws IOException{
		ObjectStream<String> lineStream = new PlainTextBySpaceLineStream(new MarkableFileInputStreamFactory(fileData), encoding);

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
		
		ClassificationModel depModel = null;
		SequenceClassificationModel seqDepModel = null;
		
		Map<String, String> manifestInfoEntries = new HashMap<String, String>();
		TrainerType trainerType = TrainerFactory.getTrainerType(params.getSettings());
		
		if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType))
		{
			ObjectStream<Event> es = new DependencySampleEventStreamTB(sampleStream, contextGenerator);
			EventTrainer trainer = TrainerFactory.getEventTrainer(params.getSettings(), manifestInfoEntries);
			depModel = trainer.train(es);
		}
		else if(TrainerType.EVENT_MODEL_SEQUENCE_TRAINER.equals(trainerType)){
			DependencySampleSequenceStream ss = new DependencySampleSequenceStream(sampleStream, contextGenerator);
            EventModelSequenceTrainer trainer = TrainerFactory.getEventModelSequenceTrainer(params.getSettings(),
                    manifestInfoEntries);
            depModel = trainer.train(ss);
		}if (TrainerType.SEQUENCE_TRAINER.equals(trainerType))
        {
            SequenceTrainer trainer = TrainerFactory.getSequenceModelTrainer(
                  params.getSettings(), manifestInfoEntries);
            DependencySampleSequenceStream ss = new DependencySampleSequenceStream(sampleStream, contextGenerator);
            seqDepModel = trainer.train(ss);
        } else
		{
			throw new IllegalArgumentException("Trainer type is not supported: " + trainerType); 
		}
		
		return new ModelWrapper(depModel, beamSize);
	}
	
	
	
	@Override
	public DependencyTree parse(String sentence) {
		return null;
	}

	@Override
	public DependencyTree parse(String[] words, String[] poses) {
		ArrayList<String> allWords = new ArrayList<String>(Arrays.asList(words));
		allWords.add(0, "核心");
		ArrayList<String> allPoses = new ArrayList<String>(Arrays.asList(poses));
		allPoses.add(0,"root");
		words = allWords.toArray(new String[allWords.size()]);
		poses = allPoses.toArray(new String[allPoses.size()]);
		
		Oracle oracleMEBased = new Oracle(model,contextGenerator);
		ActionType action = new ActionType();
		Configuration currentConf = Configuration.initialConf(words, poses);
		while (!currentConf.isFinalConf()) {
			action = oracleMEBased.classify(currentConf);
			currentConf.transition(action);
		}
		DependencyTree depTree = TBDepTree.getTree(currentConf);
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
		for(Arc arc:arcs) {
			System.out.println(arc.getHead()+" "+arc.getDependent()+" "+arc.getRelation());
		}
		
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