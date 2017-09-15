package com.lc.nlp4han.pos;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * 词性标注语料统计程序
 * 
 * 词和词性间用_分隔
 * 
 * @author 刘小峰
 *
 */
public class CorpusStat
{
    private static void usage()
    {
        System.out.println(CorpusStat.class.getName() + " <corpusFile> [encoding]");
    }

    public static void main(String[] args) throws IOException
    {
        if (args.length < 1)
        {
            usage();
            return;
        }

        String source = args[0];
        String encoding = "GBK";

        if (args.length > 1)
            encoding = args[1];

        BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream(source), encoding));
        String sentence = null;
        int nSentences = 0;
        int nWordTokens = 0;
        int nChars = 0;
        HashMap<String, HashSet<String>> word2Tags = new HashMap<String, HashSet<String>>();
        HashMap<String, Integer> tag2Count = new HashMap<String, Integer>();

        while ((sentence = in.readLine()) != null)
        {
            nSentences++;

            String[] wordtags = sentence.split("\\s");

            for (int i = 0; i < wordtags.length; i++)
            {
                String wordtag = wordtags[i];

                int pos = wordtag.lastIndexOf("_");

                if (pos < 0)
                    continue;

                String word = wordtag.substring(0, pos);
                String tag = wordtag.substring(pos + 1);

                nWordTokens++;

                nChars += word.length();

                if (!word2Tags.containsKey(word))
                {
                    HashSet<String> tags = new HashSet<String>();
                    tags.add(tag);

                    word2Tags.put(word, tags);
                }
                else
                {
                    HashSet<String> tags = word2Tags.get(word);
                    tags.add(tag);

                    word2Tags.put(word, tags);
                }

                if (!tag2Count.containsKey(tag))
                {
                    tag2Count.put(tag, 1);
                }
                else
                {
                    int n = tag2Count.get(tag);
                    n++;

                    tag2Count.put(tag, n);
                }
            }

        }

        in.close();

        System.out.println("句子数: " + nSentences);
        System.out.println("词条数: " + nWordTokens);
        System.out.println("词形数: " + word2Tags.size());
        System.out.println("字数: " + nChars);
        System.out.println("词性数: " + tag2Count.size());

        for (Map.Entry<String, Integer> e : tag2Count.entrySet())
            System.out.println(e.getKey() + "\t" + e.getValue());

        for (Map.Entry<String, HashSet<String>> e : word2Tags.entrySet())
        {
            if(e.getValue().size() > 1)
            {
                System.out.print(e.getKey() + ": ");
                
                for(String t : e.getValue())
                    System.out.print(t + "\t");
                
                System.out.println();
            }
        }
    }
}
