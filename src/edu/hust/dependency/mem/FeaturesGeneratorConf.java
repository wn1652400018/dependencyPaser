package edu.hust.dependency.mem;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;


/**
 * 
 * 根据配置文件的信息提取特征
 * TODO: 提取特征
 * @author 王宁
 *
 */
public class FeaturesGeneratorConf {
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
	public FeaturesGeneratorConf() throws IOException{
		Properties featureConf = new Properties();
        InputStream featureStream = FeaturesGeneratorConf.class.getClassLoader().getResourceAsStream("com/lc/nlp4han/dependency/feature.properties");
        featureConf.load(featureStream);
        
//        init(featureConf);
	}
	
	/**
	 * 有参构造
	 * @param config 配置文件
	 */
	public FeaturesGeneratorConf(Properties config){
//		init(config);
	}
	
	
	
	public String[] getContext(int indexi, int indexj, String[] words, String[] pos, Object[] ac) {
		return null;
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
