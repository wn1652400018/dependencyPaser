package com.lc.nlp4han.segpos;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import opennlp.tools.ml.BeamSearch;
import opennlp.tools.ml.EventTrainer;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.ml.TrainerFactory.TrainerType;
import opennlp.tools.ml.maxent.io.PlainTextGISModelReader;
import opennlp.tools.ml.maxent.io.PlainTextGISModelWriter;
import opennlp.tools.ml.model.AbstractModel;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.SequenceValidator;
import opennlp.tools.util.TrainingParameters;

/**
 * 训练模型，标记序列
 * 
 * @author 刘小峰
 * @author 王馨苇
 *
 */
public class WordSegAndPosME implements WordSegAndPos{

	public static final int DEFAULT_BEAM_SIZE = 8;
	private WordSegAndPosContextGenerator contextGenerator;
	private int size;
	private Sequence bestSequence;
	private SequenceClassificationModel<String> model;
	private WordSegAndPosModel modelPackage;

    private SequenceValidator<String> sequenceValidator;
	
	/**
	 * 构造函数，初始化工作
	 * @param model 模型
	 * @param contextGen 特征
	 */
	public WordSegAndPosME(WordSegAndPosModel model, WordSegAndPosContextGenerator contextGen) {
		init(model , contextGen);
	}
    /**
     * 初始化工作
     * @param model 模型
     * @param contextGen 特征
     */
	private void init(WordSegAndPosModel model, WordSegAndPosContextGenerator contextGen) {
		int beamSize = WordSegAndPosME.DEFAULT_BEAM_SIZE;

        String beamSizeString = model.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER);

        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }

        modelPackage = model;

        contextGenerator = contextGen;
        size = beamSize;
        sequenceValidator = new DefaultWordSegAndPosSequenceValidator();
        if (model.getWordSegAndPosSequenceModel() != null) {
            this.model = model.getWordSegAndPosSequenceModel();
        } else {
            this.model = new BeamSearch<String>(beamSize,
                    model.getWordSegPosModel(), 0);
        }
		
	}
	
	/**
	 * 训练模型
	 * @param file 训练文件
	 * @param params 训练
	 * @param contextGen 特征
	 * @param encoding 编码
	 * @return 模型和模型信息的包裹结果
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static WordSegAndPosModel train(File file, TrainingParameters params, WordSegAndPosContextGenerator contextGen,
			String encoding){
		WordSegAndPosModel model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(file), encoding);
			WordSegAndPosParseContext parse = new WordSegAndPosParseContext(new WordSegAndPosParseNews());
			ObjectStream<WordSegAndPosSample> sampleStream = new WordSegAndPosSampleStream(lineStream, parse);
			model = WordSegAndPosME.train("zh", sampleStream, params, contextGen);
			return model;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}	
		return null;
	}

	/**
	 * 训练模型
	 * @param languageCode 编码
	 * @param sampleStream 文件流
	 * @param contextGen 特征
	 * @param encoding 编码
	 * @return 模型和模型信息的包裹结果
	 * @throws IOException 
	 * @throws FileNotFoundException 
	 */
	public static WordSegAndPosModel train(String languageCode, ObjectStream<WordSegAndPosSample> sampleStream, TrainingParameters params,
			WordSegAndPosContextGenerator contextGen) throws IOException {
		String beamSizeString = params.getSettings().get(BeamSearch.BEAM_SIZE_PARAMETER);
		int beamSize = WordSegAndPosME.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }
        MaxentModel posModel = null;
        Map<String, String> manifestInfoEntries = new HashMap<String, String>();
        //event_model_trainer
        TrainerType trainerType = TrainerFactory.getTrainerType(params.getSettings());
        SequenceClassificationModel<String> seqPosModel = null;
        if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType)) {
        	//sampleStream为PhraseAnalysisSampleStream对象
            ObjectStream<Event> es = new WordSegAndPosSampleEventStream(sampleStream, contextGen);
            EventTrainer trainer = TrainerFactory.getEventTrainer(params.getSettings(),
                    manifestInfoEntries);
            posModel = trainer.train(es);                       
        }

        if (posModel != null) {
            return new WordSegAndPosModel(languageCode, posModel, beamSize, manifestInfoEntries);
        } else {
            return new WordSegAndPosModel(languageCode, seqPosModel, manifestInfoEntries);
        }
	}

	/**
	 * 训练模型，并将模型写出
	 * @param file 训练的文本
	 * @param modelbinaryFile 二进制的模型文件
	 * @param modeltxtFile 文本类型的模型文件
	 * @param params 训练的参数配置
	 * @param contextGen 上下文 产生器
	 * @param encoding 编码方式
	 * @return
	 */
	public static WordSegAndPosModel train(File file, File modelbinaryFile, File modeltxtFile, TrainingParameters params,
			WordSegAndPosContextGenerator contextGen, String encoding) {
		OutputStream modelOut = null;
		PlainTextGISModelWriter modelWriter = null;
		WordSegAndPosModel model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(file), encoding);
			WordSegAndPosParseContext parse = new WordSegAndPosParseContext(new WordSegAndPosParseNews());
			ObjectStream<WordSegAndPosSample> sampleStream = new WordSegAndPosSampleStream(lineStream, parse);
			model = WordSegAndPosME.train("zh", sampleStream, params, contextGen);
			 //模型的持久化，写出的为二进制文件
            modelOut = new BufferedOutputStream(new FileOutputStream(modelbinaryFile));           
            model.serialize(modelOut);
            //模型的写出，文本文件
            modelWriter = new PlainTextGISModelWriter((AbstractModel) model.getWordSegPosModel(), modeltxtFile);
            modelWriter.persist();
            return model;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
            if (modelOut != null) {
                try {
                    modelOut.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }	
		return null;
	}
	
	public String[] tag(String[] characters,Object[] additionaContext){
		bestSequence = model.bestSequence(characters, additionaContext, contextGenerator,sequenceValidator);
      //  System.out.println(bestSequence);
		List<String> t = bestSequence.getOutcomes();
		return t.toArray(new String[t.size()]);
	}
	/**
	 * 根据训练得到的模型文件得到
	 * @param modelFile 模型文件
	 * @param params 参数
	 * @param contextGen 上下文生成器
	 * @param encoding 编码方式
	 * @return
	 */
	public static WordSegAndPosModel readModel(File modelFile, TrainingParameters params, WordSegAndPosContextGenerator contextGen,
			String encoding) {
		PlainTextGISModelReader modelReader = null;
		AbstractModel abModel = null;
		WordSegAndPosModel model = null;
		String beamSizeString = params.getSettings().get(BeamSearch.BEAM_SIZE_PARAMETER);
	      
        int beamSize = WordSegAndPosME.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }

		try {
			Map<String, String> manifestInfoEntries = new HashMap<String, String>();
			modelReader = new PlainTextGISModelReader(modelFile);			
			abModel = modelReader.getModel();
			model =  new WordSegAndPosModel(encoding, abModel, beamSize,manifestInfoEntries);
	
			System.out.println("读取模型成功");
            return model;
        } catch (UnsupportedOperationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		return null;
	}
	
	/**
	 * 对生语料进行词性标记
	 * @param sentence 一句完整的话
	 * @return
	 */
	public String[] segmentAndTag(String sentence) {
		String[] characters = new String[sentence.length()];
		for (int i = 0; i < sentence.length(); i++) {
			characters[i] = sentence.charAt(i)+"";
		}
		return wordsegandpos(characters);
	}
	
	/**
	 * 对生语料进行词性标记
	 * @param characters 生语料切成的一个个字
	 * @return 得到分词加词性标记的结果
	 */
	public String[] wordsegandpos(String[] characters) {
		String[] characterandpos = tag(characters);
		String[] poses = WordSegAndPosSample.toPos(characterandpos);
		String[] words = WordSegAndPosSample.toWord(characters, characterandpos);
		String[] res = new String[words.length];
		for (int i = 0; i < words.length && i < poses.length; i++) {
			res[i] = words[i]+"/"+poses[i]+" ";
		}
		return res;
	}
	
	/**
	 * 得到的最好的分词加标注的序列
	 * @param words 字数组
	 * @return
	 */
	public String[] tag(String[] characters){
		String[] characterandpos = this.tag(characters,null);		
		return characterandpos;
	}
	
	/**
	 * 得到最好的numTaggings个标记序列
	 * @param numTaggings 个数
	 * @param characters 一个个字
	 * @return 分词加词性标注的序列
	 */
	public String[][] tag(int numTaggings, String[] characters) {
        Sequence[] bestSequences = model.bestSequences(numTaggings, characters, null,
        		contextGenerator, sequenceValidator);
        String[][] tagsandposes = new String[bestSequences.length][];
        for (int si = 0; si < tagsandposes.length; si++) {
            List<String> t = bestSequences[si].getOutcomes();
            tagsandposes[si] = t.toArray(new String[t.size()]);
           
        }
        return tagsandposes;
    }

	/**
	 * 最好的K个序列
	 * @param characters 一个个字
	 * @return
	 */
    public Sequence[] topKSequences(String[] characters) {
        return this.topKSequences(characters, null);
    }

    /**
     * 最好的K个序列
     * @param characters 一个个字
     * @param additionaContext
     * @return 
     */
    public Sequence[] topKSequences(String[] characters, Object[] additionaContext) {
        return model.bestSequences(size, characters, additionaContext,
        		contextGenerator, sequenceValidator);
}
    /**
     * 生成词语-词性词典
     * @param samples
     * @return
     * @throws IOException 
     */
    public static HashMap<String,List<String>> bulidDictionary(ObjectStream<WordSegAndPosSample> samples) throws IOException{
    	//词语-词性词典【键为词，值为词性】，一个词语可能有多个词性
    	HashMap<String,List<String>> dict = new HashMap<String,List<String>>();
        WordSegAndPosSample sample = null;
        while((sample = samples.read()) != null)
        {
            String[] words = sample.getWords();
            String[] poses = sample.getPoses();
            
            for (int i = 0; i < words.length; i++) {
				//是否包含这个词语
            	if(dict.containsKey(words[i])){//包含这个词语
            		//看是否包含这个词性
            		if(dict.get(words[i]).contains(poses[i])){//包含这个词性，什么也不做
            			
            		}else{//未包含这个词性，就加入
            			dict.get(words[i]).add(poses[i]);
            			dict.put(words[i], dict.get(words[i]));
            		}
            	}else{//不包含这个词语
            		List<String> list = new ArrayList<String>();
            		list.add(poses[i]);
            		dict.put(words[i],list);
            	}
			}
        }
        
        return dict;
    }
    
    /**
     * 
     * @param file 训练文件
     * @param encoding 编码方式
     * @return
     * @throws IOException 
     */
	public static HashMap<String,List<String>> buildDictionary(File file, String encoding) throws IOException {
		BufferedReader data = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
        String sentences;
        HashMap<String,List<String>> dict = new HashMap<String,List<String>>();
        while ((sentences = data.readLine()) != null) {
        	if(sentences.compareTo("") != 0){
        		String wordsandposes[] = WhitespaceTokenizer.INSTANCE.tokenize(sentences);
                for (int i = 1; i < wordsandposes.length; i++) {
                	String[] wordanspos = wordsandposes[i].split("/");
        	    	String word = wordanspos[0];
        	    	String pos = wordanspos[1];
        	    	//是否包含这个词语
                	if(dict.containsKey(word)){//包含这个词语
                		//看是否包含这个词性
                		if(dict.get(word).contains(pos)){//包含这个词性，什么也不做
                			
                		}else{//未包含这个词性，就加入
                			dict.get(word).add(pos);
                			dict.put(word, dict.get(word));
                		}
                	}else{//不包含这个词语
                		List<String> list = new ArrayList<String>();
                		list.add(pos);
                		dict.put(word,list);
                	}
    			}
        	}           
        }

        data.close();

        return dict;
	}
	
	/**
	 * 对生语料进行分词
	 * @param sentence 生语料
	 * @return 得到分词的结果
	 */
	public String[] segment(String sentence) {
		String[] characters = new String[sentence.length()];
		for (int i = 0; i < sentence.length(); i++) {
			characters[i] = sentence.charAt(i)+"";
		}
		return wordseg(characters);
	}
	
	/**
	 * 对生语料进行分词
	 * @param characters 生语料分解成的一个个字
	 * @return 得到分词的结果
	 */
	public String[] wordseg(String[] characters) {
		String[] characterandpos = tag(characters);
		String[] words = WordSegAndPosSample.toWord(characters, characterandpos);
		return words;
	}
}
