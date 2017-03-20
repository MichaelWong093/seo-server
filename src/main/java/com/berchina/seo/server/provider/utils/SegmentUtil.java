package com.berchina.seo.server.provider.utils;

import com.berchina.seo.server.provider.segment.EasyCustomDictionary;

/**
 * 
 * @author halley (yanhuiqing)
 *
 */
public class SegmentUtil {
	private SegmentUtil(){}
	
	/**
	 * 非覆盖式插入自定义词典 
	 * @param customWord 新词
	 * @return
	 */
	public static boolean customDicAdd(String customWord){
		return EasyCustomDictionary.add(customWord);
	}
	/**
	 * 非覆盖式插入自定义词典 
	 * @param customWord 新词
	 * @param natureWithFrequency 词性和对应的词频
	 * @return
	 */
	public static boolean customDicAdd(String customWord,String natureWithFrequency){
		return EasyCustomDictionary.add(customWord,natureWithFrequency);
	}
	
	/**
	 * 以覆盖模式添加新词
	 * @param customWord 新词
	 * @return
	 */
	public static boolean customDicInsert(String customWord){
		return EasyCustomDictionary.insert(customWord);
	}
	/**
	 * 以覆盖模式添加新词
	 * @param customWord 新词
	 * @param natureWithFrequency 词性和对应的词频
	 * @return
	 */
	public static boolean customDicInsert(String customWord,String natureWithFrequency){
		return EasyCustomDictionary.insert(customWord,natureWithFrequency);
	}
	
}
