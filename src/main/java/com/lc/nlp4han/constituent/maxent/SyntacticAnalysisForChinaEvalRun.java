package com.lc.nlp4han.constituent.maxent;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.lc.nlp4han.constituent.HeadTreeNode;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.PlainTextByLineStream;
import com.lc.nlp4han.ml.util.TrainingParameters;
import com.lc.nlp4han.segpos.WordSegAndPosContextGenerator;
import com.lc.nlp4han.segpos.WordSegAndPosContextGeneratorConf;
import com.lc.nlp4han.segpos.WordSegAndPosME;


/**
 * 中文句法分析评估类
 * @author 王馨苇
 *
 */
public class SyntacticAnalysisForChinaEvalRun {

	private static void usage(){
		System.out.println(SyntacticAnalysisForChinaEvalRun.class.getName() + 
				"-data <corpusFile> -type <algorithom>"
				+ "-gold <goldFile> -error <errorFile> -encoding <encoding>" + " [-cutoff <num>] [-iters <num>]");
	}
	
	public static void eval(File trainFile, TrainingParameters params, File goldFile, String encoding, File errorFile) throws IOException{
		long start = System.currentTimeMillis();
		SyntacticAnalysisContextGenerator<HeadTreeNode> contextGen = new SyntacticAnalysisContextGeneratorConf();
		ModelWrapper posmodel = new ModelWrapper(new File("data\\model\\pos\\posmodelbinary.bin"));
		
		ModelWrapper chunkmodel= SyntacticAnalysisMEForChunk.train(trainFile, params, contextGen, encoding);
		ModelWrapper buildmodel = SyntacticAnalysisMEForBuildAndCheck.trainForBuild(trainFile, params, contextGen, encoding);
		ModelWrapper checkmodel = SyntacticAnalysisMEForBuildAndCheck.trainForCheck(trainFile, params, contextGen, encoding);
        System.out.println("训练时间： " + (System.currentTimeMillis() - start));
        
        WordSegAndPosContextGenerator generator = new WordSegAndPosContextGeneratorConf();
		WordSegAndPosME postagger = new WordSegAndPosME(posmodel, generator);	
        SyntacticAnalysisMEForChunk chunktagger = new SyntacticAnalysisMEForChunk(chunkmodel,contextGen);
        SyntacticAnalysisMEForBuildAndCheck buildandchecktagger = new SyntacticAnalysisMEForBuildAndCheck(buildmodel,checkmodel,contextGen);
        
        SyntacticAnalysisMeasure measure = new SyntacticAnalysisMeasure();
        SyntacticAnalysisEvaluatorForChina evaluator = null;
        SyntacticAnalysisErrorPrinter printer = null;
        if(errorFile != null){
        	System.out.println("Print error to file " + errorFile);
        	printer = new SyntacticAnalysisErrorPrinter(new FileOutputStream(errorFile));    	
        	evaluator = new SyntacticAnalysisEvaluatorForChina(postagger,chunktagger,buildandchecktagger,printer);
        }else{
        	evaluator = new SyntacticAnalysisEvaluatorForChina(postagger,chunktagger,buildandchecktagger);
        }
        evaluator.setMeasure(measure);
        ObjectStream<String> linesStream = new PlainTextByLineStream(new FileInputStreamFactory(goldFile), encoding);
        ObjectStream<SyntacticAnalysisSample<HeadTreeNode>> sampleStream = new SyntacticAnalysisSampleStream(linesStream);
        evaluator.evaluate(sampleStream);
        SyntacticAnalysisMeasure measureRes = evaluator.getMeasure();
        System.out.println("标注时间： " + (System.currentTimeMillis() - start));
        System.out.println(measureRes);
        
	}
	
	public static void main(String[] args) throws IOException {
		if (args.length < 1){
            usage();
            return;
        }
        String trainFile = null;
        String goldFile = null;
        String errorFile = null;
        String encoding = "UTF-8";
        String type = "MAXENT";
        int cutoff = 3;
        int iters = 100;
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-data"))
            {
                trainFile = args[i + 1];
                i++;
            }
            else if (args[i].equals("-type"))
            {
                type = args[i + 1];
                i++;
            }
            else if (args[i].equals("-gold"))
            {
                goldFile = args[i + 1];
                i++;
            }
            else if (args[i].equals("-error"))
            {
                errorFile = args[i + 1];
                i++;
            }
            else if (args[i].equals("-encoding"))
            {
                encoding = args[i + 1];
                i++;
            }
            else if (args[i].equals("-cutoff"))
            {
                cutoff = Integer.parseInt(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-iters"))
            {
                iters = Integer.parseInt(args[i + 1]);
                i++;
            }
        }

        TrainingParameters params = TrainingParameters.defaultParams();
        params.put(TrainingParameters.CUTOFF_PARAM, Integer.toString(cutoff));
        params.put(TrainingParameters.ITERATIONS_PARAM, Integer.toString(iters));
        params.put(TrainingParameters.ALGORITHM_PARAM, type.toUpperCase());
        if (errorFile != null)
        {
            eval(new File(trainFile), params, new File(goldFile), encoding, new File(errorFile));
        }
        else
            eval(new File(trainFile), params, new File(goldFile), encoding, null);
	}
}
