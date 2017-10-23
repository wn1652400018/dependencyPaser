package com.lc.nlp4han.pos.character;

/**
 * 序列验证类
 * @author 王馨苇
 *
 */
public class DefaultCharPOSSequenceValidator implements CharPOSSequenceValidator<String>{

	/**
	 * 验证序列是否正确
	 * @param i 当前字符下标
	 * @param j 当前字符所属的词语
	 * @param characters 字符
	 * @param tags 字符序列
	 * @param words 词语
	 * @param poses 词性
	 * @param out 得到的下一个字符的输出结果
	 */
	@Override
	public boolean validSequence(int i, int j, String[] characters, String[] tags, String[] words, String[] poses,
			String out) {
		String temptag = out.split("_")[0];
		String temppos = out.split("_")[1];
		//保证分词的结果是一样的
		if(!temptag.equals(tags[i])){
			return false;
		}else{
			//如果是开始或者是单个的，词性可以是任意的
			if(temptag.equals("S")){
				return true;
			}else if(temptag.equals("B")){
				return true;
			}else if(temptag.equals("M") && temppos.equals(poses[i-1])){
				return true;
			}else if(temptag.equals("E") && temppos.equals(poses[i-1])){
				return true;
			}
		}
		return false;
	}
}
