package com.lc.nlp4han.dependency.tb;

import java.util.ArrayDeque;
import java.util.ArrayList;

import com.lc.nlp4han.dependency.DependencySample;

import java.util.ArrayDeque;
import java.util.ArrayList;


public class Configuration {
	private ArrayDeque<Vertice> stack = new ArrayDeque<Vertice>();
	private ArrayList<Vertice> wordsBuffer = new ArrayList<Vertice>(); 
	private ArrayList<Arc> arcs = new ArrayList<Arc>();
	
	public Configuration(ArrayDeque<Vertice> stack,ArrayList<Vertice> wordsBuffer,ArrayList<Arc> arcs) {
		
		
		stack.add( wordsBuffer.get(0));
		wordsBuffer.remove(0);
		this.stack = stack;
		this.wordsBuffer = wordsBuffer;
		this.arcs = arcs;
	}

	public Configuration(String[] words, String[] pos) {
		wordsBuffer = Vertice.getWordsBuffer(words, pos); 
		stack.add( wordsBuffer.get(0));
		wordsBuffer.remove(0);
	}
	
	private Configuration() {}
	
//	public static Configuration initialConf(DependencySample sample) {//通过sample得到初始的一个Configuration
//		//······
//		return new Configuration();
//	}
	/**
	 * 产生一个sample初始的Configuration
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
	public static Configuration initialConf(String[] words, String[] pos) {
		return new Configuration(words,pos);
	}
	
	public boolean isFinalConf() {
		if(stack.size() == 1 && wordsBuffer.isEmpty())
			return true;
		else
			return false;
	}
	
	//共四类基本操作RIGHTARC_SHIFT、LEFTARC_REDUCE_SHIFT、SHIFT、REDUCE
	public Configuration transition(ActionType actType) {
		switch(actType.getBaseAction()) {
		case "RIGHTARC_SHIFT": 
			return addArc(new Arc(actType.getRelation(),stack.peek(),wordsBuffer.get(0)));
		case "LEFTARC_REDUCE_SHIFT":
			return addArc(new Arc(actType.getRelation(),wordsBuffer.get(0),stack.peek())).reduce().shift();
		case "SHIFT":
			return shift();
		case "REDUCE":
			return reduce();
		default:
			throw new IllegalArgumentException("参数不合法!");
		}
	}
	
	
	public Configuration addArc(Arc arc) {
		arcs.add(arc);
		return this;
	}
	
	public Configuration shift() {
		if (wordsBuffer.size() == 0) {
			stack.push(wordsBuffer.remove(0));
			return this;
		}else {
			return null;//?
		}
		
	}
	
	public Configuration reduce() {
		if (!stack.isEmpty()) {
			stack.pop();
			return this;
		}else {
			return null;
		}
		
	}
	
	public ArrayDeque<Vertice> getStack() {
		return stack;
	}

	public void setStack(ArrayDeque<Vertice> stack) {
		this.stack = stack;
	}

	public ArrayList<Vertice> getWordsBuffer() {
		return wordsBuffer;
	}

	public void setWordsBuffer(ArrayList<Vertice> wordsBuffer) {
		this.wordsBuffer = wordsBuffer;
	}

	public ArrayList<Arc> getArcs() {
		return arcs;
	}

	public void setArcs(ArrayList<Arc> arcs) {
		this.arcs = arcs;
	}
	
}
