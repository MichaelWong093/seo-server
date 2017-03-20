package com.berchina.seo.server.provider.server.crud;

import java.io.IOException;
import java.util.*;

import com.google.common.collect.Lists;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import com.berchina.seo.server.provider.client.SeoRequest;
import com.berchina.seo.server.provider.model.SeoGoods;
import com.berchina.seo.server.provider.model.SeoShop;
import com.berchina.seo.server.provider.utils.SolrPageUtil;
import com.berchina.seo.server.provider.utils.SolrUtils;

@Repository
public class SeoShopRepository extends SeoAbstractRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeoShopRepository.class);

    public void querySolrDocuments(Map<String, Object> goods, SeoRequest seoRequest) throws SolrServerException, IOException {

        getShopCollection(goods, seoRequest);
    }

    public List<SeoShop> getShopCollection(Map<String, Object> goods, SeoRequest request) throws SolrServerException, IOException {

        super.InitShop();

        List<SeoShop> shops = Lists.newLinkedList();

        SolrQuery query = new SolrQuery();

        ModifiableSolrParams params = new ModifiableSolrParams();

        SolrUtils.queryShop(request, params);

        QueryResponse response = shopClient.query(params);

        SolrDocumentList doc = response.getResults();

        Map<String, Map<String, List<String>>> maps = response.getHighlighting();

        for (int i = 0; i < doc.size(); i++) {
            SeoShop shop = new SeoShop();
            String id = SolrUtils.getParameter(doc, i, "id");
            shop.setShopid(id);
            shop.setShopName(
                    String.valueOf(maps.get(id).get("hotwords")).replace("[", "").replace("]", ""));
            shop.setLogo(SolrUtils.getParameter(doc, i, "logo"));
            shop.setDist(SolrUtils.getParameter(doc, i, "_dist_"));
            shop.setAddress(SolrUtils.getParameter(doc, i, "address"));
            shop.setSource(SolrUtils.getParameter(doc, i, "source"));
            shop.setHours(SolrUtils.getParameter(doc, i, "businesshours"));
            getShopByGoodsCollection(request, query, shop);
            shops.add(shop);
        }
        SolrPageUtil.getPageInfo(goods, request, doc);
        goods.put("shop", shops);
        return shops;
    }

    public void getShopByGoodsCollection(SeoRequest request, SolrQuery query, SeoShop shop) throws SolrServerException, IOException {

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
