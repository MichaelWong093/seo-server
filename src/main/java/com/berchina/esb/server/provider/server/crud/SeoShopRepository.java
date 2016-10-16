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

        List<SeoShop> seoShops = getShopCollection(seoRequest, query, shopClient, goodsClient);

        StringBuilder builder = new StringBuilder();

        SolrUtils.setShopSolrQuery(seoShops, query, builder, "shopid");

        seoRequest.setAttribute(new String(builder));

        seoRequest.setOther("shop");

        SolrUtils.queryShop(seoRequest, query);

        SolrDocumentList goodses = goodsClient.query(query).getResults();

        LinkedList<SeoGoods> vars = SolrUtils.setSeoGoodsResponseInfo(goodses);

        goods.put("shop", seoShops);

        goods.put("goods", vars);
    }

    public List<SeoShop> getShopCollection(SeoRequest request, SolrQuery query, HttpSolrClient shopClient, HttpSolrClient goodsClient) throws SolrServerException, IOException {
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


    @Deprecated
    public void setSeoGoodsMap(Map<String, Object> shop, Map<String, SeoShop> shopMap, SolrDocumentList doc) {
        LinkedList<Object> seoLinkedList = Lists.newLinkedList();
        int args = doc.size();
        for (int i = 0; i < args; i++) {
            String shopId = SolrUtils.getParameter(doc, i, "shopid");
            // 判断商铺集合是否存在
            if (shopMap.containsKey(shopId) && StringUtil.notNull(shopMap.get(shopId))) {
                List<SeoGoods> goodsList = shopMap.get(shopId).getGoodsList();
                int var1 = goodsList.size();
                if (var1 < 4) {
                    SeoGoods goods = new SeoGoods();
                    goods.setPrices(SolrUtils.getParameter(doc, i, "prices"));
                    goods.setGoodsName(SolrUtils.getParameter(doc, i, "hotwords"));
                    goods.setPicture(SolrUtils.getParameter(doc, i, "picture"));
                    goods.setGoodsId(SolrUtils.getParameter(doc, i, "id"));
                    goods.setActivityLabel(SolrUtils.getParameter(doc, i, "activityLabel"));
//                    shopMap.get(shopId).getGoodsList().addLast(goods);
                }
            } else {
                SeoShop seoShop = new SeoShop();
                seoShop.setShopid(shopId);
                seoShop.setShopName(SolrUtils.getParameter(doc, i, "shopName"));
                seoShop.setShopLevelId(SolrUtils.getParameter(doc, i, "shopLevelId"));
                seoShop.setSource(SolrUtils.getParameter(doc, i, "source"));
                seoShop.setLogo(SolrUtils.getParameter(doc, i, "logo"));
                seoShop.setAddress(SolrUtils.getParameter(doc, i, "address"));
                seoShop.setBusinessarea(SolrUtils.getParameter(doc, i, "businessarea"));
                seoShop.setTotalSales(SolrUtils.getParameter(doc, i, "businessarea"));
                seoShop.setTrueName(SolrUtils.getParameter(doc, i, "trueName"));
                shopMap.put(shopId, seoShop);
                SeoGoods goods = new SeoGoods();
                goods.setPrices(SolrUtils.getParameter(doc, i, "prices"));
                goods.setGoodsName(SolrUtils.getParameter(doc, i, "hotwords"));
                goods.setPicture(SolrUtils.getParameter(doc, i, "picture"));
                goods.setGoodsId(SolrUtils.getParameter(doc, i, "id"));
                goods.setActivityLabel(SolrUtils.getParameter(doc, i, "activityLabel"));
//                shopMap.get(shopId).getGoodsList().addLast(goods);
            }
        }
        Iterator map = shopMap.keySet().iterator();
        while (map.hasNext()) {
            seoLinkedList.add(shopMap.get(map.next()));
        }
        shop.put("shop", seoLinkedList);
    }

    /**
     * 设置查询的SeoShop  集合
     *
     * @param
     * @return
     */
    @Deprecated
    public void setSeoShopMap(Map<String, SeoShop> shopMap, QueryResponse response) {
        SolrDocumentList doc = response.getResults();
        int args = doc.size();
        for (int i = 0; i < args; i++) {
            String shopId = SolrUtils.getParameter(doc, i, "id");
            SeoShop seoShop = new SeoShop();
            seoShop.setShopid(shopId);
            seoShop.setShopName(SolrUtils.getParameter(doc, i, "shopName"));
            seoShop.setShopLevelId(SolrUtils.getParameter(doc, i, "shopLevelId"));
            seoShop.setSource(SolrUtils.getParameter(doc, i, "source"));
            seoShop.setLogo(SolrUtils.getParameter(doc, i, "logo"));
            seoShop.setAddress(SolrUtils.getParameter(doc, i, "address"));
            seoShop.setBusinessarea(SolrUtils.getParameter(doc, i, "businessarea"));
            seoShop.setTotalSales(SolrUtils.getParameter(doc, i, "businessarea"));
            seoShop.setTrueName(SolrUtils.getParameter(doc, i, "trueName"));
            shopMap.put(shopId, seoShop);
        }
    }

    /**
     * 结果处理
     *
     * @param seoGoodsLinkedList
     * @param doc
     */
    @Deprecated
    public void setSeoShopResponseInfoT(LinkedList<Object> seoGoodsLinkedList, SolrDocumentList doc) {
        int args = doc.size();
        Map<String, SeoShop> shopMap = new HashMap<String, SeoShop>();
        for (int i = 0; i < args; i++) {
            String shopId = SolrUtils.getParameter(doc, i, "shopid");
            /**
             * 判断有没有商铺信息
             */
            if (shopMap.containsKey(shopId)) {
                if (StringUtil.notNull(shopMap.get(shopId))) {
                    List<SeoGoods> goodsList = shopMap.get(shopId).getGoodsList();
                    int var1 = goodsList.size();
                    if (var1 < 4) {
                        SeoGoods goods = new SeoGoods();
                        goods.setPrices(SolrUtils.getParameter(doc, i, "prices"));
                        goods.setPicture(SolrUtils.getParameter(doc, i, "picture"));
                        goods.setGoodsId(SolrUtils.getParameter(doc, i, "id"));
                        goods.setActivityLabel(SolrUtils.getParameter(doc, i, "activityLabel"));
//                        goodsList.addLast(goods);
                    }
                }
            } else {
                SeoShop seoShop = new SeoShop();
                seoShop.setShopid(SolrUtils.getParameter(doc, i, "shopid"));
                seoShop.setShopName(SolrUtils.getParameter(doc, i, "shopName"));
                seoShop.setShopLevelId(SolrUtils.getParameter(doc, i, "shopLevelId"));
                seoShop.setSource(SolrUtils.getParameter(doc, i, "source"));
                seoShop.setLogo(SolrUtils.getParameter(doc, i, "logo"));
                seoShop.setAddress(SolrUtils.getParameter(doc, i, "address"));
                seoShop.setBusinessarea(SolrUtils.getParameter(doc, i, "businessarea"));
                seoShop.setTotalSales(SolrUtils.getParameter(doc, i, "businessarea"));
                seoShop.setTrueName(SolrUtils.getParameter(doc, i, "trueName"));
                shopMap.put(shopId, seoShop);
                SeoGoods goods = new SeoGoods();
                goods.setPrices(SolrUtils.getParameter(doc, i, "prices"));
                goods.setGoodsName(SolrUtils.getParameter(doc, i, "goodsName"));
                goods.setPicture(SolrUtils.getParameter(doc, i, "picture"));
                goods.setGoodsId(SolrUtils.getParameter(doc, i, "id"));
                goods.setActivityLabel(SolrUtils.getParameter(doc, i, "activityLabel"));
//                shopMap.get(shopId).getGoodsList().addLast(goods);
                seoGoodsLinkedList.add(seoShop);
            }
        }

    }

    /**
     * 分页处理
     */
    @Deprecated
    public void setPage(SeoResponse seoResponse, LinkedList<Object> seoGoodsLinkedList, Object obj) {
        //参数转换
        SeoRequest seoRequest = (SeoRequest) obj;
        SolrPageUtil spu = new SolrPageUtil(seoGoodsLinkedList, Integer.parseInt(seoRequest.getPageSize()));
//        seoResponse.setSeoGoods(spu.getList(Integer.parseInt(seoRequest.getCurrentPage())));
//        seoResponse.setPage(spu.getCurrentPage());
//        seoResponse.setTotalNum(spu.getTotalNum());
//        seoResponse.setTotalPage(spu.getPageNum());
    }

}
