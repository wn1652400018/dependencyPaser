package com.lc.nlp4han.chunk.wordpos;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lc.nlp4han.chunk.AbstractChunkAnalysisSample;
import com.lc.nlp4han.chunk.Chunk;
import com.lc.nlp4han.chunk.ChunkAnalysisContextGenerator;
import com.lc.nlp4han.chunk.Chunker;
import com.lc.nlp4han.ml.model.ClassificationModel;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.model.SequenceClassificationModel;
import com.lc.nlp4han.ml.util.BeamSearch;
import com.lc.nlp4han.ml.util.EventTrainer;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.Sequence;
import com.lc.nlp4han.ml.util.SequenceValidator;
import com.lc.nlp4han.ml.util.TrainerFactory;
import com.lc.nlp4han.ml.util.TrainerFactory.TrainerType;
import com.lc.nlp4han.ml.util.TrainingParameters;

/**
 *<ul>
 *<li>Description: 基于词和词性的组块分析模型训练类 
 *<li>Company: HUST
 *<li>@author Sonly
 *<li>Date: 2017年12月3日
 *</ul>
 */
public class ChunkAnalysisWordPosME implements Chunker {
	
	public static final int DEFAULT_BEAM_SIZE = 33;
	private ChunkAnalysisContextGenerator contextGenerator;
	private int size;
	private Sequence bestSequence;
	private SequenceClassificationModel<String> model;
    private SequenceValidator<String> sequenceValidator;
    private String label;

    public ChunkAnalysisWordPosME() {

    }

	public ChunkAnalysisWordPosME(ModelWrapper model, SequenceValidator<String> sequenceValidator, ChunkAnalysisContextGenerator contextGen, String label) {
		this.sequenceValidator = sequenceValidator;
		this.label = label;
		init(model , contextGen);
	}
	
    /**
     * 初始化工作
     * @param model 		组块分析模型
     * @param contextGen	上下文生成器
     */
	private void init(ModelWrapper model, ChunkAnalysisContextGenerator contextGen) {
		int beamSize = ChunkAnalysisWordPosME.DEFAULT_BEAM_SIZE;
//        String beamSizeString = model.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER);

//        if (beamSizeString != null)
//            beamSize = Integer.parseInt(beamSizeString);

        contextGenerator = contextGen;
        size = beamSize;
        
        this.model = model.getSequenceModel();
        
//        if (model.getChunkAnalysisSequenceModel() != null)
//            this.model = model.getChunkAnalysisSequenceModel();
//        else
//            this.model = new BeamSearch<String>(beamSize, model.getChunkAnalysisModel(), 0);
	}
	
//	/**
//	 * 训练模型
//	 * @param file 			训练文件
//	 * @param params 		训练
//	 * @param contextGen	特征
//	 * @param encoding	 	编码
//	 * @return 				模型和模型信息的包裹结果
//	 * @throws IOException 
//	 * @throws FileNotFoundException 
//	 */
//	public ChunkAnalysisBasedWordAndPOSModel train(File file, TrainingParameters params, ChunkAnalysisContextGenerator contextGen,
//			String encoding, AbstractChunkAnalysisParse parse){
//		ChunkAnalysisBasedWordAndPOSModel model = null;
//		try {
//			ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(file), encoding);
//			ObjectStream<AbstractChunkAnalysisSample> sampleStream = new ChunkAnalysisBasedWordAndPOSSampleStream(lineStream, parse, label);
//			model = train("zh", sampleStream, params, contextGen);
//			return model;
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}	
//		return null;
//	}

	/**
	 * 训练模型
	 * @param languageCode 	编码
	 * @param sampleStream 	文件流
	 * @param contextGen 	特征
	 * @param encoding 		编码
	 * @return 				模型和模型信息的包裹结果
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public ModelWrapper train(ObjectStream<AbstractChunkAnalysisSample> sampleStream, TrainingParameters params,
			ChunkAnalysisContextGenerator contextGen) throws IOException {
		String beamSizeString = params.getSettings().get(BeamSearch.BEAM_SIZE_PARAMETER);
		int beamSize = ChunkAnalysisWordPosME.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }
        ClassificationModel maxentModel = null;
        Map<String, String> manifestInfoEntries = new HashMap<String, String>();
        //event_model_trainer
        TrainerType trainerType = TrainerFactory.getTrainerType(params.getSettings());
        SequenceClassificationModel<String> chunkClassificationModel = null;
        if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType)) {
        	//sampleStream为PhraseAnalysisSampleStream对象
            ObjectStream<Event> es = new ChunkAnalysisWordPosSampleEvent(sampleStream, contextGen);
            EventTrainer trainer = TrainerFactory.getEventTrainer(params.getSettings(),
                    manifestInfoEntries);
            maxentModel = trainer.train(es);                       
        }

        if (maxentModel != null) {
			return new ModelWrapper(maxentModel, beamSize);
		} else {
			return new ModelWrapper(chunkClassificationModel);
		}
	}

//	/**
//	 * 训练模型，并将模型写出
//	 * @param file 			训练的文本
//	 * @param modelFile 	模型文件
//	 * @param params 		训练的参数配置
//	 * @param contextGen 	上下文 产生器
//	 * @param encoding 		编码方式
//	 * @return
//	 */
//	public ChunkAnalysisBasedWordAndPOSModel train(File file, File modelbinaryFile, TrainingParameters params,
//			ChunkAnalysisContextGenerator contextGen, String encoding, AbstractChunkAnalysisParse parse) {
//		OutputStream modelOut = null;
//		ChunkAnalysisBasedWordAndPOSModel model = null;
//		try {
//			ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(file), encoding);
//			ObjectStream<AbstractChunkAnalysisSample> sampleStream = new ChunkAnalysisBasedWordAndPOSSampleStream(lineStream, parse, label);
//			model = train("zh", sampleStream, params, contextGen);
//			 //模型的持久化，写出的为二进制文件
//            modelOut = new BufferedOutputStream(new FileOutputStream(modelbinaryFile));           
//            model.serialize(modelOut);
//            
//            return model;
//		} catch (FileNotFoundException e) {
//			e.printStackTrace();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}finally {
//            if (modelOut != null) {
//                try {
//                    modelOut.close();
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }
//            }
//        }	
//		return null;
//	}

