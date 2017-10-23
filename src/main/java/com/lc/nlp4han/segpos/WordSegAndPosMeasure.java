package com.lc.nlp4han.segpos;

import java.util.HashMap;
import java.util.List;

/**
 * 计算指标
 * @author 王馨苇
 *
 */
public class WordSegAndPosMeasure {

	/**
     * |selected| = true positives + false positives <br>
     * 预测的样本数
     */
    private long selected;

    /**
     * |target| = true positives + false negatives <br>
     * 参考的样本数
     */
    private long target;

    /**
     * 预测正确的个数
     */
    private long truePositive;
    
    private long truePositiveIV;
    private long truePositiveOOV;
    private long targetIV;
    private long targetOOV;
    private long trueSegPositive;
    
    private HashMap<String,List<String>> dictionary;
    
    public WordSegAndPosMeasure(){
    	
    }
    
    /**
     * 有参构造
     * @param dictionary 训练语料得到的词典
     */
    public WordSegAndPosMeasure(HashMap<String,List<String>> dictionary){
    	this.dictionary = dictionary;
    }
    
	/**
	 * 更新计算指标的一些变量【词性标注的指标】
	 * @param wordsRef 参考的分词结果
	 * @param posesRef 参考的词性标记
	 * @param wordsPre 预测的分词结果
	 * @param posesPre 预测的词性标记
	 */
	public void updateSegAndPos(String[] wordsRef, String[] posesRef, String[] wordsPre, String[] posesPre) {
		//定义变量记录当前扫描的总长度
    	int countRef = 0,countPre = 0;
    	//记录当前所在的词的位置
    	int i = 0,j = 0;
    	//记录i的前一次的值
    	int iPre = -1;
    	if(wordsRef.length > 0 && wordsPre.length > 0){
    		while(wordsRef[i] != null || !("".equals(wordsRef[i]))|| wordsPre[j] != null || !("".equals(wordsPre[j]))){
    			boolean isIV = true;
    			if(iPre != i){
    				//先判断是否为登陆词未登录词,
            		if(dictionary != null){
            			//isIV = dictionary.get(wordsRef[i]);
            			if(dictionary.get(wordsRef[i]) != null){
            				isIV = true;
                            targetIV++;
            			}
                        else{
                        	targetOOV++;
                        	isIV = false;
                        }
                            
            		}
    			}
        		countRef += wordsRef[i].length();
        		countPre += wordsPre[j].length();
        		//匹配的情况
        		if((wordsRef[i] == wordsPre[j] || wordsRef[i].equals(wordsPre[j]))){
        			
        			trueSegPositive++;
        			if(posesRef[i] == posesPre[j] || posesRef[i].equals(posesPre[j])){
        					
        				truePositive++;        				
        			}       			
        			//正确分词，且在词典中匹配了的登陆词和未登陆词
    				if(dictionary!=null){
                        if(isIV)
                            truePositiveIV++;
                        else
                            truePositiveOOV++;
                    }
        			iPre = i;
    				//两个字符串同时向后扫描
        			i++;j++;
        			//为了防止：已经到达边界了，还用references[i]或者predictions[i]来判断，此时越界了
    				if(i >= wordsRef.length || j >= wordsPre.length)
    					break;
   
        		}else{
        			//不匹配的情况，则需要比较当前扫过的总长度
        			//（1）：长度长的那个不动，长度短的那个要继续向前扫描比较   			
        			if(countRef > countPre){
    					iPre = i;
        				j++;
    					countRef -= wordsRef[i].length();
    					if(j >= wordsPre.length)
    						break;
    					//（2）：长度相等的时候，二者都需要向前扫描
    				}else if(countRef == countPre){
    					iPre = i;
    					i++;j++;
    					if(i >= wordsRef.length || j >= wordsPre.length)
    						break;
    					//（1）：长度长的那个不动，长度短的那个要继续向前扫描比较
    				}else if(countRef < countPre){
    					iPre = i;
    					i++;
    					countPre -= wordsPre[j].length();
    					if(i >= wordsRef.length)
    						break;
    				}
        		}
        	}
    	}
        
		target += posesRef.length;
		selected += posesPre.length;	
		//return truePositives;
	}
	
