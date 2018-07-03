package com.lc.nlp4han.constituent;

import com.lc.nlp4han.constituent.TreeNode;

public class TreeNodeUtil{
	
	/**
	 * 将短语结构树转换成已标注基本短语的字符串
	 * @param t 待转换的短语结构树
	 */	
	public static String toChunkString(TreeNode t) {
		
		if (t.isLeaf()) {
			
			return t.getNodeName()+"/";
			
		}else {
			
			String treestr = "";
			
			for (TreeNode node:t.getChildren()) {
				treestr += toChunkString(node);
				
				if (treestr.endsWith("/")) {
					treestr += t.getNodeName()+" ";
				}
			}
			
			if (t.getNodeName().startsWith("tag:")) {
				treestr="["+treestr.trim()+"]"+t.getNodeName().substring(4,t.getNodeName().length())+" " ;
			}
			
			return treestr;
		}
	}
}
