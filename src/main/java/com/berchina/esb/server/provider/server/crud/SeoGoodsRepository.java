package com.berchina.esb.server.provider.server.crud;

import com.alibaba.fastjson.JSON;
import com.berchina.esb.server.configloader.config.SolrServerFactoryBean;
import com.berchina.esb.server.provider.client.SeoRequest;
import com.berchina.esb.server.provider.model.SeoCateGory;
import com.berchina.esb.server.provider.model.SeoGoods;
import com.berchina.esb.server.provider.utils.CateUtils;
import com.berchina.esb.server.provider.utils.SolrPageUtil;
import com.berchina.esb.server.provider.utils.SolrUtils;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * @Package com.berchina.esb.server.provider.server.crud
 * @Description: TODO ( 搜索持久类 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/9/5 下午2:22
 * @Version V1.0
 */
@Repository
public class SeoGoodsRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeoGoodsRepository.class);

    @Autowired
    private SolrServerFactoryBean factoryBean;

    public void seoGoodsRepository(Map<String, Object> goodsMap, SeoRequest request) throws IOException, SolrServerException {

        Map<String, HttpSolrClient> solrMap = factoryBean.httpSolrServer();

        SolrQuery query = new SolrQuery();

        HttpSolrClient goods = solrMap.get(request.getChannel());

        SolrUtils.querys(request, query);

        QueryResponse response = goods.query(query);

        request.setCategory(response.getFacetFields().get(0).getValues().get(0).getName());

        LinkedList<SeoGoods> seoGoodses = this.querySolrDocuments(goodsMap, goods, request, query);

        if (!StringUtils.isEmpty(seoGoodses) && seoGoodses.size() > 0) {
            goodsMap.put("goods", seoGoodses);
            //  移动端只显示商品列表信息
            if (!StringUtils.isEmpty(request.getTerminal()) && !request.getTerminal().equals("app")) {
                goodsMap.put("attribute", setCategoryAttribute(solrMap, query, request));
                if (StringUtils.isEmpty(request.getBrand())) {
                    goodsMap.put("brand", setGoodsBrandAttribute(solrMap, query, request));
                }
            }
        }
    }

    /**
     * 搜索服务处理
     *
     * @return solr 返回数据结果
     * @throws SolrServerException
     * @throws IOException
     */
    private LinkedList<SeoGoods> querySolrDocuments(Map<String, Object> solrMap, HttpSolrClient goods, SeoRequest request, SolrQuery query) throws SolrServerException, IOException {

        SolrUtils.query(request, query);

        SolrDocumentList documents = goods.query(query).getResults();

        SolrPageUtil.getPageInfo(solrMap, request, documents);

        return SolrUtils.setSeoGoodsResponseInfo(goods.query(query).getResults());

    }


    /**
     * 类目品牌合并
     *
     * @param solrMap
     * @param query
     * @param request
     * @return
     * @throws SolrServerException
     * @throws IOException
     */
    private List setGoodsBrandAttribute(Map<String, HttpSolrClient> solrMap, SolrQuery query, SeoRequest request) throws SolrServerException, IOException {

        HttpSolrClient brandrev = solrMap.get("brandrev");

        HttpSolrClient brand = solrMap.get("brand");

        SolrUtils.queryBrandRev(request, query);

        SolrDocumentList brRev = brandrev.query(query).getResults();

        StringBuilder builder = new StringBuilder();

        SolrUtils.setCollectSolrQuery(SolrUtils.getBrandIDCollection(brRev), query, builder, "id");

        SolrDocumentList brandsDoc = brand.query(query).getResults();

        return setBrand(brandsDoc);
    }

    /**
     * SKU 商品属性搜索合并
     *
     * @param solrMap
     * @param query
     * @param request
     * @return
     * @throws SolrServerException
     * @throws IOException
     */
    private List<Map<String, Object>> setCategoryAttribute(
            Map<String, HttpSolrClient> solrMap, SolrQuery query, SeoRequest request) throws SolrServerException, IOException {

        HttpSolrClient sku = solrMap.get("sku");
        /**
         * 获取 SKU 属性时 过滤 facet_fields propid 和 大于 0 的 propid
         */
        SolrUtils.queryCategoryKey(request, query);

        LinkedList<SeoCateGory> seoSku = Lists.newLinkedList();

        SeoGoodsRepository.setSku(seoSku, sku.query(query).getResults());

        Set<SeoCateGory> skuK = Sets.newHashSet();

        skuK.addAll(seoSku);

        LOGGER.info(" 商品属性 KEY , {}", JSON.toJSON(skuK));

        /**
         *
         * SKU Value 搜索, 根据 (propid  or propid ) and catid 搜索
         *
         */
        StringBuilder builder = new StringBuilder();

        SolrUtils.setCollectSolrQuery(skuK, query, builder, "propid", request.getCategory());

        request.setAttribute(new String(builder));

        SolrUtils.queryCategoryValue(request, query);

        SolrDocumentList skuV = sku.query(query).getResults();

        LOGGER.info(" 商品属性  Value, {}", JSON.toJSON(skuV));

        /**
         *
         * @ 类目属性 KEY 对应 类目属性 Value 合并  @skus  《 - 》 @ skuV
         *
         */
        return CateUtils.getCategoryCollection(skuK, skuV);
    }

    /**
     * 全站搜索设置 SKU 商品属性
     *
     * @param seoSku
     * @param doc
     */
    public static void setSku(LinkedList<SeoCateGory> seoSku, SolrDocumentList doc) {
        int args = doc.size();
        for (int i = 0; i < args; i++) {
            SeoCateGory sku = new SeoCateGory();
            sku.setKey(SolrUtils.getParameter(doc, i, "propid"));
            sku.setValue(SolrUtils.getParameter(doc, i, "proName"));
            seoSku.add(sku);
        }
    }

    public static LinkedList<SeoCateGory.Brand> setBrand(SolrDocumentList doc) {
        LinkedList<SeoCateGory.Brand> seoBrand = Lists.newLinkedList();
        int args = doc.size();
        for (int i = 0; i < args; i++) {
            SeoCateGory.Brand brand = new SeoCateGory.Brand();
            brand.setId(SolrUtils.getParameter(doc, i, "id"));
            brand.setName(SolrUtils.getParameter(doc, i, "chineseName"));
            brand.setBdLogo(SolrUtils.getParameter(doc, i, "brandLogo"));
            seoBrand.add(brand);
        }
        return seoBrand;
    }
}
