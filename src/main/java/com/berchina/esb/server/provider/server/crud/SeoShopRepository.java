package com.berchina.esb.server.provider.server.crud;

import java.io.IOException;
import java.util.*;

import com.berchina.esb.server.provider.utils.EnumUtils;
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
import org.springframework.util.StringUtils;

@Repository
public class SeoShopRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeoShopRepository.class);
    @Autowired
    private SolrServerFactoryBean factoryBean;

    public void querySolrDocuments(Map<String, Object> goods, SeoRequest seoRequest) throws SolrServerException, IOException {

        Map<String, HttpSolrClient> solrMap = factoryBean.httpSolrServer();

        getShopCollection(goods, seoRequest, solrMap);
    }

    public List<SeoShop> getShopCollection(Map<String, Object> goods, SeoRequest request,
                                           Map<String, HttpSolrClient> solrClient) throws SolrServerException, IOException {

        HttpSolrClient goodsClient = solrClient.get(EnumUtils.SEO_GOODS.getName());

        HttpSolrClient shopClient = solrClient.get(request.getChannel());

        List<SeoShop> shops = Lists.newLinkedList();

        SolrQuery query = new SolrQuery();

        SolrUtils.queryShop(request, query);

        QueryResponse response = shopClient.query(query);

        SolrDocumentList doc = response.getResults();

        Map<String, Map<String, List<String>>> maps = response.getHighlighting();

        for (int i = 0; i < doc.size(); i++) {
            SeoShop shop = new SeoShop();
            String id = SolrUtils.getParameter(doc, i, "id");
            shop.setShopid(id);
            if (!StringUtils.isEmpty(request.getTerminal()) && !request.getTerminal().equals("app")) {
                shop.setShopName(
                        String.valueOf(maps.get(id).get("hotwords")).replace("[", "").replace("]", "")
                );
            } else {
                shop.setShopName(SolrUtils.getParameter(doc, i, "hotwords"));
            }
            shop.setLogo(SolrUtils.getParameter(doc, i, "logo"));
            shop.setDist(SolrUtils.getParameter(doc,i,"_dist_"));
            shop.setAddress(SolrUtils.getParameter(doc, i, "address"));
            shop.setSource(SolrUtils.getParameter(doc, i, "source"));
            shop.setHours(SolrUtils.getParameter(doc, i, "businesshours"));
            getShopByGoodsCollection(request, query, shop, goodsClient);
            shops.add(shop);
        }
        SolrPageUtil.getPageInfo(goods, request, doc);
        goods.put("shop", shops);
        return shops;
    }

    public void getShopByGoodsCollection(SeoRequest request, SolrQuery query, SeoShop shop, HttpSolrClient goodsClient) throws SolrServerException, IOException {

        LinkedList<SeoGoods> goodses = Lists.newLinkedList();

        /** 商品信息组装*/
        request.setAttribute(shop.getShopid());

        SolrUtils.setShopbyGoods(request, query);

        QueryResponse response = goodsClient.query(query);

        SolrDocumentList gdDoc = response.getResults();

        SolrUtils.setSeoGoodsResponseInfo(goodses, gdDoc);

        shop.setGoodsList(goodses);
    }
}
