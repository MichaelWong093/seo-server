package com.berchina.seo.server.provider.server;

import com.berchina.seo.server.provider.client.SeoRequest;
import com.berchina.seo.server.provider.model.SeoGoods;
import com.berchina.seo.server.provider.server.crud.SearchRepository;
import com.berchina.seo.server.provider.utils.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Package com.berchina.seo.server.provider.server
 * @Description: 搜索商品服务
 * @Author rxbyes
 * @Date 2017 下午8:05
 * @Version V1.0
 */
@Service
public class SearchServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchServer.class);

    @Autowired private SearchRepository repository;

    @Autowired private CategoryServer categoryServer;
    /**
     * 商品搜索
     *
     * @param request
     * @return
     */
    public Map<String,Object> search(SeoRequest request) throws IOException, SolrServerException {

        Map<String,Object> maps = Maps.newConcurrentMap();

        QueryResponse response = repository.search(request);
        SolrDocumentList documents = response.getResults();

        List<Object> seoGoods = setGoodsDoc(documents);

        if (StringUtil.notNull(seoGoods))
        {
            maps.put("goods",seoGoods);
            // 查询分类，该功能只取系统类目
            Map<String,Object> map = categoryServer.search(response);

            Set<Map.Entry<String, Object>> set = map.entrySet();
            if (!set.isEmpty())
            {
                for (Map.Entry entry : set )
                {
                    maps.put((String) entry.getKey(),entry.getValue());
                }
            }
        }
        return maps;
    }

    // 解析 Goods 信息
    public List<Object> setGoodsDoc(SolrDocumentList documents) {
        List<Object> seoGoods = Lists.newLinkedList();
        for (SolrDocument doc : documents) {
            SeoGoods goods = new SeoGoods();
            goods.setHotwords(StringUtil.StringConvert(doc.get(HOTWORDS)));
            goods.setPrices(StringUtil.StringConvert(doc.get(PICTURE)));
            goods.setPicture(StringUtil.StringConvert(doc.get(PRICES)));
            goods.setShopName(StringUtil.StringConvert(doc.get(SHOPID)));
            goods.setSales(StringUtil.StringConvert(doc.get(SALES)));
            goods.setGoodsId(StringUtil.StringConvert(doc.get(ID)));
            String integral = StringUtil.StringConvert(doc.get(INTEGRAL));
            goods.setIntegralflag(StringUtil.notNull(integral) ? StringUtil.StringConvert(integral) : 0);
            goods.setSource(StringUtil.StringConvert(doc.get(SOURCE)));
            goods.setType(StringUtil.StringConvert(doc.get(SOURCES)));
            seoGoods.add(goods);
        }
        return seoGoods;
    }

    private static String PICTURE = "picture";
    private static String INTEGRAL = "integralflag";
    private static String PRICES = "prices";
    private static String SHOPID = "shopid";
    private static String SALES = "sales";
    private static String HOTWORDS = "hotwords";
    private static String SOURCE = "source";
    private static String SOURCES = "sources";
    private static String ID = "id";
}
