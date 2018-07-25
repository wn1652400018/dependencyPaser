package com.lc.nlp4han.dependency.tb;

import java.io.IOException;
import java.util.List;

import com.lc.nlp4han.dependency.DependencySample;
import com.lc.nlp4han.ml.model.AbstractModel;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.model.Sequence;
import com.lc.nlp4han.ml.model.SequenceStream;
import com.lc.nlp4han.ml.util.ObjectStream;

/**
 * @author 作者
 * @version 创建时间：2018年7月25日 上午2:34:58 类说明
 */
public class DependencySampleSequenceStream implements SequenceStream
{
	private DependencyParseContextGenerator contextGenerator;
	private ObjectStream<DependencySample> samples;

	public DependencySampleSequenceStream(ObjectStream<DependencySample> samples,
			DependencyParseContextGenerator contextGenerator)
	{
		this.contextGenerator =contextGenerator;
		this.samples = samples;
	}

	@Override
	public Event[] updateContext(Sequence sequence, AbstractModel model)// 感知机算法使用
	{
		System.out.println("使用了updateContext方法");

		return null;
	}

	@Override
	public Sequence read() throws IOException
	{
		DependencySample sentenceSample = samples.read();
		if (sentenceSample != null)
		{
			DependencySampleEventStreamTB es =new DependencySampleEventStreamTB(samples,contextGenerator);
			List<Event> events = es.generateEvents(sentenceSample.getWords(),
					sentenceSample.getPos(), sentenceSample.getDependency(), sentenceSample.getDependencyWords(),
					sentenceSample.getDependencyIndices(), sentenceSample.getAditionalContext());
			Event[] allEvents = events.toArray(new Event[events.size()]);
			Sequence<DependencySample> sequence = new Sequence<DependencySample>(allEvents, sentenceSample);
			return sequence;
		}

		return null;
	}

	@Override
	public void reset() throws IOException, UnsupportedOperationException
	{

	}

	@Override
	public void close() throws IOException
	{

	}

}
