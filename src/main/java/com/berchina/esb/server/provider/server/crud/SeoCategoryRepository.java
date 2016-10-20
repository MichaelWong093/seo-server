package com.berchina.esb.server.provider.server.crud;

import com.alibaba.fastjson.JSON;
import com.berchina.esb.server.configloader.config.SolrServerFactoryBean;
import com.berchina.esb.server.provider.client.SeoRequest;
import com.berchina.esb.server.provider.model.SeoCateGory;
import com.berchina.esb.server.provider.model.SeoGoods;
import com.berchina.esb.server.provider.utils.*;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Package com.berchina.esb.server.provider.server.crud
 * @Description: TODO ( 类目搜索类 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/9/9 上午11:13
 * @Version V1.0
 */
@Repository
public class SeoCategoryRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeoCategoryRepository.class);

    @Autowired
    private SolrServerFactoryBean factoryBean;

    public void setSeoCategoryResponseInfo(Map<String, Object> seoResponse, SeoRequest request) throws SolrServerException, IOException {

        Map<String, HttpSolrClient> solrClient = factoryBean.httpSolrServer();

        SolrQuery query = new SolrQuery();

        /**
         *  @ 品牌搜索
         */
        if (!StringUtils.isEmpty(request.getBrand())) {

            /***
             * 根据品牌搜索，展示商品属性，及商品信息
             *
             * 商品属性：搜索商品信息聚合商品 "属性"，"类目" 组装商品属性类目
             */
            QueryResponse response = this.getQueryCategoryAttrResponse(solrClient, request, query);

            /**
             * 商品信息
             */
            seoResponse.put("goods", SolrUtils.setSeoGoodsResponseInfo(response.getResults()));

            /**
             *  商品属性聚合条件
             */
            this.setFacetQuery(query, response);

            /**
             * SKU 信息
             */
            seoResponse.put("attribute", getShopPropertyCollection(solrClient, query));

            /**
             * 分页信息
             */
            SolrPageUtil.getPageInfo(seoResponse, request, response.getResults());
        }

        /**
         * 类目搜索
         */
        if (!StringUtils.isEmpty(request.getCategory())) {

            /**
             * 类目相关商品
             */
            seoResponse.put("goods", this.getSolrGoods(seoResponse, solrClient, query, request));

            /**
             * 类目相关属性
             */
            seoResponse.put("attribute", this.getSolrCategorys(solrClient, request, query));
        }
    }

    /**
     * 获取 SKU 信息
     *
     * @param solrClient sku 索引连接
     * @param query      sku 搜索条件
     * @return sku 集合
     * @throws SolrServerException
     * @throws IOException
     */
    public LinkedList<Map<String, Object>> getShopPropertyCollection(Map<String, HttpSolrClient> solrClient, SolrQuery query) throws SolrServerException, IOException {

        HttpSolrClient sku = solrClient.get(Constants.SEO_SKU);

        SolrDocumentList skuDoc = sku.query(query).getResults();

        Set<SeoCateGory> skuK = getSeoCateGories(skuDoc);

        Iterator<SeoCateGory> iterator = skuK.iterator();

        LinkedList<Map<String, Object>> cateGories = Lists.newLinkedList();

        while (iterator.hasNext()) {
            Map<String, Object> seoCates = Maps.newHashMap();
            LinkedList<SeoCateGory> skuV = Lists.newLinkedList();

            SeoCateGory cateGory = iterator.next();

            String key = cateGory.getKey();
            String value = cateGory.getValue();

            for (SolrDocument doc : skuDoc) {
                String propid = StringUtil.StringConvert(doc.get("propid"));
                if (cateGory.getKey().equals(propid)) {
                    SeoCateGory gory = new SeoCateGory();
                    gory.setKey(StringUtil.StringConvert(doc.get("vid")));
                    gory.setValue(StringUtil.StringConvert(doc.get("prvName")));
                    skuV.add(gory);
                }
            }
            seoCates.put("key", key);
            seoCates.put("value", value);
            seoCates.put("child", skuV);
            cateGories.add(seoCates);
        }
        return cateGories;
    }


    /**
     * SKU 商品属性键，商品分类总称
     *
     * @param skuDoc 商品相关 SKU 值信息
     * @return 筛选后 SKU 属性键
     */
    public Set<SeoCateGory> getSeoCateGories(SolrDocumentList skuDoc) {
        LinkedList<SeoCateGory> attrList = Lists.newLinkedList();
        for (SolrDocument doc : skuDoc) {
            SeoCateGory cateGory = new SeoCateGory();
            cateGory.setValue(StringUtil.StringConvert(doc.get("proName")));
            cateGory.setKey(StringUtil.StringConvert(doc.get("propid")));
            attrList.add(cateGory);
        }
        Set<SeoCateGory> skuK = Sets.newHashSet();
        skuK.addAll(attrList);
        return skuK;
    }

    /**
     * 商品sku聚合，类目聚合 Query 组装
     *
     * @param query    Solr 搜索语句
     * @param response 商品搜索信息
     */
    public void setFacetQuery(SolrQuery query, QueryResponse response) {
        List<FacetField> facetFields = response.getFacetFields();

        List<FacetField.Count> attr = facetFields.get(0).getValues();

        List<FacetField.Count> category = facetFields.get(1).getValues();

        List attrs = getFacetCollection(attr);

        List cates = getFacetCollection(category);

        StringBuilder atr = new StringBuilder();

        if (!StringUtils.isEmpty(attrs) && attrs.size() > 0) {
            query.clear();
            query.set("q", "*:*");
            atr.append(" ( ");
            int a = attrs.size();
            for (int i = 0; i < a; i++) {
                atr.append("vid").append(":").append(attrs.get(i));
                if (i < a - 1) {
                    atr.append(" OR ");
                }
            }
            atr.append(" ) ");
        }
        if (!StringUtils.isEmpty(cates) && cates.size() > 0) {
            atr.append(" AND ");
            int b = cates.size();
            for (int i = 0; i < b; i++) {
                atr.append("category").append(":").append(cates.get(i));
                if (i < b - 1) {
                    atr.append(" OR ");
                }
            }
        }
        query.set("fq", new String(atr));
    }

    /**
     * 聚合集合信息过滤
     *
     * @param attr 聚合属性集合
     * @return 聚合集合
     */
    public List getFacetCollection(List<FacetField.Count> attr) {
        List attrs = Lists.newLinkedList();
        for (int i = 0; i < attr.size(); i++) {
            FacetField.Count attrCount = attr.get(i);
            if (attrCount.getCount() > 0) {
                attrs.add(attrCount.getName());
            }
        }
        return attrs;
    }

    /**
     * 品牌搜索，商品属性，与类目聚合关联相关商品属性
     *
     * @param request 请求参数
     * @param query   Solr
     * @return 聚合类目属性
     * @throws SolrServerException
     * @throws IOException
     */
    private QueryResponse getQueryCategoryAttrResponse(
            Map<String, HttpSolrClient> solrClient, SeoRequest request, SolrQuery query) throws SolrServerException, IOException {

        HttpSolrClient goods = solrClient.get(EnumUtils.SEO_GOODS.getName());
        /**
         * 品牌搜索，根据品牌标识，找到相关类目
         */
        if (!StringUtils.isEmpty(request.getBrand())) {
            /**
             *  根据品牌聚合,展示商品属性，商品信息
             */
            query.set("q", "*:*");

            query.setFacet(true);

            query.addFacetField("vid");

            query.addFacetField("category");

            query.set("fq", SolrUtils.getQueryQ("brand", request.getBrand()));

            SolrUtils.setSolrPage(query, request);
        }
        return goods.query(query);
    }


    private LinkedList<SeoGoods> getSolrGoods(Map<String, Object> seoResponse,
                                              Map<String, HttpSolrClient> solrClient, SolrQuery query, SeoRequest request)
            throws SolrServerException, IOException {

        HttpSolrClient solrGoods = solrClient.get(EnumUtils.SEO_GOODS.getName());

        SolrUtils.query(this.getSolrCate(request, solrClient, query), query, request);

        SolrDocumentList goodsDoc = solrGoods.query(query).getResults();

        SolrPageUtil.getPageInfo(seoResponse, request, goodsDoc);

        SolrUtils.commit(solrGoods);

        LOGGER.info(" [ 系统类目关系商品信息, {} ] ", JSON.toJSONString(goodsDoc));

        return SolrUtils.setSeoGoodsResponseInfo(goodsDoc);
    }

    private List<String> getSolrCate(
            SeoRequest request, Map<String, HttpSolrClient> solrClient, SolrQuery query) throws SolrServerException, IOException {

        HttpSolrClient solrCate = solrClient.get("caterev");
        /**
         * 展示类目与系统类目关联关系
         */
        SolrUtils.queryCategorys(request, query);

        SolrDocumentList revDoc = solrCate.query(query).getResults();

        List<String> catList = Lists.newArrayList();

        catList.addAll(
                revDoc.stream().map(doc ->
                        SolrUtils.getSolrDocumentFiled(doc, "category"))
                        .collect(Collectors.toList()));

        LOGGER.info(" [ 展示类目与系统类目关联关系, {} ] ", JSON.toJSONString(catList));

        return catList;
    }

    private LinkedList<SeoCateGory> getSolrCategorys(
            Map<String, HttpSolrClient> solrClient, SeoRequest request, SolrQuery query) throws SolrServerException, IOException {

        HttpSolrClient solrCates = solrClient.get(request.getChannel());
        /**
         * 展示类目
         */
        SolrUtils.queryCategorys(query);

        SolrDocumentList cateGoryDoc = solrCates.query(query).getResults();

        LinkedList<SeoCateGory> seoCateGorys = CateUtils.getSeoCateGories(request.getCategory(), cateGoryDoc);

        SolrUtils.commit(solrCates);

        LOGGER.info(" [ 展示类目数据封装, {} ] ", JSON.toJSONString(seoCateGorys));

        return seoCateGorys;
    }
}
