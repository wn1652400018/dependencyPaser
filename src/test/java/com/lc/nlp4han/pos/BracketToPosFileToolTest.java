package com.lc.nlp4han.pos;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

/**
 * 将树转成词性标注语料
 * 
 * @author 王馨苇
 *
 */
public class BracketToPosFileToolTest {

	@Test
	public void test(){
		String treeStr = "((S(NP(PRP I))(VP(VP(VBD saw)(NP(DT the)(NN man)))(PP(IN with)(NP(DT the)(NN telescope))))))";
		String resultPre = BracketToPosFileTool.extractWordAndPos(treeStr);
		
		String resultRef = "PRP/I VBD/saw DT/the NN/man IN/with DT/the NN/telescope ";
		assertEquals(resultRef,resultPre);
	}
}
