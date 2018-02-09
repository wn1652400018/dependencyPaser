package com.lc.nlp4han.constituent;

/**
 * 成分树
 * @author 王馨苇
 *
 */
public class ConstituentTree {

	private TreeNode root;
	
	public void setRoot(TreeNode treeNode){
		this.root = treeNode;
	}
	
	public TreeNode getRoot(){
		return this.root;
	}

	@Override
	public String toString() {
		return root.toString();
	}
	
	
}
