package edu.hust.dependency.arceager;

public class OperatorArcEagerbased implements Operator{

	
    @Override
	public Configuration transitionOperate(Configuration conf,ActionType action) {	
    	// 根据action对conf做相应的操作
    	return new Configuration();
	}
}
