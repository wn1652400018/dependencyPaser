package com.lc.nlp4han.segpos;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import opennlp.tools.util.MarkableFileInputStreamFactory;
import opennlp.tools.util.ObjectStream;
import opennlp.tools.util.PlainTextByLineStream;
import opennlp.tools.util.TrainingParameters;

/**
 * 分词和词性标注训练程序
 * 
 * @author 刘小峰
 *
 */
public class WordSegAndPosTrainerTool
{
    private static void usage()
    {
        System.out.println(WordSegAndPosTrainerTool.class.getName() + " -data <corpusFile> -model <modelFile> -encoding <encoding> " + "[-cutoff <num>] [-iters <num>]");
    }

    public static void main(String[] args) throws ClassNotFoundException, IOException, InstantiationException, IllegalAccessException
    {
        if (args.length < 1)
        {
            usage();

            return;
        }

        int cutoff = 3;
        int iters = 100;
        File corpusFile = null;
        File modelFile = null;
        String encoding = "UTF-8";
        for (int i = 0; i < args.length; i++)
        {
            if (args[i].equals("-data"))
            {
                corpusFile = new File(args[i + 1]);
                i++;
            }
            else if (args[i].equals("-model"))
            {
                modelFile = new File(args[i + 1]);
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

        WordSegAndPosParseContext parse = new WordSegAndPosParseContext(new WordSegAndPosParseOpen());
        ObjectStream<String> lineStream = new PlainTextByLineStream(new MarkableFileInputStreamFactory(corpusFile), encoding);
        ObjectStream<WordSegAndPosSample> sampleStream = new WordSegAndPosSampleStream(lineStream, parse);
        WordSegAndPosContextGenerator contextGen = new WordSegAndPosContextGeneratorConf();
        WordSegAndPosModel model = WordSegAndPosME.train("zh", sampleStream, params, contextGen);
        OutputStream modelOut = new BufferedOutputStream(new FileOutputStream(modelFile));
        model.serialize(modelOut);

    }
}
