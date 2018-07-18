package com.lc.nlp4han.dependency.tb;

import java.util.ArrayList;

import com.lc.nlp4han.dependency.DependencySample;


public class Vertice {
	private String word;
	private String pos;
	private int indexOfWord;//在句子中的位置，第一个位置为0
	
	
	
	
	public Vertice(String word,String pos,int indexOfWord) {
		this.word = word;
		this.pos = pos;
		this.indexOfWord = indexOfWord;
	}
	
	public static ArrayList<Vertice> getWordsBuffer(DependencySample sample) {

		String[] words = sample.getWords();
		String[] pos = sample.getPos();
		
		return getWordsBuffer(words,pos);
	}
	
	public static ArrayList<Vertice> getWordsBuffer(String[] words,String[] pos) {

		ArrayList<Vertice> wordsBuffer = new ArrayList<Vertice>();
		for (int i = 0; i < words.length; i++) // words����λ��ŵ��ǡ����ġ�
			wordsBuffer.add(new Vertice(words[i], pos[i], i));
		return wordsBuffer;
	}
	
	public String getWord() {
		return word;
	}
	public String getPos() {
		return pos;
	}
	public int getIndexOfWord() {
		return indexOfWord;
	}
	
}
