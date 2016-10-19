package com.berchina.esb.server.provider.server.crud;

import java.io.IOException;
import java.util.*;

import com.alibaba.fastjson.JSON;
import com.berchina.esb.server.provider.client.SeoResponse;
import com.google.common.collect.Lists;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.berchina.esb.server.configloader.config.SolrServerFactoryBean;
import com.berchina.esb.server.provider.client.SeoRequest;
import com.berchina.esb.server.provider.model.SeoGoods;
import com.berchina.esb.server.provider.model.SeoShop;
import com.berchina.esb.server.provider.utils.SolrPageUtil;
import com.berchina.esb.server.provider.utils.SolrUtils;
import com.berchina.esb.server.provider.utils.StringUtil;
import org.springframework.util.StringUtils;

@Repository
public class SeoShopRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeoShopRepository.class);
    @Autowired
    private SolrServerFactoryBean factoryBean;

    public void querySolrDocuments(Map<String, Object> goods, SeoRequest seoRequest) throws SolrServerException, IOException {

        Map<String, HttpSolrClient> solrMap = factoryBean.httpSolrServer();

        setShopCollection(goods, seoRequest, solrMap);
    }

    public void setShopCollection(Map<String, Object> goods, SeoRequest seoRequest, Map<String, HttpSolrClient> solrMap) throws SolrServerException, IOException {

        HttpSolrClient goodsClient = solrMap.get("goods");

        HttpSolrClient shopClient = solrMap.get(seoRequest.getChannel());

        SolrQuery query = new SolrQuery();

        SolrUtils.queryShop(seoRequest, query);

        List<SeoShop> seoShops = getShopCollection(goods, seoRequest, query, shopClient, goodsClient);

        goods.put("shop", seoShops);

//        StringBuilder builder = new StringBuilder();
//        SolrUtils.setShopSolrQuery(seoShops, query, builder, "shopid");
//        seoRequest.setAttribute(new String(builder));
//        seoRequest.setOther("shop");
//        SolrUtils.queryShop(seoRequest, query);
//        SolrDocumentList goodses = goodsClient.query(query).getResults();
//        if (!StringUtils.isEmpty(seoRequest.getTerminal()) && seoRequest.getTerminal().equals("app")) {
//            goods.put("goods", SolrUtils.setSeoGoodsResponseInfo(goodses));
//        }
    }

    public List<SeoShop> getShopCollection(Map<String, Object> goods, SeoRequest request,
                                           SolrQuery query, HttpSolrClient shopClient, HttpSolrClient goodsClient) throws SolrServerException, IOException {
        List<SeoShop> shops = Lists.newLinkedList();
        SolrDocumentList doc = shopClient.query(query).getResults();
        for (int i = 0; i < doc.size(); i++) {
            SeoShop shop = new SeoShop();
            shop.setShopid(SolrUtils.getParameter(doc, i, "id"));
            shop.setShopName(SolrUtils.getParameter(doc, i, "hotwords"));
            shop.setLogo(SolrUtils.getParameter(doc, i, "logo"));
            shop.setAddress(SolrUtils.getParameter(doc, i, "address"));
            shop.setSource(SolrUtils.getParameter(doc, i, "source"));
            getShopByGoodsCollection(request, query, shop, goodsClient);
            shops.add(shop);
        }
        SolrPageUtil.getPageInfo(goods, request, doc);
        LOGGER.info(" [ 搜索商铺输出信息 ], {} ", JSON.toJSON(shops));
        return shops;
    }

    public void getShopByGoodsCollection(SeoRequest request, SolrQuery query, SeoShop shop, HttpSolrClient goodsClient) throws SolrServerException, IOException {
        /** 商品信息组装*/
        request.setAttribute(shop.getShopid());

        SolrUtils.queryShop(request, query);

        SolrDocumentList gdDoc = goodsClient.query(query).getResults();

        shop.setGoodsList(SolrUtils.setSeoGoodsResponseInfo(gdDoc));
    }
}
