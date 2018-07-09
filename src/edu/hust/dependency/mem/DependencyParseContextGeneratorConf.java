package edu.hust.dependency.mem;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Properties;

import edu.hust.dependency.arceager.Arc;
import edu.hust.dependency.arceager.Configuration;
import edu.hust.dependency.arceager.Vertice;


/**
 * 
 * 根据配置文件的信息提取特征
 * TODO: 提取特征
 * @author 王宁
 *
 */
public class DependencyParseContextGeneratorConf {
	//定义变量控制feature的使用
	//一个单词特征
	private boolean s1wset = false;
	private boolean s1tset = false;
	private boolean s1wtset = false;
	
	private boolean s2wset = false;
	private boolean s2tset = false;
	private boolean s2wtset = false;
	
	
	private boolean b1wset = false;
	private boolean b1tset = false;
	private boolean b1wtset = false;
	
	private boolean b2wset = false;
	private boolean b2tset = false;
	private boolean b2wtset = false;
	
	//两个单词特征
	private boolean s1w_b1wset = false;
	private boolean s1t_b1tset = false;
	private boolean s1t_b1wtset = false;
	private boolean s1w_b1wtset = false;
	private boolean s1wt_b1wset = false;
	private boolean s1wt_b1tset = false;
	private boolean s1wt_b1wtset = false;
	
	
	
	/**
	 * 无参构造
	 * @throws IOException IO异常
	 */
	public DependencyParseContextGeneratorConf() throws IOException{
		Properties featureConf = new Properties();
        InputStream featureStream = DependencyParseContextGeneratorConf.class.getClassLoader().getResourceAsStream("edu/hust/dependency/mem/features.properties");
        featureConf.load(featureStream);
        
//        init(featureConf);
	}
	
	/**
	 * 有参构造
	 * @param config 配置文件
	 */
	public DependencyParseContextGeneratorConf(Properties config){
//		init(config);
	}
	
	
	
	
	public String[] getContext(Configuration conf) {
		return getContext(new ArrayDeque<Vertice>(), new ArrayList<Vertice> (), new ArrayList<Arc>());
	}
	/**
	 * 获取特征
	 * @param Configuration的stack
	 * @param Configuration中的buffer
	 * @param Configuration中的关系列表
	 * @return 特征的数组
	 */
	public String[] getContext(ArrayDeque<Vertice> stack, ArrayList<Vertice> wordsBuffer, ArrayList<Arc> arcs) {
		return null;
	}
	
	
}
