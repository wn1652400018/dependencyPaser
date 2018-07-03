package com.lc.nlp4han.constituent;

import java.util.ArrayList;
import java.util.List;


import com.lc.nlp4han.constituent.TreeNode;

public class BaseChunkSearcher {
	//维护一个基本短语标记的列表
	private static List<String> tags = new ArrayList<>();
	static {
		tags.add("NP");
		tags.add("ADJP");
		tags.add("ADVP");
		tags.add("CLP");
		tags.add("DNP");
		tags.add("DVP");
		tags.add("DP");
		tags.add("PP");
		tags.add("QP");
		tags.add("UCP");
		tags.add("VP");
		tags.add("LCP");
		tags.add("IP");
		tags.add("PRN");
		tags.add("LST");
		tags.add("CP");
		tags.add("FRAG");
	}
	
	/**
	 * 提取基本短语块
	 * @param tn 短语结构树
	 * @param targetTag 待提取的基本短语块
	 * 
	 */

	public static void search(TreeNode tn, List<String> targetTag) {

		List<? extends TreeNode> children = null;

		boolean flag = true;// 标记是否是基本短语块
		boolean tried = false;//节点是否被遍历过
		if (tn != null) {
			children = tn.getChildren();
			if (!children.isEmpty()) {
				for (TreeNode child : children) {
					
					// 判断该节点的子树中是否包含短语块
					String name = child.getNodeName();
					if (tags.contains(name)) {
						flag = false;
						break;
					}
				}
				tried = true;
			}
		}

		if (tried) {
			if (flag) {
				if (targetTag.contains("all") && tags.contains(tn.getNodeName())) {
					tn.setNewName("tag:" + tn.getNodeName());
				} else {
					if (targetTag.contains(tn.getNodeName())) {
						tn.setNewName("tag:" + tn.getNodeName());
					}
				}
				return;
			}
		}

		for (int i = 0; i < tn.getChildrenNum(); i++)// 遍历
		{
			search(children.get(i), targetTag);
		}
	}
}
	
			

