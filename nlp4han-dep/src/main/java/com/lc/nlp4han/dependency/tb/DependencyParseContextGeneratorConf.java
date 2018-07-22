package com.lc.nlp4han.dependency.tb;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DependencyParseContextGeneratorConf implements DependencyParseContextGenerator {

	// 定义变量控制feature的使用
	// 一个单词特征
	private boolean s1wset;
	private boolean s1tset;
	private boolean s1wtset;

	private boolean s2wset;
	private boolean s2tset;
	private boolean s2wtset;

	private boolean b1wset;
	private boolean b1tset;
	private boolean b1wtset;

	private boolean b2wset;
	private boolean b2tset;
	private boolean b2wtset;

	// 两个单词特征
	private boolean s1w_b1wset;
	private boolean s1t_b1tset;
	private boolean s1t_b1wtset;
	private boolean s1w_b1wtset;
	private boolean s1wt_b1wset;
	private boolean s1wt_b1tset;
	private boolean s1wt_b1wtset;

	private void init(Properties config) {

		s1wset = config.getProperty("feature.s1w", "true").equals("true");
		s1tset = config.getProperty("feature.s1t", "true").equals("true");
		s1wtset = config.getProperty("feature.s1wt", "true").equals("true");
		s2wset = config.getProperty("feature.s2w", "true").equals("true");
		s2tset = config.getProperty("feature.s2t", "true").equals("true");
		s2wtset = config.getProperty("feature.s2wt", "true").equals("true");
		b1wset = config.getProperty("feature.b1w", "true").equals("true");
		b1tset = config.getProperty("feature.b1t", "true").equals("true");
		b1wtset = config.getProperty("feature.b1wt", "true").equals("true");
		b2wset = config.getProperty("feature.b2w", "true").equals("true");
		b2tset = config.getProperty("feature.b2t", "true").equals("true");
		b2wtset = config.getProperty("feature.b2wt", "true").equals("true");

		s1w_b1wset = config.getProperty("feature.s1w_b1w", "true").equals("true");
		s1t_b1tset = config.getProperty("feature.s1t_b1t", "true").equals("true");
		s1t_b1wtset = config.getProperty("feature.s1t_b1wt", "true").equals("true");
		s1w_b1wtset = config.getProperty("feature.s1w_b1wt", "true").equals("true");
		s1wt_b1wset = config.getProperty("feature.s1wt_b1w", "true").equals("true");
		s1wt_b1tset = config.getProperty("feature.s1wt_b1t", "true").equals("true");
		s1wt_b1wtset = config.getProperty("feature.s1wt_b1wt", "true").equals("true");
	}

	/**
	 * 无参构造
	 * 
	 * @throws IOException
	 *             IO异常
	 */
	public DependencyParseContextGeneratorConf() throws IOException {
		Properties featureConf = new Properties();
		InputStream featureStream = DependencyParseContextGeneratorConf.class.getClassLoader()
				.getResourceAsStream("com/lc/nlp4han/dependency/tbfeature.properties");
		featureConf.load(featureStream);

		init(featureConf);
	}

	/**
	 * 有参构造
	 * 
	 * @param config
	 *            配置文件
	 */
	public DependencyParseContextGeneratorConf(Properties config) {
		init(config);
	}

	public String[] getContext(Configuration conf) {
		String s1w, s1t, s2w, s2t, b1w, b1t, b2w, b2t;
		s1w = s1t = s2w = s2t = b1w = b1t = b2w = b2t = null;
		List<String> features = new ArrayList<String>();
		ArrayDeque<Vertice> stack = conf.getStack();
		List<Vertice> wordsBuffer = conf.getWordsBuffer();

		s1w = stack.peek().getWord();
		s1t = stack.peekLast().getPos();

		if (stack.size() >= 2) {
			Vertice vertice = stack.pop();
			s2w = stack.peek().getWord();
			s2t = stack.peek().getPos();
			stack.push(vertice);
		}
		if (wordsBuffer.size() >= 1) {
			b1w = wordsBuffer.get(0).getWord();
			b1t = wordsBuffer.get(0).getPos();
			if (wordsBuffer.size() >= 2) {
				b2w = wordsBuffer.get(1).getWord();
				b2t = wordsBuffer.get(1).getPos();
			}
		}

		if (s1wset)
			features.add("s1w=" + s1w);
		if (s1tset)
			features.add("s1t=" + s1t);

		if (s2wset && s2w != null)
			features.add("s2w=" + s2w);
		if (s2tset && s2t != null)
			features.add("s2t=" + s2t);
		if (b1wset)
			features.add("b1w=" + b1w);
		if (b1tset)
			features.add("b1t=" + b1t);
		if (b2wset && b2w != null)
			features.add("b2w=" + b2w);
		if (b2tset && b2t != null)
			features.add("b2t=" + b2t);
		if (s1wtset)
			features.add("s1wt=" + s1w + s1t);
		if (s2wtset && s2w != null && s2t != null)
			features.add("s2wt=" + s2w + s2t);
		if (b1wtset)
			features.add("b1wt=" + b1w + b1t);
		if (b2wtset && b2w != null && b2t != null)
			features.add("b2wt=" + b2w + b2t);
		if (s1w_b1wset)
			features.add("s1w_b1w=" + s1w + b1w);
		if (s1t_b1tset)
			features.add("s1t_b1t=" + s1t + b1t);
		if (s1t_b1wtset)
			features.add("s1t_b1wt=" + s1t + b1w + b1t);
		if (s1w_b1wtset)
			features.add("s1w_b1wt=" + s1w + b1w + b1t);
		if (s1wt_b1wset)
			features.add("s1wt_b1w=" + s1w + s1t + b1w + b1t);
		if (s1wt_b1tset)
			features.add("s1wt_b1t=" + s1w + s1t + b1t);
		if (s1wt_b1wtset)
			features.add("s1wt_b1wt=" + s1w + s1t + b1w + b1t);
		return features.toArray(new String[features.size()]);
	}
}
