package com.berchina.esb.server.provider.model;

import java.io.Serializable;

import org.apache.solr.client.solrj.beans.Field;

import com.berchina.esb.server.provider.utils.StringUtil;

/**
 * 
 * @Package com.berchina.esb.server.provider.model
 * @Description: TODO ( )
 * @Author yanhuiqing
 * @Date  2016年9月14日 上午10:24:27
 * @Version V1.0
 */
public class SeoHotWords implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1463324420972963092L;
	//热词
	private String hotWord;
	//热词全拼
	private String pinyin;
	//热词缩写
	private String abbre;
	//出现的次数
	private long frequency;
	public long getFrequency() {
		return frequency;
	}
	public String getHotWord() {
		return hotWord;
	}
	public void setHotWord(String hotWord) {
		this.hotWord = hotWord;
	}
	
	public void setPinyin(String pinyin) {
		this.pinyin = pinyin;
	}
	public void setAbbre(String abbre) {
		this.abbre = abbre;
	}
	public void setFrequency(long frequency) {
		this.frequency = frequency;
	}
	
	
}