	/**
	 * 更新计算指标的一些变量【分词的指标】
	 * @param wordsRef 参考的分词结果
	 * @param posesRef 参考的词性标记
	 * @param wordsPre 预测的分词结果
	 * @param posesPre 预测的词性标记
	 */
	public void updateTag(String[] wordsRef, String[] wordsPre) {
		//定义变量记录当前扫描的总长度
    	int countRef = 0,countPre = 0;
    	//记录当前所在的词的位置
    	int i = 0,j = 0;
    	//记录i的前一次的值
    	int iPre = -1;
    	if(wordsRef.length > 0 && wordsPre.length > 0){
    		while(wordsRef[i] != null || !("".equals(wordsRef[i]))|| wordsPre[j] != null || !("".equals(wordsPre[j]))){
    			boolean isIV = true;
    			if(iPre != i){
    				//先判断是否为登陆词未登录词,
            		if(dictionary != null){
            			//isIV = dictionary.get(wordsRef[i]);
            			if(dictionary.get(wordsRef[i]) != null){
            				isIV = true;
                            targetIV++;
            			}
                        else{
                        	targetOOV++;
                        	isIV = false;
                        }
                            
            		}
    			}
        		countRef += wordsRef[i].length();
        		countPre += wordsPre[j].length();
        		//匹配的情况
        		if((wordsRef[i] == wordsPre[j] || wordsRef[i].equals(wordsPre[j]))){
        			truePositive++;
        			//正确分词，且在词典中匹配了的登陆词和未登陆词
    				if(dictionary!=null){
                        if(isIV)
                            truePositiveIV++;
                        else
                            truePositiveOOV++;
                    }
        			iPre = i;
    				//两个字符串同时向后扫描
        			i++;j++;
        			//为了防止：已经到达边界了，还用references[i]或者predictions[i]来判断，此时越界了
    				if(i >= wordsRef.length || j >= wordsPre.length)
    					break;
   
        		}else{
        			//不匹配的情况，则需要比较当前扫过的总长度
        			//（1）：长度长的那个不动，长度短的那个要继续向前扫描比较   			
        			if(countRef > countPre){
    					iPre = i;
        				j++;
    					countRef -= wordsRef[i].length();
    					if(j >= wordsPre.length)
    						break;
    					//（2）：长度相等的时候，二者都需要向前扫描
    				}else if(countRef == countPre){
    					iPre = i;
    					i++;j++;
    					if(i >= wordsRef.length || j >= wordsPre.length)
    						break;
    					//（1）：长度长的那个不动，长度短的那个要继续向前扫描比较
    				}else if(countRef < countPre){
    					iPre = i;
    					i++;
    					countPre -= wordsPre[j].length();
    					if(i >= wordsRef.length)
    						break;
    				}
        		}
        	}
    	}
        
		target += wordsRef.length;
		selected += wordsPre.length;	
		//return truePositives;
	}
	
	
	/**
	 * 词性标注准确率
	 * @return
	 */
	public double getPrecisionScore() {
		return selected > 0 ? (double) truePositive / (double) selected : 0;
	}

	/**
	 * 词性标注召回率
	 * @return
	 */
	public double getRecallScore() { 
		return target > 0 ? (double) truePositive / (double) target : 0;
	}
	
	/**
	 * 分词准确率
	 * @return
	 */
	public double getPrecisionSegScore() {
		return selected > 0 ? (double) trueSegPositive / (double) selected : 0;
	}

	/**
	 * 分词召回率
	 * @return
	 */
	public double getRecallSegScore() { 
		return target > 0 ? (double) trueSegPositive / (double) target : 0;
	}
	
	/**
	 * 词性标注F值
	 * @return
	 */
	public double getMeasure() {

        if (getPrecisionScore() + getRecallScore() > 0) {
            return 2 * (getPrecisionScore() * getRecallScore())
                    / (getPrecisionScore() + getRecallScore());
        } else {
            // cannot divide by zero, return error code
            return -1;
        }
    }
	
	/**
	 * 分词F值
	 * @return
	 */
	public double getSegMeasure() {

        if (getPrecisionSegScore() + getRecallSegScore() > 0) {
            return 2 * (getPrecisionSegScore() * getRecallSegScore())
                    / (getPrecisionSegScore() + getRecallSegScore());
        } else {
            // cannot divide by zero, return error code
            return -1;
        }
    }
	
	/**
	 * 登录词的召回率
	 * @return
	 */
	public double getRecallScoreIV(){
		return targetIV > 0 ? (double)truePositiveIV / (double)targetIV : 0;
	}
	
	/**
	 * 未登录词的召回率
	 * @return
	 */
	public double getRecallScoreOOV(){
		return targetOOV > 0 ? (double)truePositiveOOV / (double)targetOOV : 0;
	}
	
	public HashMap<String,List<String>> getDictionary(){
		return this.dictionary;
	}
	
	/**
	 * 打印的格式
	 */
	@Override
    public String toString() {
        return "POSPrecision: " + Double.toString(getPrecisionScore()) + "\n"
                + "POSRecall: " + Double.toString(getRecallScore()) + "\n" 
        		+ "POSF-Measure: "+ Double.toString(getMeasure()) + "\n"
        		+ "SEGPrecision: " + Double.toString(getPrecisionSegScore()) + "\n"
        		+ "SEGRecall: " + Double.toString(getRecallSegScore()) + "\n" 
                + "SEGF-Measure: "+ Double.toString(getSegMeasure()) + "\n"
        		+ "RIV: " + Double.toString(getRecallScoreIV()) + "\n"
        		+ "ROOV: " + Double.toString(getRecallScoreOOV());
    }

}
