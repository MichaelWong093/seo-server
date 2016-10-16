package com.berchina.esb.server.provider.client;

import com.berchina.esb.server.provider.client.base.Response;
import com.google.common.collect.Maps;

import java.io.Serializable;
import java.util.Map;

/**
 * @Package com.berchina.esb.server.provider.client
 * @Description: TODO ( 搜索出参 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/8/30 上午11:34
 * @Version V1.0
 */
public class SeoResponse extends Response implements Serializable {

    private static final long serialVersionUID = 7684055426935435070L;

    /**
     * 响应结果
     */
    private Map<String, Object> seoGoods = Maps.newHashMap();

    public SeoResponse() {
    }

    public SeoResponse(Map<String, Object> seoCateMap, SeoRequest seoRequest) {
        super.setTime(seoRequest.getTime());
        super.setCode("0000");
        super.setMessage("成功");
        super.setSerialNum(seoRequest.getSerialNum());
        this.seoGoods = seoCateMap;
    }

    public Map<String, Object> getSeoGoods() {
        return seoGoods;
    }

    public void setSeoGoods(Map<String, Object> seoGoods) {
        this.seoGoods = seoGoods;
    }
}


