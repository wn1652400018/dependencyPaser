package com.lc.nlp4han.pos.character;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import com.lc.nlp4han.pos.POSTaggerProb;

import opennlp.tools.ml.BeamSearch;
import opennlp.tools.ml.EventTrainer;
import opennlp.tools.ml.TrainerFactory;
import opennlp.tools.ml.TrainerFactory.TrainerType;
import opennlp.tools.ml.maxent.io.PlainTextGISModelReader;
import opennlp.tools.ml.model.AbstractModel;
import opennlp.tools.ml.model.Event;
import opennlp.tools.ml.model.MaxentModel;
import opennlp.tools.ml.model.SequenceClassificationModel;
import opennlp.tools.tokenize.WhitespaceTokenizer;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.Sequence;
import opennlp.tools.util.TrainingParameters;

/**
 * 基于字的最大熵中文词性标注器
 * 
 * 输入为切分后的词序列
 * 
 * @author 王馨苇
 * @author 刘小峰
 * 
 */
public class CharPOSTaggerME implements POSTaggerProb
{

    public static final int DEFAULT_BEAM_SIZE = 10;
    private CharPOSContextGenerator contextGenerator;
    private int size;
    private Sequence bestSequence;
    private SequenceClassModel<String> model;
    private CharPOSModel modelPackage;
    private List<String> characters = new ArrayList<>();
    private List<String> tags = new ArrayList<>();

    private CharPOSSequenceValidator<String> sequenceValidator;

