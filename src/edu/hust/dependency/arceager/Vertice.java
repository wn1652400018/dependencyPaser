package edu.hust.dependency.arceager;

import java.util.ArrayList;

import edu.hust.dependency.DependencySample;

public class Vertice {
	private String word;
	private String pos;
	private int indexOfWord;
	
	
	
	
	public Vertice(String word,String pos,int indexOfWord) {
		this.word = word;
		this.pos = pos;
		this.indexOfWord = indexOfWord;
	}
	
	public static ArrayList<Vertice> getWordsBuffer(DependencySample sample) {

		String[] words = sample.getWords();
		String[] pos = sample.getPos();
		ArrayList<Vertice> wordsBuffer = new ArrayList<Vertice>();
		for (int i = 0; i < words.length; i++) // words的首位存放的是“核心”
			wordsBuffer.add(new Vertice(words[i], pos[i], i));
		return null;
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
