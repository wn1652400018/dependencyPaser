package com.lc.nlp4han.dependency.tb;

import java.io.File;
import java.io.IOException;

import com.lc.nlp4han.dependency.DependencySample;
import com.lc.nlp4han.dependency.DependencySampleParser;
import com.lc.nlp4han.dependency.DependencySampleParserCoNLL;
import com.lc.nlp4han.dependency.DependencySampleStream;
import com.lc.nlp4han.dependency.PlainTextBySpaceLineStream;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.util.MarkableFileInputStreamFactory;
import com.lc.nlp4han.ml.util.ObjectStream;

/**
 * @author 作者
 * @version 创建时间：2018年7月25日 下午5:21:36 类说明
 */
public class EventStreamTest
{

	public static void main(String[] args)
	{

		String path = "C:\\Users\\hp\\Desktop\\eventTest.conll";// 训练样本子集
		try
		{
			ObjectStream<String> lineStream = new PlainTextBySpaceLineStream(
					new MarkableFileInputStreamFactory(new File(path)), "UTF-8");
			DependencySampleParser sampleParser = new DependencySampleParserCoNLL();
			ObjectStream<DependencySample> sampleStream = new DependencySampleStream(lineStream, sampleParser);
			DependencySample sample = sampleStream.read();
			while (sample != null)
			{
				new DependencySampleEventStreamTB(sampleStream, new DependencyParseContextGeneratorConf())
						.createEvents(sample);
				sample = sampleStream.read();
			}
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}

}
