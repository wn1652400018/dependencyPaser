package com.lc.nlp4han.dependency.tb;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.LinkedList;

/**
* @author 作者
* @version 创建时间：2018年8月19日 上午9:59:53
* 类说明
*/
public class Configuration_ArcStandard
{
	private ArrayDeque<Vertice> stack = new ArrayDeque<Vertice>();
	private LinkedList<Vertice> wordsBuffer = new LinkedList<Vertice>();
	private ArrayList<Arc> arcs = new ArrayList<Arc>();

	public Configuration_ArcStandard(ArrayDeque<Vertice> stack, LinkedList<Vertice> wordsBuffer, ArrayList<Arc> arcs)
	{

		stack.push(wordsBuffer.get(0));
		wordsBuffer.remove(0);
		this.stack = stack;
		this.wordsBuffer = wordsBuffer;
		this.arcs = arcs;
	}

	public Configuration_ArcStandard(String[] words, String[] pos)
	{
		if (words.length != 0)
		{
			wordsBuffer = Vertice.getWordsBuffer(words, pos);
			stack.push(wordsBuffer.get(0));
			wordsBuffer.remove(0);
		}
	}
	
	public Configuration_ArcStandard()
	{
	}
	
	public static Configuration_ArcEager generateConfByActions(String[] wordpos, String[] priorActions) {
		//暂时先管这个
		return null;
	}
	
	public static Configuration_ArcEager initialConf(String[] words, String[] pos)
	{
		return new Configuration_ArcEager(words, pos);
	}
	
	public boolean isFinalConf()
	{
		if (wordsBuffer.isEmpty() && stack.size() == 1)
			return true;
		else
			return false;
	}
	
	/**
	 * 当栈顶元素和buffer第一个单词没有关系时，判断是否reduce
	 * 
	 * @return 有关系返回true
	 */
	public boolean canReduce(String[] dependencyIndices) {
		return false;
	}
	
	//共三类基本操作RIGHTARC_REDUCE、LEFTARC_REDUCE、SHIFT
	public Configuration_ArcStandard transition(ActionType actType) {
		switch (actType.getBaseAction())
		{
		case "RIGHTARC_REDUCE":
//			return addArc(new Arc(actType.getRelation(), stack.peek(), wordsBuffer.get(0))).reduce();
		case "LEFTARC_REDUCE":
//			return addArc(new Arc(actType.getRelation(), wordsBuffer.get(0), stack.peek())).reduce();
		case "SHIFT":
			return shift();
		default:
			throw new IllegalArgumentException("参数不合法!");
		}
	}
	
	public Configuration_ArcStandard addArc(Arc arc)
	{
		arcs.add(arc);
		return this;
	}
	
	public Configuration_ArcStandard shift()
	{
		if (wordsBuffer.size() != 0)
		{
			stack.push(wordsBuffer.remove(0));
			return this;
		}
		else
		{
			return null;// ?
		}

	}
	
	public Configuration_ArcStandard reduce()
	{
	return null;

	}
	
	
	
	
	public String toString()
	{
		Vertice[] vS = stack.toArray(new Vertice[stack.size()]);
		Vertice[] vB = wordsBuffer.toArray(new Vertice[wordsBuffer.size()]);
		StringBuilder stackStr = new StringBuilder();
		StringBuilder bufferStr = new StringBuilder();
		for (int i = 0; i < stack.size(); i++)
		{
			stackStr.append(vS[stack.size() - i - 1].toString() + " ");
		}
		for (int i = 0; i < wordsBuffer.size(); i++)
		{
			bufferStr.append(vB[i].toString() + " ");
		}

		return "栈底至栈顶元素：" + stackStr.toString() + " ___" + "buffer:" + bufferStr.toString();
	}

	public String arcsToString()
	{
		StringBuilder allArc = new StringBuilder();
		allArc.append("arcs:" + "\r\n");
		for (int i = 0; i < arcs.size(); i++)
		{
			allArc.append(arcs.get(arcs.size() - i - 1).toString() + "\r\n");
		}
		return allArc.toString();
	}

	public ArrayDeque<Vertice> getStack()
	{
		return stack;
	}

	public void setStack(ArrayDeque<Vertice> stack)
	{
		this.stack = stack;
	}

	public LinkedList<Vertice> getWordsBuffer()
	{
		return wordsBuffer;
	}

	public void setWordsBuffer(LinkedList<Vertice> wordsBuffer)
	{
		this.wordsBuffer = wordsBuffer;
	}

	public ArrayList<Arc> getArcs()
	{
		return arcs;
	}

	public void setArcs(ArrayList<Arc> arcs)
	{
		this.arcs = arcs;
	}
}
