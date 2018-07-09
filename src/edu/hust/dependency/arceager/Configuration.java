package edu.hust.dependency.arceager;

import java.util.ArrayDeque;
import java.util.ArrayList;

import edu.hust.dependencyParse.DependencySample;

public class Configuration {
	private ArrayDeque<Vertice> stack = new ArrayDeque<Vertice>();
	private ArrayList<Vertice> wordsBuffer = new ArrayList<Vertice>(); 
	private ArrayList<Arc> arcs = new ArrayList<Arc>();
	
	public Configuration(ArrayDeque<Vertice> stack,ArrayList<Vertice> wordsBuffer,ArrayList<Arc> arcs) {
		
		this.stack = stack;
		stack.add( new Vertice("核心","root",0) );
		this.wordsBuffer = wordsBuffer;
		this.arcs = arcs;
	}

	public Configuration(String[] words, String[] poses) {
		// stack <——root
		// wordsBuffer <——words,poses
		// arc为空
	}
	public Configuration() {}
	
	public static Configuration initialConf(DependencySample sample) {//通过sample得到初始的一个Configuration
		//······
		return null;
	}
	
	
	
	public boolean isFinalConf() {
		if(stack.size() == 1 && wordsBuffer.isEmpty())
			return true;
		else
			return false;
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