//	/**
//	 * 根据训练得到的模型文件得到
//	 * @param modelFile 	模型文件
//	 * @return
//	 */
//	public ChunkAnalysisBasedWordAndPOSModel readModel(File modelFile) {
//		ChunkAnalysisBasedWordAndPOSModel model = null;
//		
//		try {
//			model =  new ChunkAnalysisBasedWordAndPOSModel(modelFile);
//			System.out.println("读取模型成功");
//            return model;
//        } catch (IOException e) {
//			e.printStackTrace();
//		} 
//		
//		return model;
//	}
	
	/**
	 * 返回给定词组和词性的组块类型
	 * @param words				待确定组块类型的词组
	 * @param poses				词组对应的词性数组
	 * @param additionaContext	其他上下文信息
	 * @return					组块类型
	 */
	public String[] tag(String[] words, String[] poses){
		bestSequence = model.bestSequence(words, poses, contextGenerator,sequenceValidator);
		List<String> chunks = bestSequence.getOutcomes();
		
		return chunks.toArray(new String[chunks.size()]);
	}
	
	/**
	 * 得到最优的num个组块标注结果
	 * @param k 	返回的标注结果个数
	 * @param words 待确定组块类型的词组
	 * @param pos 	词组对应的词性数组
	 * @return 		最优的k个组块标注结果
	 */
	public String[][] tag(int k, String[] words, String[] pos) {
        Sequence[] bestSequences = model.bestSequences(k, words, pos,
        		contextGenerator, sequenceValidator);
        String[][] tagsandposes = new String[bestSequences.length][];
        for (int si = 0; si < tagsandposes.length; si++) {
            List<String> t = bestSequences[si].getOutcomes();
            tagsandposes[si] = t.toArray(new String[t.size()]);
           
        }
        return tagsandposes;
    }

    /**
	 * 根据给定词组及其词性，返回最优的K个标注序列
	 * @param words	待标注的词组
	 * @param poses	与词组对应的词性数组
	 * @return		最优的K个标注序列
	 */
    public Sequence[] getTopKSequences(String[] characters, String[] pos) {
        return model.bestSequences(size, characters, pos,
        		contextGenerator, sequenceValidator);
    }

    @Override
	public Chunk[] parse(String sentence) {
    	String[] wordTags = sentence.split("//s+");
    	List<String> words = new ArrayList<>();
    	List<String> poses = new ArrayList<>();
    	
    	for(String wordTag : wordTags) {
    		words.add(wordTag.split("/")[0]);
    		poses.add(wordTag.split("/")[1]);
    	}
    	
		String[] chunkTypes = tag(words.toArray(new String[words.size()]), poses.toArray(new String[poses.size()]));
		
		AbstractChunkAnalysisSample sample = new ChunkAnalysisWordPosSample(words.toArray(new String[words.size()]), poses.toArray(new String[poses.size()]), chunkTypes);
		sample.setLabel(label);
		
		return sample.toChunk();
	}

	@Override
	public Chunk[][] parse(String sentence, int k) {
		String[] wordTags = sentence.split("//s+");
		List<String> words = new ArrayList<>();
		List<String> poses = new ArrayList<>();
		
		for(String wordTag : wordTags) {
			words.add(wordTag.split("/")[0]);
			poses.add(wordTag.split("/")[1]);
		}
		
		String[][] chunkTypes = tag(k, words.toArray(new String[words.size()]), poses.toArray(new String[poses.size()]));
		Chunk[][] chunks = new Chunk[chunkTypes.length][];
		for(int i = 0; i < chunkTypes.length; i++) {
			String[] chunkSequences = chunkTypes[i];
						
			AbstractChunkAnalysisSample sample = new ChunkAnalysisWordPosSample(words.toArray(new String[words.size()]), poses.toArray(new String[poses.size()]), chunkSequences);
			sample.setLabel(label);
			chunks[i] = sample.toChunk();
		}
		
		return chunks;
	}
}

