package com.lc.nlp4han.pos.character;

import java.util.ArrayList;

import opennlp.tools.tokenize.WhitespaceTokenizer;

/**
 * 解析OpenNLP词性标注语料
 * 
 * @author 刘小峰
 *
 */
public class CharPOSParseOpen implements CharPOSParseStrage
{

    /**
     * 解析OpenNLP词性标注语料
     * 
     * @return WordSegPosSample对象
     */
    public CharPOSSample parse(String sampleSentence)
    {
        String[] wordsAndPoses = WhitespaceTokenizer.INSTANCE.tokenize(sampleSentence);

        ArrayList<String> characters = new ArrayList<String>();
        ArrayList<String> words = new ArrayList<String>();
        ArrayList<String> tags = new ArrayList<String>();
        ArrayList<String> poses = new ArrayList<String>();

        for (int i = 0; i < wordsAndPoses.length; i++)
        {
            String[] wordanspos = wordsAndPoses[i].split("_");
            String word = wordanspos[0];
            String pos = wordanspos[1];

            words.add(word);

            poses.add(pos);

            if (word.length() == 1)
            {
                characters.add(word);
                tags.add("S");
                continue;
            }

            for (int j = 0; j < word.length(); j++)
            {
                char c = word.charAt(j);
                if (j == 0)
                {
                    characters.add(c + "");
                    tags.add("B");
                }
                else if (j == word.length() - 1)
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

        return new CharPOSSample(characters, tags, words, poses);
    }

}