    /**
     * 构造函数，初始化工作
     * 
     * @param model
     *            模型
     * @param contextGen
     *            特征
     */
    public CharPOSTaggerME(CharPOSModel model, CharPOSContextGenerator contextGen)
    {
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
    private void init(CharPOSModel model, CharPOSContextGenerator contextGen)
    {
        int beamSize = CharPOSTaggerME.DEFAULT_BEAM_SIZE;

        String beamSizeString = model.getManifestProperty(BeamSearch.BEAM_SIZE_PARAMETER);

        if (beamSizeString != null)
        {
            beamSize = Integer.parseInt(beamSizeString);
        }

        modelPackage = model;

        contextGenerator = contextGen;
        size = beamSize;
        sequenceValidator = new DefaultCharPOSSequenceValidator();
        if (model.getWordSegPosSequenceModel() != null)
        {
            this.model = model.getWordSegPosSequenceModel();
        }
        else
        {
            this.model = new BeamSearchCharPOS<String>(beamSize, model.getWordSegPosModel(), 0);
        }

    }


    /**
     * 训练模型
     * 
     * @param languageCode
     *            编码
     * @param sampleStream
     *            文件流
     * @param contextGen
     *            特征
     * @param encoding
     *            编码
     * @return 模型和模型信息的包裹结果
     * @throws IOException
     * @throws FileNotFoundException
     */
    public static CharPOSModel train(String languageCode, ObjectStream<CharPOSSample> sampleStream, TrainingParameters params, CharPOSContextGenerator contextGen) throws IOException
    {
        String beamSizeString = params.getSettings().get(BeamSearch.BEAM_SIZE_PARAMETER);
        int beamSize = CharPOSTaggerME.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null)
        {
            beamSize = Integer.parseInt(beamSizeString);
        }
        MaxentModel posModel = null;
        Map<String, String> manifestInfoEntries = new HashMap<String, String>();
        // event_model_trainer
        TrainerType trainerType = TrainerFactory.getTrainerType(params.getSettings());
        SequenceClassificationModel<String> seqPosModel = null;
        if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType))
        {
            // sampleStream为PhraseAnalysisSampleStream对象
            ObjectStream<Event> es = new CharPOSSampleEventStream(sampleStream, contextGen);
            EventTrainer trainer = TrainerFactory.getEventTrainer(params.getSettings(), manifestInfoEntries);
            posModel = trainer.train(es);
        }

        if (posModel != null)
        {
            return new CharPOSModel(languageCode, posModel, beamSize, manifestInfoEntries);
        }
        else
        {
            return new CharPOSModel(languageCode, seqPosModel, manifestInfoEntries);
        }
    }

    public String[] tag(String[] characters, String[] tags, String[] words, Object[] additionaContext)
    {
        bestSequence = model.bestSequence(characters, tags, words, additionaContext, contextGenerator, sequenceValidator);
        // System.out.println(bestSequence);
        List<String> t = bestSequence.getOutcomes();

        return t.toArray(new String[t.size()]);
    }

    /**
     * 根据训练得到的模型文件得到
     * 
     * @param modelFile
     *            模型文件
     * @param params
     *            参数
     * @param contextGen
     *            上下文生成器
     * @param encoding
     *            编码方式
     * @return
     * @throws IOException
     */
    public static CharPOSModel readModel(File modelFile, TrainingParameters params, CharPOSContextGenerator contextGen, String encoding) throws IOException
    {
        PlainTextGISModelReader modelReader = null;
        AbstractModel abModel = null;
        CharPOSModel model = null;
        String beamSizeString = params.getSettings().get(BeamSearch.BEAM_SIZE_PARAMETER);

        int beamSize = CharPOSTaggerME.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null)
        {
            beamSize = Integer.parseInt(beamSizeString);
        }

        Map<String, String> manifestInfoEntries = new HashMap<String, String>();
        modelReader = new PlainTextGISModelReader(modelFile);
        abModel = modelReader.getModel();
        model = new CharPOSModel(encoding, abModel, beamSize, manifestInfoEntries);

        System.out.println("读取模型成功");
        return model;
    }

    /**
     * 根据分词的数组生成字符序列，字符的标记序列
     * 
     * @param words
     *            分词之后的词语数组
     */
    public void taglabel(String[] words)
    {
        characters.clear();
        tags.clear();
        for (int i = 0; i < words.length; i++)
        {
            String temp = words[i];
            if (temp.length() == 1)
            {
                characters.add(temp);
                tags.add("S");
                continue;
            }
            for (int j = 0; j < temp.length(); j++)
            {
                char c = temp.charAt(j);
                if (j == 0)
                {
                    characters.add(c + "");
                    tags.add("B");
                }
                else if (j == temp.length() - 1)
                {
                    characters.add(c + "");
                    tags.add("E");
                }
                else
                {
                    characters.add(c + "");
                    tags.add("M");
                }
            }
        }
    }

    /**
     * 得到的最好的标记序列
     * 
     * @param words
     *            分词的数组
     * @return
     */
    public String[] tag(String[] words)
    {
        taglabel(words);
        String[] characterandpos = this.tag(characters.toArray(new String[characters.size()]), tags.toArray(new String[tags.size()]), words, null);
        return CharPOSSample.toPos(characterandpos);
    }

    /**
     * 得到最好的numTaggings个标记序列
     * 
     * @param k
     *            个数
     * @param words
     *            分词的数组
     * @return 词性标注的序列
     */
    public String[][] tag(String[] words, int k)
    {
        taglabel(words);
        Sequence[] bestSequences = model.bestSequences(k, characters.toArray(new String[characters.size()]), tags.toArray(new String[tags.size()]), words, null, contextGenerator, sequenceValidator);
        String[][] tags = new String[bestSequences.length][];
        String[][] poses = new String[bestSequences.length][];
        for (int si = 0; si < tags.length; si++)
        {
            List<String> t = bestSequences[si].getOutcomes();
            tags[si] = t.toArray(new String[t.size()]);
            poses[si] = CharPOSSample.toPos(tags[si]);
        }
        return poses;
    }

    /**
     * 最好的K个序列
     * 
     * @param sentence
     *            分词之后的词语数组
     * @return
     */
    public Sequence[] topKSequences(String[] sentence)
    {
        return this.topKSequences(sentence, null);
    }

    /**
     * 最好的K个序列
     * 
     * @param words
     *            分词之后的词语数组
     * @param additionaContext
     * @return
     */
    public Sequence[] topKSequences(String[] words, Object[] additionaContext)
    {
        taglabel(words);
        return model.bestSequences(size, characters.toArray(new String[characters.size()]), tags.toArray(new String[tags.size()]), words, additionaContext, contextGenerator, sequenceValidator);
    }

    /**
     * 生成词典
     * 
     * @param sample
     *            样本流
     * @return
     * @throws IOException
     */
    public static HashSet<String> buildDictionary(ObjectStream<CharPOSSample> samples) throws IOException
    {
        HashSet<String> dict = new HashSet<>();
        CharPOSSample sample;
        while ((sample = samples.read()) != null)
        {
            String[] words = sample.getWords();
            for (int i = 0; i < words.length; i++)
            {
                dict.add(words[i]);
            }
        }
        return dict;
    }

    /**
     * 
     * @param file
     *            训练文件
     * @param encoding
     *            编码方式
     * @return
     * @throws IOException
     */
    public static HashSet<String> buildDictionary(File file, String encoding) throws IOException
    {
        BufferedReader data = new BufferedReader(new InputStreamReader(new FileInputStream(file), encoding));
        String sentences;
        HashSet<String> dict = new HashSet<String>();
        while ((sentences = data.readLine()) != null)
        {
            if (sentences.compareTo("") != 0)
            {
                String wordsandposes[] = WhitespaceTokenizer.INSTANCE.tokenize(sentences);
                for (int i = 1; i < wordsandposes.length; i++)
                {
                    String[] wordanspos = wordsandposes[i].split("/");
                    String word = wordanspos[0];
                    dict.add(word);
                }
            }
        }

        data.close();

        return dict;
    }
}
