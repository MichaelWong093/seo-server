package com.berchina.esb.server.provider.client;

import java.util.LinkedList;

import com.berchina.esb.server.provider.client.base.Response;
import com.berchina.esb.server.provider.model.SeoHotWords;
/**
 * 
 * @Package com.berchina.esb.server.provider.client
 * @Description: TODO (热词联想的返回对象 )
 * @Author yanhuiqing
 * @Date  2016年9月14日 上午10:14:07
 * @Version V1.0
 */
public class HotWordResponse extends Response {

	private static final long serialVersionUID = 8645763068822268199L;

	private LinkedList<SeoHotWords> hotWords;//搜索的结果集
	
	public HotWordResponse(){}
	
	public HotWordResponse(LinkedList<SeoHotWords> hotWords){
		this.hotWords = hotWords;
	}

	public LinkedList<SeoHotWords> getHotWords() {
		return hotWords;
	}

	public void setHotWords(LinkedList<SeoHotWords> hotWords) {
		this.hotWords = hotWords;
	}
}
