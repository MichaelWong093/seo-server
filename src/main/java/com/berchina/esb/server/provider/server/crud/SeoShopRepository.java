package com.berchina.esb.server.provider.server.crud;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.berchina.esb.server.provider.client.SeoResponse;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
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
    @Autowired
    private SolrServerFactoryBean factoryBean;

    /**
     * 搜索服务处理
     *
     * @param seoRequest 请求 SeoRequest 对象
     * @return
     * @throws SolrServerException
     * @throws IOException
     */
    public QueryResponse querySolrDocuments( SeoRequest seoRequest) throws SolrServerException, IOException {
        //获取 店铺solr 请求连接
        HttpSolrClient solrClient = factoryBean.httpSolrServer().get(seoRequest.getChannel());
        SolrQuery query = new SolrQuery();
        SolrUtils.queryParameter(seoRequest, query);
        return solrClient.query(query);
    }


    /**
     * 搜索商品服务处理
     *
     * @param objects     请求 SeoRequest 对象
     * @param channelName 渠道名称
     * @return
     * @throws SolrServerException
     * @throws IOException
     */
    public SolrDocumentList querySolrDocuments(Object objects, String channelName) throws SolrServerException, IOException {
        //参数转换
        SeoRequest seoRequest = (SeoRequest) objects;
        //获取 店铺solr 请求连接
        HttpSolrClient solrClient = factoryBean.httpSolrServer().get(channelName);
        SolrQuery query = new SolrQuery();
        SolrUtils.queryParameter(seoRequest, query);
        QueryResponse response = solrClient.query(query);
        return response.getResults();
    }


    public void setSeoGoodsMap(Map<String, Object> shop, Map<String, SeoShop> shopMap,SolrDocumentList doc) {
        LinkedList<Object> seoLinkedList = Lists.newLinkedList();
        int args = doc.size();
        for (int i = 0; i < args; i++) {
            String shopId = SolrUtils.getParameter(doc, i, "shopid");
            // 判断商铺集合是否存在
            if (shopMap.containsKey(shopId) && StringUtil.notNull(shopMap.get(shopId))) {
                LinkedList<SeoGoods> goodsList = shopMap.get(shopId).getGoodsList();
                int var1 = goodsList.size();
                if (var1 < 4) {
                    SeoGoods goods = new SeoGoods();
                    goods.setPrices(SolrUtils.getParameter(doc, i, "prices"));
                    goods.setGoodsName(SolrUtils.getParameter(doc, i, "hotwords"));
                    goods.setPicture(SolrUtils.getParameter(doc, i, "picture"));
                    goods.setGoodsId(SolrUtils.getParameter(doc, i, "id"));
                    goods.setActivityLabel(SolrUtils.getParameter(doc, i, "activityLabel"));
                    shopMap.get(shopId).getGoodsList().addLast(goods);
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
                shopMap.get(shopId).getGoodsList().addLast(goods);
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
     * @param doc
     * @return
     */
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
                    LinkedList<SeoGoods> goodsList = shopMap.get(shopId).getGoodsList();
                    int var1 = goodsList.size();
                    if (var1 < 4) {
                        SeoGoods goods = new SeoGoods();
                        goods.setPrices(SolrUtils.getParameter(doc, i, "prices"));
                        goods.setPicture(SolrUtils.getParameter(doc, i, "picture"));
                        goods.setGoodsId(SolrUtils.getParameter(doc, i, "id"));
                        goods.setActivityLabel(SolrUtils.getParameter(doc, i, "activityLabel"));
                        goodsList.addLast(goods);
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
                shopMap.get(shopId).getGoodsList().addLast(goods);
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
