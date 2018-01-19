package com.lc.nlp4han.dependency;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ml.util.ObjectStream;
import com.lc.nlp4han.ml.util.TrainingParameters;

/**
 * 依存解析评价应用
 * 
 * @author 刘小峰
 *
 */
public class DependencyEvalTool
{
    public static void eval(File trainFile, TrainingParameters params, File goldFile, String encoding, File errorFile) throws IOException
    {
        DependencyParseContextGenerator gen = new DependencyParseContextGeneratorConf();
        
        ModelWrapper model = DependencyParserME.train(trainFile, params, gen, encoding);
        
        DependencyParserME tagger = new DependencyParserME(model,gen);
             
         //最大概率中不包含null的情况   
         DependencyParseMeasure measureNoNull = new DependencyParseMeasure();
         DependencyParseEvaluatorNoNull evaluatorNoNull = null;
         DependencyParseErrorPrinter printerNoNull = null;
         if(errorFile != null){
             printerNoNull = new DependencyParseErrorPrinter(new FileOutputStream(errorFile));        
             evaluatorNoNull = new DependencyParseEvaluatorNoNull(tagger,printerNoNull);
         }else{
             evaluatorNoNull = new DependencyParseEvaluatorNoNull(tagger);
         }
         evaluatorNoNull.setMeasure(measureNoNull);
         
         ObjectStream<String> linesStreamNoNull = new PlainTextBySpaceLineStream(new MarkableFileInputStreamFactory(goldFile), encoding);
         DependencySampleParser sampleParser = new DependencySampleParserCoNLL();
         ObjectStream<DependencySample> sampleStreamNoNull = new DependencySampleStream(linesStreamNoNull, sampleParser);
         evaluatorNoNull.evaluate(sampleStreamNoNull);
         
         DependencyParseMeasure measureResNoNull = evaluatorNoNull.getMeasure();

         System.out.println(measureResNoNull);
    }

    private static void usage()
    {
        System.out.println(DependencyEvalTool.class.getName() + " -data <trainFile> -gold <goldFile> -encoding <encoding> [-error <errorFile>]" + " [-cutoff <num>] [-iters <num>]");
    }

    public static void main(String[] args) throws IOException
    {
        if (args.length < 1)
        {
            usage();

            return;
        }

        String trainFile = null;
        String goldFile = null;
        String errorFile = null;
        String encoding = null;
        int cutoff = 3;
        int iters = 100;
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-data"))
            {
                trainFile = args[i + 1];
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

        if (errorFile != null)
        {
            eval(new File(trainFile), params, new File(goldFile), encoding, new File(errorFile));
        }
        else
            eval(new File(trainFile), params, new File(goldFile), encoding, null);
    }
}
