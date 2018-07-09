package edu.hust.dependency.arceager;
/**
 * action预测
 * 
 * TODO: 通过给定的Conf去预测相应的操作
 * 
 * @author 王宁
 *
 */
public class Oracle {

//	public Configuration transition(Configuration conf) {
//		ActionType action = classifyConf(conf);
//		OperatorArcEagerbased operator = new OperatorArcEagerbased();
//		return operator.operate(conf,action);
//	}

	public ActionType classifyConf(Configuration conf) {//将当前的Configuration分类
		//利用训练得到模型去判断对当前的conf做怎样的操作
		return new ActionType();
	}
	

}
