package com.berchina.seo.server.provider.server.crud;

import com.berchina.seo.server.configloader.config.logger.LoggerConfigure;
import com.berchina.seo.server.provider.client.SeoRequest;
import com.berchina.seo.server.provider.model.SeoCateGory;
import com.berchina.seo.server.provider.model.SeoGoods;
import com.berchina.seo.server.provider.utils.CateUtils;
import com.berchina.seo.server.provider.utils.SolrPageUtil;
import com.berchina.seo.server.provider.utils.SolrUtils;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Package com.berchina.seo.server.provider.server.crud
 * @Description: 搜索持久类
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/9/5 下午2:22
 * @Version V1.0
 */
@Repository
public class SeoGoodsRepository extends SeoAbstractRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private LoggerConfigure Logger;

    public void seoGoodsRepository(Map<String, Object> goodsMap, SeoRequest request) throws IOException, SolrServerException {
        super.InitGoods();
        ModifiableSolrParams params = new ModifiableSolrParams();
        SolrUtils.querys(request, params, false);
        if (Logger.info())
        {
            LOGGER.info("[  SOLR SQL 语法: {}]", params.toQueryString());
        } else {
            Cat.logEvent("SOLR.Query", "seoGoodsRepository", Transaction.SUCCESS, params.toQueryString());
        }

        final QueryResponse response;

        final LinkedList<SeoGoods> seoGoodses;

        /**
         * 区分特色频道搜索，与普通商品搜索 type eq 0 : 特产频道
         */
        if (request.getType().equals("1"))
        {
            response = goodsClient.query(params);
            seoGoodses = this.querySolrDocuments(goodsMap, request, params, goodsClient);
        } else {
            response = speClient.query(params);
            seoGoodses = this.querySolrDocuments(goodsMap, request, params, speClient);
        }

        if (response.getFacetFields().get(0).getValues().get(0).getCount() > 0) {

            request.setCategory(response.getFacetFields().get(0).getValues().get(0).getName());
        }

        if (!StringUtils.isEmpty(seoGoodses) && seoGoodses.size() > 0) {
            goodsMap.put("goods", seoGoodses);
            //  移动端只显示商品列表信息
            if (!StringUtils.isEmpty(request.getTerminal()) && !request.getTerminal().equals("app")) {
                goodsMap.put("attribute", setCategoryAttribute(request));

                if (StringUtils.isEmpty(request.getBrand())) {
                    goodsMap.put("brand", setGoodsBrandAttribute(request));
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
    private LinkedList<SeoGoods> querySolrDocuments(Map<String, Object> solrMap, SeoRequest request, ModifiableSolrParams params, HttpSolrClient solrClient) throws SolrServerException, IOException {

        SolrUtils.query(request, params);

        if (Logger.info()) {

            LOGGER.info("[  SOLR SQL 语法: {}]", params.toQueryString());
        } else {

            Cat.logEvent("SOLR.Query", "querySolrDocuments", Transaction.SUCCESS, params.toQueryString());
        }
        QueryResponse response = solrClient.query(params);

        SolrDocumentList documents = response.getResults();

        Map<String, Map<String, List<String>>> maps = response.getHighlighting();

        SolrPageUtil.getPageInfo(solrMap, request, documents);

        return SolrUtils.setSeoGoodsResponseInfos(maps, documents);
    }

    /**
     * 类目品牌合并
     *
     * @param request
     * @return
     * @throws SolrServerException
     * @throws IOException
     */
    private List setGoodsBrandAttribute(SeoRequest request) throws SolrServerException, IOException {

        SolrQuery query = new SolrQuery();

        SolrUtils.queryBrandRev(request, query);

        SolrDocumentList brRev = bdrevClient.query(query).getResults();

        if (!StringUtils.isEmpty(brRev) && brRev.size() > 0) {
            StringBuilder builder = new StringBuilder();

            SolrUtils.setCollectSolrQuery(SolrUtils.getBrandIDCollection(brRev), query, builder, "id");

            SolrDocumentList brandsDoc = brandClient.query(query).getResults();

            return SolrUtils.setBrand(brandsDoc);
        }
        return Lists.newLinkedList();
    }

    /**
     * SKU 商品属性搜索合并
     *
     * @param request
     * @return
     * @throws SolrServerException
     * @throws IOException
     */
    private List<Map<String, Object>> setCategoryAttribute(SeoRequest request) throws SolrServerException, IOException {

        SolrQuery query = new SolrQuery();

        /**
         * 获取 SKU 属性时 过滤 facet_fields propid 和 大于 0 的 propid
         */
        SolrUtils.queryCategoryKey(request, query);

        if (Logger.info()) {
            LOGGER.info(" [ SOLR SQL 语法: {}]", query);
        } else {
            Cat.logEvent("SOLR.Query", "setCategoryAttribute", Transaction.SUCCESS, query.toQueryString());
        }

        LinkedList<SeoCateGory> seoSku = Lists.newLinkedList();

        SolrUtils.setSku(seoSku, skuClient.query(query).getResults());

        Set<SeoCateGory> skuK = Sets.newHashSet();

        skuK.addAll(seoSku);

//        LOGGER.info(" 商品属性 KEY , {}", JSON.toJSON(skuK));

        /**
         *
         * SKU Value 搜索, 根据 (propid  or propid ) and catid 搜索
         *
         */
        StringBuilder builder = new StringBuilder();

        SolrUtils.setCollectSolrQuery(skuK, query, builder, "propid", request.getCategory());

        request.setAttribute(new String(builder));
//        if (Logger.info()) {
//            LOGGER.info(" [ SOLR SQL 语法: {}]", query);
//        } else {
//            Cat.logEvent("SOLR.Query", "setCategoryAttribute", Transaction.SUCCESS, query.toQueryString());
//        }
        SolrUtils.queryCategoryValue(request, query);

        SolrDocumentList skuV = skuClient.query(query).getResults();

//        LOGGER.info(" 商品属性  Value, {}", JSON.toJSON(skuV));
        /**
         *
         * @ 类目属性 KEY 对应 类目属性 Value 合并  @skus  《 - 》 @ skuV
         *
         */
        return CateUtils.getCategoryCollection(skuK, skuV);
    }
}
