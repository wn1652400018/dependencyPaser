package com.lc.nlp4han.pos.character;

/**
 * 验证输出序列是否合法的接口
 * @author 王馨苇
 *
 * @param <T>
 */
public interface CharPOSSequenceValidator<T> {

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
	boolean validSequence(int i, int j, T[] characters, T[] tags, T[] words, String[] poses, String out);
}
