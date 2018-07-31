package com.lc.nlp4han.dependency.tb;

/**
 * @author 作者
 * @version 创建时间：2018年7月27日 下午2:38:17 类说明
 */
public class SimpleValidator
{
	/**
	 * @param conf
	 *            当前的配置
	 * @param outCome
	 *            基于当前配置的预测结果
	 * @return 返回预测结果的合法性
	 */
	public static boolean validate(Configuration conf, String outCome)
	{
		// System.out.println("执行了validate方法");
		ActionType preAct = ActionType.toType(outCome);
		if (preAct != null)
		{
			if (preAct.getBaseAction().equals("LEFTARC_REDUCE"))
			{// 确保一个单词的中心词只能有一个
				if (conf.getStack().peek().getIndexOfWord() == 0)
					return false;
				for (Arc arc : conf.getArcs())
				{
					if (arc.getDependent() == conf.getStack().peek())
					{
						System.out.println("因为栈顶单词已经有中心词故不能有LEFTARC_REDUCE操作");
						return false;
					}
				}
				return true;
			}

			if (preAct.getBaseAction().equals("REDUCE"))
			{
				if (conf.getStack().peek().getIndexOfWord() == 0)

					if (conf.getWordsBuffer().size() == 0)
						return true;
				for (Arc arc : conf.getArcs())
				{
					if (arc.getHead().getIndexOfWord() == conf.getStack().peek().getIndexOfWord()
							|| arc.getDependent().getIndexOfWord() == conf.getStack().peek().getIndexOfWord())
					{
						return true;
					}
				}
				System.out.println("因为栈顶单词还没有建立依存关系，故不能有REDUCE操作");
				return false;
			}

			if (preAct.getRelation().equals("核心成分"))
			{// 确保“核心”只能作为一个词语的中心词

				for (Arc arc : conf.getArcs())
				{
					if (arc.getRelation().equals("核心成分"))
						return false;
				}
				return true;
			}
			else
			{
				return true;
			}

		}
		return false;
	}
}
