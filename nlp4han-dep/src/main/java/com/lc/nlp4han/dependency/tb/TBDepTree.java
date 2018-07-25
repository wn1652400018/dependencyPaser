package com.lc.nlp4han.dependency.tb;

import java.util.ArrayList;

import com.lc.nlp4han.dependency.DependencySample;
import com.lc.nlp4han.dependency.DependencyTree;

/**
 * @author 王宁
 * @version 创建时间：2018年7月24日 下午3:35:20 
 * 将解析来的conf解析为树和sample
 */
public class TBDepTree
{

	/**
	 * @param conf  解析得到的弧的配置
	 * @return 句子对应的依存树
	 */
	public static DependencyTree getTree(Configuration conf) {
		return getTree(conf.getArcs());
	}
	
	/**
	 * @param  arcs 解析得到的弧的list
	 * @return 句子对应的依存树
	 */
	public static DependencyTree getTree(ArrayList<Arc> arcs) {
		return new DependencyTree(getSample(arcs));
	}
	
	/**
	 * @param arcs 解析得到的弧的list
	 * @return 句子对应的DependencySample实例
	 */
	public static DependencySample getSample(ArrayList<Arc> arcs)
	{
		String[] words = new String[arcs.size() + 1];
		String[] poses = new String[arcs.size() + 1];
		String[] dependency = new String[arcs.size()];
		String[] dependencyWords = new String[arcs.size()];
		String[] dependencyIndices = new String[arcs.size()];

		for (Arc arc : arcs)
		{
			words[arc.getHead().getIndexOfWord()] = arc.getHead().getWord();
			words[arc.getDependent().getIndexOfWord()] = arc.getDependent().getWord();
			poses[arc.getHead().getIndexOfWord()] = arc.getHead().getPos();
			poses[arc.getDependent().getIndexOfWord()] = arc.getDependent().getPos();
			dependency[arc.getDependent().getIndexOfWord() - 1] = arc.getRelation();
			dependencyWords[arc.getDependent().getIndexOfWord() - 1] = arc.getHead().getWord();
			dependencyIndices[arc.getDependent().getIndexOfWord() - 1] = String.valueOf(arc.getHead().getIndexOfWord());
		}

		return new DependencySample(words, poses, dependency, dependencyWords, dependencyIndices);
	}

	public DependencySample getSample(Configuration conf) {
		return getSample(conf.getArcs());
	}
}
