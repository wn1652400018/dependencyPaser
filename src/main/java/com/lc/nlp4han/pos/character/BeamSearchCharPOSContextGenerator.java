package com.lc.nlp4han.pos.character;

/**
 * 上下文产生器
 * 
 * @author 王馨苇
 * 
 * @param <T>
 */
public interface BeamSearchCharPOSContextGenerator<T>
{
    /**
     * 根据当前下标生成上下文
     * 
     * @param i
     *            当前字下标
     * @param j
     *            当前词的下标
     * @param characters
     *            字符序列
     * @param tags
     *            字符的标记序列
     * @param words
     *            词语序列
     * @param poses
     *            词语的标记序列
     * @param ac
     *            额外的信息
     * @return 上下文信息
     */
    String[] getContext(int i, int j, T[] characters, T[] tags, T[] words, String[] poses, Object[] ac);
}
