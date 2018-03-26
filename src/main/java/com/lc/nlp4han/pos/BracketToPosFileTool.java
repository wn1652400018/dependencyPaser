package com.lc.nlp4han.pos;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import com.lc.nlp4han.constituent.PlainTextByTreeStream;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;

/**
 * 从括号表达式提取词与词性
 * 
 * @author 王馨苇
 *
 */
public class BracketToPosFileTool {

	/**
	 * 预处理
	 * @param frompath 要进行处理的文档路径
	 * @param topath 预处理之后的文档路径
	 * @throws IOException
	 */
	public static void pretreatment(String frompath, String topath) throws IOException{
		//读取一颗树
		PlainTextByTreeStream lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(new File(frompath)), "utf8");	
		//创建输出流
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(topath)));
		String tree = "";
		while((tree = lineStream.read()) != ""){
			String result = extractWordAndPos(tree);
			bw.write(result);
			bw.newLine();
		}
		bw.close();
		lineStream.close();
	}
	
	/**
	 * 由括号表达式生成成分树结构
	 * 
	 * @param bracketStr 括号表达式
	 * @return
	 */
	public static String extractWordAndPos(String bracketStr){
		bracketStr = format(bracketStr);
		List<String> parts = stringToList(bracketStr);
		String result = "";
        Stack<String> stack = new Stack<String>();
        for (int i = 0; i < parts.size(); i++) {
			if(!parts.get(i).equals(")") && !parts.get(i).equals(" ")){
				stack.push(parts.get(i));
			}else if(parts.get(i).equals(" ")){
				
			}else if(parts.get(i).equals(")")){
				if(!stack.isEmpty()){
					String pos = stack.pop();
					String word = stack.pop();
					result += word + "/" + pos+" ";
				}
				stack.clear();;
			}
		}
                
        return result;
	}
	
	/**
	 * 将括号表达式去掉空格转成列表的形式
	 * 
	 * @param bracketStr 括号表达式
	 * @return
	 */
	public static List<String> stringToList(String bracketStr){
		List<String> parts = new ArrayList<String>();
        for (int index = 0; index < bracketStr.length(); ++index) {
            if (bracketStr.charAt(index) == '(' || bracketStr.charAt(index) == ')' || bracketStr.charAt(index) == ' ') {
                parts.add(Character.toString(bracketStr.charAt(index)));
            } else {
                for (int i = index + 1; i < bracketStr.length(); ++i) {
                    if (bracketStr.charAt(i) == '(' || bracketStr.charAt(i) == ')' || bracketStr.charAt(i) == ' ') {
                        parts.add(bracketStr.substring(index, i));
                        index = i - 1;
                        break;
                    }
                }
            }
        }
        return parts;
	}
	
	/**
	 * 格式化为形如：(A(B1(C1 d1)(C2 d2))(B2 d3)) 的括号表达式。叶子及其父节点用一个空格分割，其他字符紧密相连。
	 * 
	 * @param bracketStr 从训练语料拼接出的一棵树
	 */
	public static String format(String bracketStr){
		//去除最外围的括号
        bracketStr = bracketStr.substring(1, bracketStr.length() - 1).trim();
        //所有空白符替换成一位空格
        bracketStr = bracketStr.replaceAll("\\s+", " ");
        
        //去掉 ( 和 ) 前的空格
        String newTree = "";
        for (int c = 0; c < bracketStr.length(); ++c) {
            if (bracketStr.charAt(c) == ' ' && (bracketStr.charAt(c + 1) == '(' || bracketStr.charAt(c + 1) == ')')) {
                continue;
            } else {
                newTree = newTree + (bracketStr.charAt(c));
            }
        }
        
        return newTree;
	}
}
