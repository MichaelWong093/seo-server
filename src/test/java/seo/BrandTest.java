package seo;

import com.alibaba.fastjson.JSON;
import com.berchina.esb.server.provider.client.SeoRequest;
import com.berchina.esb.server.provider.model.SeoCateGory;
import com.berchina.esb.server.provider.model.SeoGoods;
import com.berchina.esb.server.provider.utils.SolrUtils;
import com.berchina.esb.server.provider.utils.StringUtil;
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
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * 品牌搜索测试
 * Created by Administrator on 2016/10/19.
 */
public class BrandTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrandTest.class);

    @Test
    public void getBrand() throws IOException, SolrServerException {

        SeoRequest request = new SeoRequest();

        request.setBrand("249");

        HttpSolrClient skus = new HttpSolrClient.Builder("http://127.0.0.1:8983/solr/sku").build();

        SolrQuery query = new SolrQuery();

        /**
         *  品牌搜索
         */
        QueryResponse response = getQueryResponse(request, query);

        /**
         * 商品信息
         */
        LinkedList<SeoGoods> goodses = Lists.newLinkedList();

        SolrUtils.setSeoGoodsResponseInfo(goodses, response.getResults());

        System.out.println(JSON.toJSONString(goodses));

        /**
         *  商品属性聚合条件
         */
        setFacetQuery(query, response);

        /**
         * SKU 信息
         */
        LinkedList<Map<String, Object>> cateGories = getShopPropertyCollection(skus, query);

        System.out.println(JSON.toJSONString(cateGories));
    }

    /**
     * 获取 SKU 信息
     *
     * @param skus  sku 索引连接
     * @param query sku 搜索条件
     * @return sku 集合
     * @throws SolrServerException
     * @throws IOException
     */
    private LinkedList<Map<String, Object>> getShopPropertyCollection(HttpSolrClient skus, SolrQuery query) throws SolrServerException, IOException {
        SolrDocumentList skuDoc = skus.query(query).getResults();

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
    private Set<SeoCateGory> getSeoCateGories(SolrDocumentList skuDoc) {
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
    private void setFacetQuery(SolrQuery query, QueryResponse response) {
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
    private List getFacetCollection(List<FacetField.Count> attr) {
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
    private QueryResponse getQueryResponse(SeoRequest request, SolrQuery query) throws SolrServerException, IOException {
        HttpSolrClient goods = new HttpSolrClient.Builder("http://127.0.0.1:8983/solr/goods").build();
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

        }
        return goods.query(query);
    }
}
