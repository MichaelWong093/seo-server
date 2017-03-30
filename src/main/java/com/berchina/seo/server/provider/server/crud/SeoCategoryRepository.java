package com.berchina.seo.server.provider.server.crud;

import com.berchina.seo.server.provider.client.SeoRequest;
import com.berchina.seo.server.provider.model.SeoCateGory;
import com.berchina.seo.server.provider.model.SeoGoods;
import com.berchina.seo.server.provider.utils.CateUtils;
import com.berchina.seo.server.provider.utils.SolrPageUtil;
import com.berchina.seo.server.provider.utils.SolrUtils;
import com.berchina.seo.server.provider.utils.StringUtil;
import com.dianping.cat.Cat;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Package com.berchina.seo.server.provider.server.crud
 * @Description: TODO ( 类目搜索类 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/9/9 上午11:13
 * @Version V1.0
 */
@Repository
public class SeoCategoryRepository extends SeoAbstractRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeoCategoryRepository.class);

    public void setSeoCategoryResponse(Map<String, Object> seoResponse, SeoRequest request) throws SolrServerException, IOException {

        try {
            // 初始化类目搜索链接
            super.InitCategory();
            /**
             *  保存商品集合
             */
            LinkedList<SeoGoods> goods = Lists.newLinkedList();
            /**
             *  @ 品牌搜索
             */
            if (StringUtil.notNull(request.getBrand())) {
                /***
                 * 根据品牌搜索，展示商品属性，及商品信息
                 *
                 * 商品属性：搜索商品信息聚合商品 "属性"，"类目" 组装商品属性类目
                 */
                QueryResponse response = this.getQueryCategoryAttr(request);

                SolrDocumentList documents = response.getResults();

                if (StringUtil.notNull(documents) && documents.size() > 0) {

                    /**
                     *  商品属性聚合条件
                     */
                    this.setFacetQuery(response);

                    /**
                     * SKU 信息
                     */
                    seoResponse.put("attribute", this.getShopPropertyCollection());

                    /**
                     * 商品信息
                     */
                    SolrUtils.setSeoGoodsResponseInfo(goods, documents);

                    seoResponse.put("goods", goods);
                    /**
                     * 分页信息
                     */
                    SolrPageUtil.getPageInfo(seoResponse, request, documents);
                }
            }

            /**
             * 类目搜索
             */
            if (StringUtil.notNull(request.getCategory())) {
                /**
                 * 三级类目处理
                 */
                if (!this.getCategoryLevel(request)) {

                    LinkedList<String> gories = Lists.newLinkedList();

                    gories.add(request.getCategory());

                    QueryResponse response = this.getSolrGoods(gories, goods, seoResponse, request);

                    seoResponse.put("goods", goods);
                    /**
                     *  商品属性聚合条件
                     */
                    this.setFacetQuery(response);

                    if (StringUtil.notNull(goods) && goods.size() > 0) {

                        seoResponse.put("attribute", this.getShopPropertyCollection());
                    }
                } else {
                    /**
                     * 验证类目搜索是否是第三级类目，三级类目特殊化处理
                     */
                    LinkedList<SeoCateGory> cateGories = this.getSolrCategorys(request);

                    /** 类目搜索之后，搜索商品，过滤恶意搜索，减少服务器类目搜索, 或者是三级类目 */
                    if (StringUtil.notNull(cateGories) && cateGories.size() > 0) {

                        this.getSolrGoods(
                                CateUtils.setEndCategoryQueryString(cateGories), goods, seoResponse, request);

                        if (StringUtil.notNull(goods) && goods.size() > 0) {
                            /**
                             * 类目相关属性
                             */
                            seoResponse.put("category", cateGories);
                        }
                        /**
                         * 类目相关商品
                         */
                        seoResponse.put("goods", goods);
                    }
                }
            }
        } catch (Exception ex) {
            Cat.logError("[ 未知异常： ]", ex);
        }
    }


    public LinkedList<SeoCateGory> getSolrCategorys(SeoRequest request) throws SolrServerException, IOException {

        SolrUtils.queryCategorys(query);

        SolrDocumentList cateGoryDoc = categorysClient.query(query).getResults();

        SolrUtils.commit(categorysClient);

        return CateUtils.getSeoCateGories(request.getCategory(), cateGoryDoc);
    }

    /**
     * 类目级别判断,获取类目编号
     *
     * @param request
     * @return
     * @throws SolrServerException
     * @throws IOException
     */
    public boolean getCategoryLevel(SeoRequest request) throws SolrServerException, IOException {

        super.query.clear();

        super.query.set("q", "*:*");
        super.query.set("fl", "id");
        super.query.set("start", "0");
        super.query.set("rows", "1");
        super.query.set("fq", SolrUtils.getQueryQ("id", request.getCategory()));

        SolrDocumentList docs = categorysClient.query(query).getResults();

        if (StringUtil.notNull(docs) && docs.size() > 0) {

            return getCategoryByParentId(StringUtil.StringConvert(docs.get(0).getFieldValue("id")));
        }
        return false;
    }


    /**
     * 根据展示类目编号递归叶子类目，假设无叶子类目则表示，当前搜索类目为叶子类目，否则为叶子类目父类目
     *
     * @param parentId
     * @return
     */
    public boolean getCategoryByParentId(String parentId) throws IOException, SolrServerException {
        super.query.clear();

        query.set("q", "*:*");
        query.set("fl", "id");
        query.set("fq", SolrUtils.getQueryQ("parentid", parentId));

        SolrDocumentList leves = categorysClient.query(query).getResults();

        return StringUtil.notNull(leves) && leves.size() > 0 ? true : false;
    }


    /**
     * 获取 SKU 信息
     *
     * @return sku 集合
     * @throws SolrServerException
     * @throws IOException
     */
    public LinkedList<Map<String, Object>> getShopPropertyCollection() throws SolrServerException, IOException {

        SolrDocumentList skuDoc = skuClient.query(query).getResults();

        Set<SeoCateGory> skuK = getSeoCateGories(skuDoc);

        Iterator<SeoCateGory> iterator = skuK.iterator();

        LinkedList<Map<String, Object>> cateGories = Lists.newLinkedList();

        while (iterator.hasNext()) {
            Set<SeoCateGory> setSkuV = Sets.newHashSet();
            Map<String, Object> seoCates = Maps.newHashMap();
            LinkedList<SeoCateGory> skuV = Lists.newLinkedList();

            SeoCateGory cateGory = iterator.next();
            String key = cateGory.getKey();
            String value = cateGory.getValue();

            for (SolrDocument doc : skuDoc) {
                String propid = StringUtil.StringConvert(doc.get("propid"));
                if (key.equals(propid)) {
                    SeoCateGory gory = new SeoCateGory();
                    gory.setKey(StringUtil.StringConvert(doc.get("vid")));
                    gory.setValue(StringUtil.StringConvert(doc.get("prvName")));
                    skuV.add(gory);
                }
            }

            setSkuV.addAll(skuV);

            seoCates.put("key", key);
            seoCates.put("value", value);
            seoCates.put("childs", setSkuV);

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
     * @param response 商品搜索信息
     */
    public void setFacetQuery(QueryResponse response) {

        List<FacetField> facetFields = response.getFacetFields();

        List<FacetField.Count> attr = facetFields.get(0).getValues();

        List<FacetField.Count> category = facetFields.get(1).getValues();

        List attrs = getFacetCollection(attr);

        Collections.sort(attrs);

        List cates = getFacetCollection(category);

        Collections.sort(cates);

        StringBuilder atr = new StringBuilder();

        super.query.clear();
        super.query.set("q", "*:*");

        if (StringUtil.notNull(attrs) && attrs.size() > 0) {
            atr.append("(");
            int a = attrs.size();
            for (int i = 0; i < a; i++) {
                String vid = String.valueOf(attrs.get(i));
                if (!vid.contains("-") && !vid.equals("0")) {
                    atr.append("vid").append(":").append(vid);
                    if (i < a - 1) {
                        atr.append(" OR ");
                    }
                }
            }
            atr.append(")");
        }
        if (StringUtil.notNull(cates) && cates.size() > 0) {
            atr.append(" AND ").append("(");
            int b = cates.size();
            for (int i = 0; i < b; i++) {
                atr.append("category").append(":").append(cates.get(i));
                if (i < b - 1) {
                    atr.append(" OR ");
                }
            }
            atr.append(")");
        }
        super.query.set("fq", new String(atr));
        SolrUtils.setStartAndRows(super.query);
        LOGGER.info(" [ SOLR SQL 语法: {}] ", super.query);
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
     * @return 聚合类目属性
     * @throws SolrServerException
     * @throws IOException
     */
    public QueryResponse getQueryCategoryAttr(SeoRequest request) throws SolrServerException, IOException {
        super.query.clear();
        /**
         *  根据品牌聚合,展示商品属性，商品信息
         */
        super.query.set("q", "*:*");

        super.query.setFacet(true);

        super.query.addFacetField("vid");

        super.query.addFacetField("category");

        super.query.set("fq", SolrUtils.getQueryQ("brand", request.getBrand()));

        SolrUtils.setSolrPage(super.query, request);

        LOGGER.info(" { 商品搜索过滤商品SKU，类目信息，{} } ", super.query);

        return goodsClient.query(super.query);
    }


    public QueryResponse getSolrGoods(
            LinkedList<String> gories, LinkedList<SeoGoods> goodses, Map<String, Object> seoResponse, SeoRequest request)
            throws SolrServerException, IOException {
        /**
         * 排除叶子类目，因叶子类目没有类目列表
         */
        SolrUtils.query(getSolrCate(gories, request), query, request);

        QueryResponse response;

        if (request.getType().equals("1")) {

            response = goodsClient.query(query);
        } else {
            response = speClient.query(query);
        }

        SolrDocumentList goodsDoc = response.getResults();

        SolrPageUtil.getPageInfo(seoResponse, request, goodsDoc);

        SolrUtils.setSeoGoodsResponseInfo(goodses, goodsDoc);

        return response;
    }


    /**
     * 系统类目与展示类目关联
     *
     * @param request
     * @return
     * @throws SolrServerException
     * @throws IOException
     */
    public List<String> getSolrCate(LinkedList<String> gories, SeoRequest request) throws SolrServerException, IOException {

        StringBuilder builder = new StringBuilder();

        SolrUtils.setCollectSolrQuery(gories, query, builder, "revid");

        request.setCategory(new String(builder));

        /**
         * 展示类目与系统类目关联关系
         */
        SolrUtils.queryCategorys(request, query);

        SolrDocumentList revDoc = caterevClient.query(query).getResults();

        List<String> catList = Lists.newArrayList();

        catList.addAll(
                revDoc.stream().map(doc ->
                        SolrUtils.getSolrDocumentFiled(doc, "category")).collect(Collectors.toList()));

        StringBuilder bd = new StringBuilder();
        /**
         * 系统类目关联分装
         */
        SolrUtils.setCollectSolrQuery(catList, query, bd, "id");
        /**
         * 系统类目统一处理
         */
        request.setCategory(new String(bd));

        SolrUtils.queryCategorys(request, query);

        SolrDocumentList categories = categoryClient.query(query).getResults();

        List<String> gorys = Lists.newArrayList();

        gorys.addAll(
                categories.stream().map(doc ->
                        SolrUtils.getSolrDocumentFiled(doc, "id"))
                        .collect(Collectors.toList()));
        return gorys;
    }
}
