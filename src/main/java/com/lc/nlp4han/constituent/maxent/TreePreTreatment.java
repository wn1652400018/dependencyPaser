package com.lc.nlp4han.constituent.maxent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.PlainTextByTreeStream;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;

/**
 * 句法树的初始化处理
 * 说明：去除句法树中的空节点，去除节点命名中的功能标记
 * @author 王馨苇
 *
 */
public class TreePreTreatment{
	
	/**
	 * 预处理
	 * @param frompath 要进行处理的文档路径
	 * @param topath 预处理之后的文档路径
	 * @throws UnsupportedOperationException
	 * @throws FileNotFoundException
	 * @throws IOException
	 */
	public static void pretreatment(String frompath, String topath) throws UnsupportedOperationException, FileNotFoundException, IOException{
		//读取一颗树
		PlainTextByTreeStream lineStream = null;	
		//创建输出流
		BufferedWriter bw = new BufferedWriter(new FileWriter(new File(topath)));
		lineStream = new PlainTextByTreeStream(new FileInputStreamFactory(new File(frompath)), "utf8");
		String tree = "";
		while((tree = lineStream.read()) != ""){
			TreeNode node = BracketExpUtil.generateTree(tree);
			//对树进行遍历
			deleteNone(node);	
			String newTreeStr = node.toNoNoneSample();
			TreeNode newTree = BracketExpUtil.generateTree("("+newTreeStr+")");
			bw.write("("+TreeNode.printTree(newTree, 1)+")");
			bw.newLine();
		}
		bw.close();
		lineStream.close();
	}

	/**
	 * 对树进行遍历删除NONE【这里的删除是将属性flag设置为false】
	 * @param node 一棵树
	 */
	public static void deleteNone(TreeNode node){
		if(node.getChildrenNum() != 0){
			for (TreeNode treenode:node.getChildren()) {
				deleteNone(treenode);
			}
		}		
		if(!node.isLeaf()){
			if(node.getNodeName().contains("NONE")){
				//该节点的父节点只有空节点一个孩子
				if(node.getParent().getChildrenNum() > 1){
					//将NONE和NONE的子节点标记位false
					node.setFlag(false);
					node.getFirstChild().setFlag(false);	
					//(SBAR(-NONE- 0)(S(-NONE- *T*-1)))
					if(node.getParent().getChildrenNum() == 2){
						node.getParent().setFlag(false);
						if(node.getParent().getIChild(1).getChildrenNum() == 1){
							if(node.getParent().getIChild(1).getFirstChildName().contains("NONE")){
								node.getParent().getIChild(1).setFlag(false);
								node.getParent().getIChild(1).getFirstChild().setFlag(false);
								node.getParent().getIChild(1).getFirstChild().getFirstChild().setFlag(false);
								//(VP (VBD reported) (SBAR (-NONE- 0) (S (-NONE- *T*-1) )))变为(VBD reported)
								if(node.getParent().getParent().getChildrenNum() == 2){
									node.getParent().getParent().setFlag(false);
								}
							}
						}
					}
				}else if(node.getParent().getChildrenNum() == 1){
					//将NONE和NONE的子节点和父节点标记位false
					node.setFlag(false);
					node.getFirstChild().setFlag(false);
					node.getParent().setFlag(false);
					//(S(NP(-NONE- *-1))(VP(To to)(VP ....)))
					if(node.getParent().getParent().getChildrenNum() == 2){
						node.getParent().getParent().setFlag(false);
					}
				}
			}else if(node.getNodeName().contains("-")){
				if(!node.getNodeName().equals("-LRB-") && !(node.getNodeName().equals("-RRB-"))){
					node.setNewName(node.getNodeName().split("-")[0]);
				}
			}else if(IsDigitUtil.isDigit(node.getNodeName().charAt(node.getNodeName().length()-1))){
				if(IsDigitUtil.isDigit(node.getNodeName().charAt(node.getNodeName().length()-2))){
					node.setNewName(node.getNodeName().substring(0, node.getNodeName().length()-3));
				}else{
					node.setNewName(node.getNodeName().substring(0, node.getNodeName().length()-2));
				}
			}
		}
	}
}
