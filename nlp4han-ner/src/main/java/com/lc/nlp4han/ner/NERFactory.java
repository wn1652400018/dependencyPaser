package com.lc.nlp4han.ner;

import java.io.IOException;
import java.io.InputStream;

import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.ner.word.NERWord;
import com.lc.nlp4han.ner.word.NERWordContextGeneratorConf;
import com.lc.nlp4han.ner.word.NERWordME;

public class NERFactory
{

	private static NERWord ner;

	private NERFactory()
	{
	}

	/**
	 * 装入中文NER模型，并生成NER标注器
	 * 
	 * @return 中文NER
	 * @throws IOException
	 */
	public static NERWord getNERWordTagger() throws IOException
	{
		if (ner != null)
			return ner;

		String modelName = "com/lc/nlp4han/ner/ner-pd-word-gb.model";

		InputStream modelIn = NERFactory.class.getClassLoader().getResourceAsStream(modelName);
		ModelWrapper model = new ModelWrapper(modelIn);

		ner = new NERWordME(model, new NERWordContextGeneratorConf());

		return ner;
	}

	public static void main(String[] args) throws IOException
	{
		String words = "李 鹏 在 北京 视察 。";

		NERWord tagger = NERFactory.getNERWordTagger();
		NamedEntity[] tags = tagger.ner(words);
		for(int i=0; i<tags.length; i++)
			System.out.println(tags[i]);
	}

}
