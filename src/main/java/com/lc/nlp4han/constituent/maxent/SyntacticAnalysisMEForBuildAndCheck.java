package com.lc.nlp4han.constituent.maxent;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.lc.nlp4han.constituent.AbstractGenerateHeadWords;
import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.ConstituentTree;
import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.constituent.PlainTextByTreeStream;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.constituent.TreeToHeadTree;
import com.lc.nlp4han.ml.model.ClassificationModel;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.util.EventTrainer;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.TrainerFactory;
import com.lc.nlp4han.ml.util.TrainerFactory.TrainerType;
import com.lc.nlp4han.pos.word.POSTaggerWordME;
import com.lc.nlp4han.ml.util.TrainingParameters;

/**
 * 分步骤训练build check模型
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisMEForBuildAndCheck implements ConstituentParser<HeadTreeNode>{
	public static final int DEFAULT_BEAM_SIZE = 20;
	private SyntacticAnalysisContextGenerator<HeadTreeNode> contextGenerator;
	@SuppressWarnings("unused")
	private int size;
	private SyntacticAnalysisSequenceClassificationModel<HeadTreeNode> model;

    private SyntacticAnalysisSequenceValidator<HeadTreeNode> sequenceValidator;
    
    private AbstractGenerateHeadWords aghw ; 
	private POSTaggerWordME postagger ;
	private SyntacticAnalysisMEForChunk chunktagger ;
	/**
	 * 构造函数，初始化工作
	 * @param model 模型
	 * @param contextGen 特征
	 * @param aghw 生成头结点，build后check为yes时候进行合并的时候需要
	 */
	public SyntacticAnalysisMEForBuildAndCheck(ModelWrapper posmodel, ModelWrapper chunkmodel, ModelWrapper buildmodel, ModelWrapper checkmodel,SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen, AbstractGenerateHeadWords aghw) {
		init(posmodel, chunkmodel, buildmodel ,checkmodel, contextGen, aghw);
	}
    /**
     * 初始化工作
     * @param model 模型
     * @param contextGen 特征
     * @param aghw 生成头结点，build后check为yes时候进行合并的时候需要
     */
	private void init(ModelWrapper posmodel, ModelWrapper chunkmodel, ModelWrapper buildmodel, ModelWrapper checkmodel, SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen, AbstractGenerateHeadWords aghw) {
		int beamSize = SyntacticAnalysisMEForBuildAndCheck.DEFAULT_BEAM_SIZE;

        contextGenerator = contextGen;
        size = beamSize;
        sequenceValidator = new DefaultSyntacticAnalysisSequenceValidator();
        this.aghw = aghw;
        postagger = new POSTaggerWordME(posmodel);
        chunktagger = new SyntacticAnalysisMEForChunk(chunkmodel, contextGen, aghw);
        this.model = new SyntacticAnalysisBeamSearch(beamSize,buildmodel.getModel(),
                    checkmodel.getModel(), 0, aghw);
	}
	
	/**
	 * 训练模型
	 * @param file 训练文件
	 * @param params 训练模型的参数配置
	 * @param contextGen 特征
	 * @param encoding 编码
	 * @param aghw 生成头结点的方法
	 * @return 模型和模型信息的包裹结果
	 */
	public static ModelWrapper trainForBuild(File file, TrainingParameters params, SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen,
			String encoding, AbstractGenerateHeadWords aghw){
		ModelWrapper model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(file), encoding);
			ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sampleStream = new SyntacticAnalysisSampleStream(lineStream, aghw);
			model = SyntacticAnalysisMEForBuildAndCheck.trainForBuild("zh", sampleStream, params, contextGen);
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
	 * @param sampleStream 样本流
	 * @param params 训练模型需要设置的参数
	 * @param contextGen 特征
	 * @return 模型和模型信息的包裹结果
	 * @throws IOException 
	 */
	public static ModelWrapper trainForBuild(String languageCode, ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sampleStream, TrainingParameters params,
			SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen) throws IOException {
		String beamSizeString = params.getSettings().get(SyntacticAnalysisBeamSearch.BEAM_SIZE_PARAMETER);
		int beamSize = SyntacticAnalysisMEForBuildAndCheck.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }
        ClassificationModel buildModel = null;
        Map<String, String> manifestInfoEntries = new HashMap<String, String>();
        TrainerType trainerType = TrainerFactory.getTrainerType(params.getSettings());
        if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType)) {
            ObjectStream<Event> buildes = new SyntacticAnalysisSampleEventForBuild(sampleStream, contextGen);
            EventTrainer buildtrainer = TrainerFactory.getEventTrainer(params.getSettings(),
                    manifestInfoEntries);
            buildModel = buildtrainer.train(buildes);   
        }

        return new ModelWrapper(buildModel, beamSize);
	}

	/**
	 * 训练build模型，并将模型写出
	 * @param file 训练的文本
	 * @param buildmodelFile 模型文件
	 * @param params 训练模型需要设置的参数
	 * @param contextGen 上下文 产生器
	 * @param encoding 编码方式
	 * @param aghw 生成头结点的方法
	 * @return
	 */
	public static ModelWrapper trainForBuild(File file, File buildmodelFile, 
			TrainingParameters params,
			SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen, String encoding, AbstractGenerateHeadWords aghw) {
		OutputStream modelOut = null;
		ModelWrapper model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(file), encoding);
			ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sampleStream = new SyntacticAnalysisSampleStream(lineStream,aghw);
			model = SyntacticAnalysisMEForBuildAndCheck.trainForBuild("zh", sampleStream, params, contextGen);
			modelOut = new BufferedOutputStream(new FileOutputStream(buildmodelFile));           
            model.serialize(modelOut);
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

	/**
	 * 训练模型
	 * @param file 训练文件
	 * @param params 训练模型的参数配置信息
	 * @param contextGen 特征
	 * @param encoding 编码
	 * @param aghw 生成头结点的方法
	 * @return 模型和模型信息的包裹结果
	 */
	public static ModelWrapper trainForCheck(File file, TrainingParameters params, SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen,
			String encoding, AbstractGenerateHeadWords aghw){
		ModelWrapper model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(file), encoding);
			ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sampleStream = new SyntacticAnalysisSampleStream(lineStream,aghw);
			model = SyntacticAnalysisMEForBuildAndCheck.trainForCheck("zh", sampleStream, params, contextGen);
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
	 * @param sampleStream 样本流
	 * @param params 训练模型的参数配置信息
	 * @param contextGen 特征
	 * @return 模型和模型信息的包裹结果
	 * @throws IOException 
	 */
	public static ModelWrapper trainForCheck(String languageCode, ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sampleStream, TrainingParameters params,
			SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen) throws IOException {
		String beamSizeString = params.getSettings().get(SyntacticAnalysisBeamSearch.BEAM_SIZE_PARAMETER);
		int beamSize = SyntacticAnalysisMEForBuildAndCheck.DEFAULT_BEAM_SIZE;
        if (beamSizeString != null) {
            beamSize = Integer.parseInt(beamSizeString);
        }
        ClassificationModel checkModel = null;
        Map<String, String> manifestInfoEntries = new HashMap<String, String>();
        //event_model_trainer
        TrainerType trainerType = TrainerFactory.getTrainerType(params.getSettings());
        if (TrainerType.EVENT_MODEL_TRAINER.equals(trainerType)) { 
            sampleStream.reset();
            ObjectStream<Event> checkes = new SyntacticAnalysisSampleEventForCheck(sampleStream, contextGen);
            EventTrainer checktrainer = TrainerFactory.getEventTrainer(params.getSettings(),
                    manifestInfoEntries);
            checkModel = checktrainer.train(checkes); 
        }

        return new ModelWrapper(checkModel, beamSize);
	}

	/**
	 * 训练模型，并将模型写出
	 * @param file 训练的文本
	 * @param checkmodelFile 模型文件
	 * @param params 训练模型的参数配置信息
	 * @param contextGen 上下文 产生器
	 * @param encoding 编码方式
	 * @param aghw 生成头结点的方法
	 * @return
	 */
	public static ModelWrapper trainForCheck(File file, File checkmodelFile, 
			TrainingParameters params,
			SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen, String encoding, AbstractGenerateHeadWords aghw) {
		OutputStream modelOut = null;
		ModelWrapper model = null;
		try {
			ObjectStream<String> lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(file), encoding);
			ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sampleStream = new SyntacticAnalysisSampleStream(lineStream, aghw);
			model = SyntacticAnalysisMEForBuildAndCheck.trainForBuild("zh", sampleStream, params, contextGen);
			modelOut = new BufferedOutputStream(new FileOutputStream(checkmodelFile));           
            model.serialize(modelOut);
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

	
	/**
	 * 得到最好的K个最好结果的树,List中每一个值都是一颗完整的树
	 * @param k 结果数目
	 * @param chunkTree chunk标记树
	 * @param ac
	 * @return
	 */
	public List<HeadTreeNode> tagBuildAndCheck(int k, List<List<HeadTreeNode>> chunkTree, Object[] ac){
		List<HeadTreeNode> buildAndCheckTree = new ArrayList<>();
		SyntacticAnalysisSequenceForBuildAndCheck<HeadTreeNode>[] sequences = this.model.bestSequencesForBuildAndCheck(k, chunkTree, ac, contextGenerator, sequenceValidator);
		if(sequences == null){
			return null;
		}else{
			for (int i = 0; i < sequences.length; i++) {
				buildAndCheckTree.add(sequences[i].getTree().get(0));
			}
			return buildAndCheckTree;
		}
	}
	
	/**
	 * 得到最好的K个完整的动作序列
	 * @param k 结果数
	 * @param chunkTree k个chunk子树序列
	 * @param ac
	 * @return
	 * @throws CloneNotSupportedException 
	 */
	public List<List<String>> tagKactions(int k, List<List<HeadTreeNode>> chunkTree, Object[] ac) throws CloneNotSupportedException{
		List<List<String>> kActions = new ArrayList<>();
		List<HeadTreeNode> alltree= tagBuildAndCheck(k,chunkTree,null);
		if(alltree == null){
			return null;
		}else{
			for (int i = 0; i < alltree.size(); i++) {
				TreeNode node = BracketExpUtil.generateTree("("+alltree.get(i).toBracket()+")");
				HeadTreeNode headTree = TreeToHeadTree.treeToHeadTree(node,aghw);
				SyntacticAnalysisSample<HeadTreeNode> sample = HeadTreeToActions.headTreeToAction(headTree,aghw);
				kActions.add(sample.getActions());	
			}
			return kActions;
		}
	}
	
	/**
	 * 得到最好的K个完整的动作序列
	 * @param k 结果数
	 * @param chunkTree k个chunk子树序列
	 * @param ac
	 * @return
	 * @throws CloneNotSupportedException 
	 */
	public List<String> tagActions(int k, List<List<HeadTreeNode>> chunkTree, Object[] ac) throws CloneNotSupportedException{
		List<List<String>> kActions = tagKactions(1,chunkTree,null);
		return kActions.get(0);
	}
	
	/**
	 * 得到最好的树
	 * @param chunkTree chunk标记树
	 * @param ac
	 * @return
	 */
	public HeadTreeNode tagBuildAndCheck(List<List<HeadTreeNode>> chunkTree, Object[] ac){
		List<HeadTreeNode> buildAndCheckTree = tagBuildAndCheck(1,chunkTree, ac);
		if(buildAndCheckTree == null){
			return null;
		}else{
			return buildAndCheckTree.get(0);
		}
	}
	/**
	 * 得到句法树
	 * @param chunkTree chunk子树序列
	 * @return
	 */
	public ConstituentTree syntacticTree(List<HeadTreeNode> chunkTree) {
		List<List<HeadTreeNode>> allTree = new ArrayList<>();
		allTree.add(chunkTree);
		HeadTreeNode headTreeNode = tagBuildAndCheck(allTree,null);
		ConstituentTree constituent = new ConstituentTree();
		constituent.setRoot(headTreeNode);
		return constituent;
	}
	
	/**
	 * 由chunk子树得到K颗最好的成分树
	 * @param k 最好的成分树的个数
	 * @param chunkTree chunk子树序列
	 * @return
	 */
	public ConstituentTree[] syntacticTree(int k, List<HeadTreeNode> chunkTree) {
		List<List<HeadTreeNode>> allTree = new ArrayList<>();
		allTree.add(chunkTree);
		List<HeadTreeNode> headTreeNode = tagBuildAndCheck(k,allTree,null);
		List<ConstituentTree> constituent = new ArrayList<>();
		for (int i = 0; i < headTreeNode.size(); i++) {
			ConstituentTree con = new ConstituentTree();
			con.setRoot(headTreeNode.get(i));
			constituent.add(con);
		}
		return constituent.toArray(new ConstituentTree[constituent.size()]);
	}

	/**
	 * 得到句法树
	 * @param words 分词序列
	 * @param poses 词性标记
	 * @return
	 */
	@Override
	public ConstituentTree parseTree(String[] words, String[] poses) {
		return parseKTree(1,words,poses)[0];
	}
	
	/**
	 * 得到句法树
	 * @param words 分词序列
	 * @return
	 */
	@Override
	public ConstituentTree parseTree(String[] words) {
		return parseKTree(1,words)[0];
	}
	
	/**
	 * 得到最好的K个句法树
	 * @param k 最好的K个结果
	 * @param words 词语
	 * @param poses 词性标记
	 * @return
	 */
	@Override
	public ConstituentTree[] parseKTree(int k, String[] words, String[] poses) {
        //转换成chunk子树序列
		//(1)将poses转换成二维
		String[][] kposes = new String[1][poses.length];
		for (int i = 0; i < kposes.length; i++) {
			for (int j = 0; j < kposes[i].length; j++) {
				kposes[i][j] = poses[j];
			}
		}
		List<List<HeadTreeNode>> posTree = SyntacticAnalysisSample.toPosTree(words, kposes);
		List<List<HeadTreeNode>> chunkTree = chunktagger.tagKChunk(k, posTree, null);
		List<HeadTreeNode> headTreeNode = tagBuildAndCheck(k,chunkTree,null);
		List<ConstituentTree> constituent = new ArrayList<>();
		for (int i = 0; i < headTreeNode.size(); i++) {
			ConstituentTree con = new ConstituentTree();
			con.setRoot(headTreeNode.get(i));
			constituent.add(con);
		}
		return constituent.toArray(new ConstituentTree[constituent.size()]);
	}
	
	/**
	 * 得到最好的K个句法树
	 * @param k 最好的K个结果
	 * @param words 分词序列
	 * @return
	 */
	@Override
	public ConstituentTree[] parseKTree(int k, String[] words) {
		// 得到K个最好的词性标注序列
		String[][] kposes = postagger.tag(words,k);
		List<List<HeadTreeNode>> posTree = SyntacticAnalysisSample.toPosTree(words, kposes);
		//转换成chunk子树序列
		List<List<HeadTreeNode>> chunkTree = chunktagger.tagKChunk(k, posTree, null);
		List<HeadTreeNode> headTreeNode = tagBuildAndCheck(k,chunkTree,null);
		List<ConstituentTree> constituent = new ArrayList<>();
		for (int i = 0; i < headTreeNode.size(); i++) {
			ConstituentTree con = new ConstituentTree();
			con.setRoot(headTreeNode.get(i));
			constituent.add(con);
		}
		return constituent.toArray(new ConstituentTree[constituent.size()]);
	}
}

