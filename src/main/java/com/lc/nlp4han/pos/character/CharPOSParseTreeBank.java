package com.lc.nlp4han.pos.character;

import java.util.ArrayList;
import java.util.List;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.TreeNode;

/**
 * 解析树库语料的到词性标记训练语料
 * 
 * @author 王馨苇
 *
 */
public class CharPOSParseTreeBank implements CharPOSSampleParser {

	private static List<TreeNode> posTree = new ArrayList<TreeNode>();
	
	@Override
	public CharPOSSample parse(String sentence) {
		posTree.clear();
		
		TreeNode tree = BracketExpUtil.generateTree(sentence);
		getWordAndPosTree(tree);
		
		return getWordAndPos(posTree);
	}

	/**
	 * 解析成样本类
	 * @param posTree 词性标记子树
	 * @return
	 */
	private CharPOSSample getWordAndPos(List<TreeNode> posTree) {
		
		ArrayList<String> characters = new ArrayList<String>();
		ArrayList<String> words = new ArrayList<String>();
		ArrayList<String> tagsAndposes = new ArrayList<String>();
		
		for (int i = 0; i < posTree.size(); i++) {
			String word = posTree.get(i).getFirstChildName();
			String pos = posTree.get(i).getNodeName();

			words.add(word);

			if (word.length() == 1) {
				characters.add(word + "_S");
				tagsAndposes.add("S_" + pos);
				continue;
			}
			for (int j = 0; j < word.length(); j++) {
				char c = word.charAt(j);
				if (j == 0) {
					characters.add(c + "_B");
					tagsAndposes.add("B_" + pos);
				} else if (j == word.length() - 1) {
					characters.add(c + "_E");
					tagsAndposes.add("E_" + pos);
				} else {
					characters.add(c + "_M");
					tagsAndposes.add("M_" + pos);
				}
			}
		}
		
		return new CharPOSSample(characters, words, tagsAndposes);		
	}

	/**
	 * 获得词性标记子树序列
	 * @param tree
	 */
	private void getWordAndPosTree(TreeNode tree){
		//如果是叶子节点，肯定是具体的词，父节点是词性
		if(tree.getChildren().size() == 0){
			posTree.add(tree.getParent());
		}else{
			//不是叶子节点的时候，递归
			for (TreeNode node : tree.getChildren()) {
				getWordAndPosTree(node);
			}
		}		
	}
}
