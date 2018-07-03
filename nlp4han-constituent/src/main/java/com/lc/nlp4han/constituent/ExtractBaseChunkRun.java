package com.lc.nlp4han.constituent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.lc.nlp4han.constituent.BracketExpUtil;
import com.lc.nlp4han.constituent.PlainTextByTreeStream;
import com.lc.nlp4han.constituent.TreeNode;
import com.lc.nlp4han.ml.util.FileInputStreamFactory;


public class ExtractBaseChunkRun {		
	
	public static void main(String[] args) throws IOException
	{
		
		if (args.length < 1) {
			usage();
			return;
		}
		
		String in = "";//输入文件路径
		String out = "";//输出文件路径
		String chunkTag = "";//要提取的标记
		String encoding ="GBK";//编码格式,默认GBK
		for(int i = 0;i<args.length;i++) {
			if(args[i].equals("-in")) {
				in = args[i+1];
				i++;
			}
			if(args[i].equals("-out")) {
				out = args[i+1];
				i++;
			}	
			if(args[i].equals("-chunkTag")) {
				chunkTag = args[i+1];
				i++;
			}
			if(args[i].equals("-encoding")) {
				encoding = args[i+1];
				i++;
			}
		}
		
		List<String> tag = new ArrayList<>();
		String[] s = chunkTag.split(",");
		for (int i=0;i<s.length;i++) {
			tag.add(s[i]);
		}
		
		List<TreeNode> target = run(in,tag,encoding);
		writeFile(target,out);
	}	
	
	private static void usage() {
		System.out.println(ExtractBaseChunkRun.class.getName()
				+ " -in <inputFile> -out <outputFile> -chunkTag <targetTag> -encoding <encoding>");
	}
	
	public static List<TreeNode> run(String fileIn,List<String> cTag,String encoding) throws IOException{
		
		List<TreeNode> treeList = new ArrayList<>(); //存放标记了基本组块的树结构
		
		PlainTextByTreeStream lineStream = new PlainTextByTreeStream(
				new FileInputStreamFactory(new File(fileIn)),encoding);
		String bracketStr = "";
		while((bracketStr = lineStream.read()) != "") {
			TreeNode tree = BracketExpUtil.generateTree(bracketStr);
			BaseChunkSearcher.search(tree, cTag);	
			treeList.add(tree);
		}	
		lineStream.close();
		return treeList;
	}
	
	public static void writeFile(List<TreeNode> target,String fileOut) throws IOException {
		FileWriter fw = new FileWriter(fileOut);
		BufferedWriter bw = new BufferedWriter(fw);
		for (int i = 0;i<target.size();i++) {
			String chunkStr = TreeNodeUtil.toChunkString(target.get(i));
			bw.write(chunkStr);
			bw.newLine();
		}
		bw.close();
	}
	
	

}
