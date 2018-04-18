package com.lc.nlp4han.segment;

import java.io.IOException;
import java.io.InputStream;

import com.lc.nlp4han.ml.util.ModelWrapper;
import com.lc.nlp4han.segment.maxent.WordSegContextGeneratorConf;
import com.lc.nlp4han.segment.maxent.WordSegmenterME;

public class WordSegFactory
{
	private WordSegFactory()
	{
	}

	public static WordSegmenter getWordSegmenter() throws IOException
	{
		String modelName = "com/lc/nlp4han/segment/ws-pku-gb.model";

		InputStream modelIn = WordSegFactory.class.getClassLoader().getResourceAsStream(modelName);
		ModelWrapper model = new ModelWrapper(modelIn);

		WordSegmenterME segmenter = new WordSegmenterME(model, new WordSegContextGeneratorConf());

		return segmenter;
	}

	public static void main(String[] args) throws IOException
	{
		String text = "我喜欢自然语言处理。";

		WordSegmenter segmenter = WordSegFactory.getWordSegmenter();
		String[] words = segmenter.segment(text);
		for (String w : words)
			System.out.println(w);
	}
}
