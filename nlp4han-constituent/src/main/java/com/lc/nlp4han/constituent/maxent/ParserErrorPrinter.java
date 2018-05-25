package com.lc.nlp4han.constituent.maxent;

import java.io.OutputStream;
import java.io.PrintStream;

import com.lc.nlp4han.constituent.HeadTreeNode;

/**
 * 错误打印类
 * @author 王馨苇
 *
 */
public class ParserErrorPrinter extends ParserEvaluateMonitor{

    private PrintStream errOut;
	
	public ParserErrorPrinter(OutputStream out){
		errOut = new PrintStream(out);
	}
	
	/**
	 * 样本和预测的不一样的时候进行输出
	 * @param reference 参考的样本
	 * @param predict 预测的结果
	 */
	@Override
	public void missclassified(ConstituentTreeSample<HeadTreeNode> reference, ConstituentTreeSample<HeadTreeNode> predict) {
		 errOut.println("样本的结果：");
		 errOut.println(ConstituentTreeSample.toTree(reference.getWords(), reference.getActions()).toStringWordIndex());
		 errOut.println();
		 errOut.println("预测的结果：");
		 errOut.println(ConstituentTreeSample.toTree(predict.getWords(), predict.getActions()).toStringWordIndex());
		 errOut.println();
	}
}
