package com.lc.nlp4han.pos;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/**
 * 将词性标注语料分隔成训练语料和测试语料
 * 
 * 从训练语料中取一部分作为测试语料
 * 
 * @author 刘小峰
 *
 */
public class CorpusSplit
{
    private static void usage()
    {
        System.out.println(CorpusSplit.class.getName() + " <corpusFile> [percent] [encoding]");
    }

    public static void main(String[] args) throws IOException
    {
        if (args.length < 1)
        {
            usage();
            return;
        }

        String source = args[0];
        int percent = 10;
        String encoding = "GBK";
        
        if(args.length > 1)
            percent = Integer.parseInt(args[1]);
        
        if(args.length > 2)
            encoding = args[2];
        
        String trainFile = "train-pos.txt";
        String testFile = "test-pos.txt";

        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(source), encoding));
        String sentence = null;
        PrintWriter train = new PrintWriter(new OutputStreamWriter(new FileOutputStream(trainFile), encoding));
        PrintWriter test = new PrintWriter(new OutputStreamWriter(new FileOutputStream(testFile), encoding));
        int n = 0;
        while ((sentence = in.readLine()) != null)
        {
            n++;
            if(n% percent == 0)
                test.append(sentence + "\n");
            else
                train.append(sentence + "\n");
        }

        train.close();
        test.close();
        in.close();
    }
}
