package com.lc.nlp4han.constituent;

import static org.junit.Assert.*;

import org.junit.Test;

public class Bracket2POSTest
{

	@Test
	public void test()
	{
		String treeStr = "((S(NP(PRP I))(VP(VP(VBD saw)(NP(DT the)(NN man)))(PP(IN with)(NP(DT the)(NN telescope))))))";
		String resultPre = Bracket2POSTool.extractWordAndPos(treeStr, "/");

		String resultRef = "PRP/I VBD/saw DT/the NN/man IN/with DT/the NN/telescope ";
		
		assertEquals(resultPre, resultRef);
	}

}
