package com.berchina.seo.server.provider.server;

import com.berchina.seo.server.provider.client.SeoRequest;
import com.berchina.seo.server.provider.model.SeoCateGory;
import com.berchina.seo.server.provider.model.SeoGoods;
import com.berchina.seo.server.provider.server.crud.SearchRepository;
import com.berchina.seo.server.provider.utils.SolrPageUtil;
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
import java.lang.invoke.MethodHandles;
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

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired private SearchRepository repository;

    @Autowired private CategoryServer categoryServer;

    /**
     *  筛选入口
     * @param category
     * @return
     * @throws IOException
     * @throws SolrServerException
     */
    public Set<SeoCateGory> change(String category) throws IOException, SolrServerException {

//        Assert.isNull(category,"category is not null");

//        category = "fq=id:57+OR+id:29+OR+id:29+OR+id:54+OR+id:165+OR+id:185";
        return categoryServer.change(category);
    }

    /**
     * 商品搜索
     *
     * @param request
     * @return
     */
    public Map<String,Object> search(SeoRequest request) throws IOException, SolrServerException {

        Map<String,Object> maps = Maps.newConcurrentMap();

        QueryResponse response = this.repository.search(request);
        SolrDocumentList documents = response.getResults();

        List<Object> seoGoods = setGoodsDoc(documents);

        if (StringUtil.notNull(seoGoods))
        {
            SolrPageUtil.getPageInfo(maps, request, documents);

            maps.put("goods",seoGoods);
            // 查询分类，该功能只取系统类目
            Map<String,Object> map = this.categoryServer.search(response);

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
            goods.setHotwords(StringUtil.StringConvert(doc.get(this.HOTWORDS)));
            goods.setPrices(StringUtil.StringConvert(doc.get(this.PICTURE)));
            goods.setPicture(StringUtil.StringConvert(doc.get(this.PRICES)));
            goods.setShopName(StringUtil.StringConvert(doc.get(this.SHOPID)));
            goods.setSales(StringUtil.StringConvert(doc.get(this.SALES)));
            goods.setGoodsId(StringUtil.StringConvert(doc.get(this.ID)));
            String integral = StringUtil.StringConvert(doc.get(this.INTEGRAL));
            goods.setIntegralflag(StringUtil.notNull(integral) ? StringUtil.StringConvert(integral) : 0);
            goods.setSource(StringUtil.StringConvert(doc.get(this.SOURCE)));
            goods.setType(StringUtil.StringConvert(doc.get(this.SOURCES)));
            seoGoods.add(goods);
        }
        return seoGoods;
    }

    private final String PICTURE = "picture";
    private final String INTEGRAL = "integralflag";
    private final String PRICES = "prices";
    private final String SHOPID = "shopid";
    private final String SALES = "sales";
    private final String HOTWORDS = "hotwords";
    private final String SOURCE = "source";
    private final String SOURCES = "sources";
    private final String ID = "id";
}
