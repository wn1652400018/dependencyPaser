package com.lc.nlp4han.dependency.tb;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.lc.nlp4han.dependency.tb.DependencyParseContextGenerator;
import com.lc.nlp4han.dependency.DependencySample;
import com.lc.nlp4han.ml.model.Event;
import com.lc.nlp4han.ml.util.AbstractEventStream;
import com.lc.nlp4han.ml.util.ObjectStream;

public class DependencySampleEventStreamTB extends AbstractEventStream<DependencySample>
{

	// 上下文产生器
	private DependencyParseContextGenerator pcg;

	/**
	 * 构造
	 * 
	 * @param samples
	 *            样本流
	 * @param pcg
	 *            特征
	 */
	public DependencySampleEventStreamTB(ObjectStream<DependencySample> samples, DependencyParseContextGenerator pcg)
	{

		super(samples);
		this.pcg = pcg;
	}

	/**
	 * 根据依存样本流创建事件
	 * 
	 * @param sample
	 *            依存样本
	 */
	@Override
	protected Iterator<Event> createEvents(DependencySample sample)
	{
		String[] words = sample.getWords();
		String[] pos = sample.getPos();
		String[] dependency = sample.getDependency();
		String[] dependencyWords = sample.getDependencyWords();
		String[] dependencyIndices = sample.getDependencyIndices();
		String[][] ac = sample.getAditionalContext();

		List<Event> events = generateEvents(words, pos, dependency, dependencyWords, dependencyIndices, ac);
		return events.iterator();
	}
	
	/**
	 * 产生对应的事件
	 * 
	 * @param words
	 *            词语
	 * @param pos
	 *            词性
	 * @param dependency
	 *            依存关系
	 * @param dependencyWords
	 *            依存词
	 * @param dependencyIndices
	 *            依存词的下标
	 * @param ac
	 *            额外的信息
	 * @return 事件列表
	 */
	private List<Event> generateEvents(String[] words, String[] pos, String[] dependency, String[] dependencyWords,
			String[] dependencyIndices, String[][] ac)
	{
		if(words.length == 0)
			return new ArrayList<Event>(words.length);;
		Configuration conf_ArcEager  = Configuration.initialConf(words, pos);
		List<Event> events  = new ArrayList<Event>();
		ActionType at ;
		String strOfAType;
		int indexOfWord_S1 ;//该单词在words中索引
		int indexOfWord_B1 ;
		int headIndexOfWord_S1 ;//栈顶单词中心词在words中的索引  
		int headIndexOfWord_B1 ;
		int count = 0;
		while(!conf_ArcEager.isFinalConf()&& count < 15) {
			System.out.println(conf_ArcEager.toString());
			count ++;
			String[] context = pcg.getContext(conf_ArcEager);
			if(conf_ArcEager.getStack().size() == 1 && conf_ArcEager.getWordsBuffer().size() != 0 ) {
				indexOfWord_B1 = conf_ArcEager.getWordsBuffer().get(0).getIndexOfWord();
				headIndexOfWord_B1 = Integer.parseInt(dependencyIndices[indexOfWord_B1 - 1]);
				if(headIndexOfWord_B1 == 0) {
					at = new ActionType("EXTRAROOT", "RIGHTARC_SHIFT");
					strOfAType = at.typeToString();
					conf_ArcEager.addArc(
							new Arc("EXTRAROOT", conf_ArcEager.getStack().peek(), conf_ArcEager.getWordsBuffer().get(0)));
					conf_ArcEager.shift();
				}else {
					at = new ActionType("null","SHIFT");
					conf_ArcEager.shift();
					strOfAType = at.typeToString();
				}
				
			}else if(conf_ArcEager.getStack().size() != 1 && conf_ArcEager.getWordsBuffer().size() == 0 ){
				at = new ActionType("null","REDUCE");
				strOfAType = at.typeToString();
				conf_ArcEager.reduce();
			}else {
				indexOfWord_S1 = conf_ArcEager.getStack().peek().getIndexOfWord();//该单词在words中索引
				indexOfWord_B1 = conf_ArcEager.getWordsBuffer().get(0).getIndexOfWord();
				headIndexOfWord_S1 = Integer.parseInt(dependencyIndices[indexOfWord_S1 - 1]);//栈顶单词中心词在words中的索引  
				headIndexOfWord_B1 = Integer.parseInt(dependencyIndices[indexOfWord_B1 - 1]);
				if(indexOfWord_S1 == headIndexOfWord_B1) {//右弧
					at =  new ActionType(dependency[indexOfWord_B1 - 1],"RIGHTARC_SHIFT");
					strOfAType = at.typeToString();
					conf_ArcEager.addArc(new Arc(dependency[indexOfWord_B1 - 1],conf_ArcEager.getStack().peek(),conf_ArcEager.getWordsBuffer().get(0)));
					conf_ArcEager.shift();
				} else if(indexOfWord_B1 == headIndexOfWord_S1 ) {//左弧
					at = new ActionType(dependency[indexOfWord_S1 - 1],"LEFTARC_REDUCE");
					strOfAType = at.typeToString();
					conf_ArcEager.addArc(new Arc(dependency[indexOfWord_S1 - 1],conf_ArcEager.getStack().peek(),conf_ArcEager.getWordsBuffer().get(0)));
					conf_ArcEager.reduce();
				} else {//没有关系
					at = new ActionType("null","SHIFT");
					strOfAType = at.typeToString();
					conf_ArcEager.shift(); 
				}
			}
			Event event = new Event(strOfAType,context);
			events.add(event);
		}
		return events;
	}
}