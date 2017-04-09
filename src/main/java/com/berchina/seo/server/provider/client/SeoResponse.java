package com.berchina.seo.server.provider.client;

import com.berchina.seo.server.provider.client.base.Response;
import com.berchina.seo.server.provider.model.SeoHotWords;
import com.google.common.collect.Maps;
import org.apache.http.HttpStatus;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * @Package com.berchina.seo.server.provider.client
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

    private Map<String, List<SeoHotWords>> suggest = Maps.newHashMap();

    public SeoResponse(Map<String, List<SeoHotWords>> maps)
    {
        this.suggest = maps;
    }

    public SeoResponse(Map<String, Object> seoCateMap, SeoRequest seoRequest)
    {
        super.setTime(seoRequest.getTime());
        super.setCode(HttpStatus.SC_OK);
        super.setMessage("成功");
        if (null != seoRequest)
        {
            super.setSerialNum(seoRequest.getSerialNum());
        }
        this.seoGoods = seoCateMap;
    }

    public Map<String, List<SeoHotWords>> getSuggest() {
        return suggest;
    }

    public void setSuggest(Map<String, List<SeoHotWords>> suggest) {
        this.suggest = suggest;
    }

    public Map<String, Object> getSeoGoods() {
        return seoGoods;
    }

    public void setSeoGoods(Map<String, Object> seoGoods) {
        this.seoGoods = seoGoods;
    }
}


