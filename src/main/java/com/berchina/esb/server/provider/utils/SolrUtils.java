package com.berchina.esb.server.provider.utils;

import java.io.IOException;
import java.util.*;

import com.berchina.esb.server.provider.model.SeoCateGory;
import com.berchina.esb.server.provider.model.SeoGoods;
import com.berchina.esb.server.provider.model.SeoShop;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import com.berchina.esb.server.configloader.exception.SeoException;
import com.berchina.esb.server.configloader.exception.server.ServerException;
import com.berchina.esb.server.provider.client.SeoRequest;
import com.berchina.esb.server.provider.segment.EasySeg;
import com.hankcs.hanlp.dictionary.py.Pinyin;

/**
 * @Package com.berchina.esb.server.provider.utils
 * @Description: TODO ( 搜索工具类 )
 * @Author 任小斌 renxiaobin@berchin.com
 * @Date 16/9/6 下午3:26
 * @Version V1.0
 */
public class SolrUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(SolrUtils.class);

    private static final long rows = 10;

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

    /**
     * 系统类目
     *
     * @param catList
     * @param query
     */
    public static void query(List<String> catList, SolrQuery query, SeoRequest request) {

        if (!StringUtils.isEmpty(catList) && catList.size() > 0) {
            StringBuilder builder = new StringBuilder();
            setCollectSolrQuery(catList, query, builder, "category");
        }

        if (!StringUtils.isEmpty(request.getSort())) {

            query.set("sort", getSortRule(request));
        }

        /**
         * 三级类目搜索使用
         */
        query.setFacet(true);
        query.addFacetField("vid");
        query.addFacetField("category");

        setSolrPage(query, request);

        LOGGER.info(" [ SOLR SQL 语法: {}] ", query);
    }


    public static void setShopSolrQuery(List<SeoShop> shops,
                                        SolrQuery query, StringBuilder builder, String var) {
        if (!StringUtils.isEmpty(shops) && shops.size() > 0) {
            Iterator<SeoShop> iterator = shops.iterator();
            while (iterator.hasNext()) {
                SeoShop shop = iterator.next();
                builder.append("!").append(var).append(":").append(shop.getShopid());
                if (iterator.hasNext()) {
                    builder.append(" OR ");
                }
            }
        }
        query.set("fq", new String(builder));
    }


    public static void setCollectSolrQuery(Set<SeoCateGory> skus,
                                           SolrQuery query, StringBuilder builder, String category, String catid) {
        if (!StringUtils.isEmpty(skus) && skus.size() > 0) {
            Iterator<SeoCateGory> iterator = skus.iterator();
            builder.append("( ");
            while (iterator.hasNext()) {
                SeoCateGory sku = iterator.next();
                builder.append(category).append(":").append(sku.getKey());
                if (iterator.hasNext()) {
                    builder.append(" OR ");
                }
            }
            builder.append(" )").append(" AND category:").append(catid);
            query.set("fq", new String(builder));
        }
    }


    public static void setCollectSolrQuery(List<String> catList, SolrQuery query, StringBuilder builder, String category) {
        query.clear();
        query.set("q", "*:*");
        int a = catList.size();
        if (!StringUtils.isEmpty(catList) && a > 0) {
            for (int i = 0; i < a; i++) {
                builder.append(category).append(":").append(catList.get(i));
                if (i < a - 1) {
                    builder.append(" OR ");
                }
            }
            query.set("fq", new String(builder));
        }
        LOGGER.info(" [ SOLR SQL 语法: {}] ", query);
    }


    /**
     * 类目属性组合
     *
     * @param catList
     * @param query
     */
    public static void query(List<String> catList, SolrQuery query) {
        StringBuilder builder = new StringBuilder();
        query.clear();
        query.set("q", "*:*");
        setCollectSolrQuery(catList, query, builder, "propid");
        setStartAndRows(query);
        LOGGER.info(" [ SOLR SQL 语法: {}] ", query);
    }


    public static void query(final String var2, SolrQuery query) {
        query.clear();
        query.set("q", getQueryQ(null));
        if (!StringUtils.isEmpty(var2)) {
            query.set("fq", getQueryQ("category", var2));
        }
        LOGGER.info(" [ SOLR SQL 语法: {}] ", query);
    }

    public static SolrQuery queryCategoryKey(SeoRequest request, SolrQuery query) {
        query.set("q", "*:*");

        query.set("fl", "propid,proName");

        query.set("fq", getQueryQ("category", request.getCategory()));

        setStartAndRows(query);

        LOGGER.info(" [ SOLR SQL 语法: {}] ", query);

        return query;
    }

    public static void queryCategoryValue(SeoRequest request, SolrQuery query) {
        query.clear();

        query.set("q", "*:*");

        query.set("fl", "vid,prvName,propid");

        query.set("fq", getQueryQ("category", request.getCategory()));

        query.set("fq", request.getAttribute());

        setStartAndRows(query);

        LOGGER.info(" [ SOLR SQL 语法: {}] ", query);
    }

    public static void querys(SeoRequest request, ModifiableSolrParams params, boolean flag) {

        SolrQuery query = new SolrQuery();

        if (flag) {
            query.set("q", getQueryQs(request));
        } else {
            query.set("q", getQueryQ(request));
        }

        query.set("fl", "category");

        query.setFacet(true);

        query.addFacetField("category");

        query.set("start", "0");

        query.set("rows", "1");

        params.add(query);

        LOGGER.info(" [ SOLR SQL 语法: {}] ", query);
    }

    /**
     * 类目关联关系
     *
     * @param request
     * @param params
     */
    public static void query(SeoRequest request, ModifiableSolrParams params) {

        params.clear();

        SolrQuery query = new SolrQuery();

        query.set("q", getQueryQ(request));

        setHighlight(query);

        String brand = request.getBrand();

        StringBuilder builder = new StringBuilder();

        if (!StringUtils.isEmpty(brand)) {

            query.addFilterQuery(setBrandQuery(brand, builder, "brand"));
        }
        String attr = request.getAttribute();

        if (!StringUtils.isEmpty(attr)) {

            query.addFilterQuery(getStringAtrr(attr));
        }

        if (!StringUtils.isEmpty(request.getSort())) {

            query.set("sort", getSortRule(request));
        }

        if (!StringUtils.isEmpty(request.getOther())) {

            query.set("fq", getQueryQ("source", request.getOther()));

            query.addFilterQuery("source:" + request.getOther());
        }

        setSolrPage(query, request);

        params.add(query);

        LOGGER.info(" [ SOLR SQL 语法: {}] ", params);
    }

    private static void setHighlight(SolrQuery query) {
        query.setHighlight(true); // 开启高亮组件
        query.addHighlightField("hotwords"); // 高亮字段
        query.setHighlightSimplePre("<font color='red'>");//标记，高亮关键字前缀
        query.setHighlightSimplePost("</font>");// 后缀
//        query.setHighlightRequireFieldMatch(true);
        query.setHighlightSnippets(1); // 结果分片数，默认为1
        query.setHighlightFragsize(1000); // 每个分片的最大长度，默认为100

        query.set("hl.usePhraseHighlighter", true);
        query.set("hl.highlightMultiTerm", true);
    }

    /**
     * @param var1    搜索属性
     * @param builder
     * @param var     搜索属性名称
     */
    private static String setBrandQuery(String var1, StringBuilder builder, String var) {
        String[] args = var1.split(",");
        for (int i = 0; i < args.length; i++) {
            builder.append(var).append(":").append(args[i]);
            if (i < args.length - 1) {
                builder.append(" OR ");
            }
        }
        return new String(builder);
    }


    public static void setSolrPage(SolrQuery query, SeoRequest request) {
        if (StringUtils.isEmpty(request.getStart())
                || request.getStart().equals("0") || request.getStart().equals("1")) {
            query.set("start", "0");
            request.setPage("1");
        } else {
            String pageNum = StringUtil.StringConvert(
                    Long.valueOf(request.getRows()) * (Long.valueOf(request.getStart()) - 1));
            query.set("start", pageNum);
            request.setPage(request.getStart());
        }
        if (StringUtils.isEmpty(request.getRows())) {
            query.set("rows", StringUtil.StringConvert(rows));
        } else {
            query.set("rows", request.getRows());
        }
    }

    /**
     * 展示类目
     *
     * @param query
     */
    public static void queryCategorys(SolrQuery query) {
        query.clear();
        query.set("q", "*:*");
        query.set("start", "0");
        query.set("rows", "500");
        LOGGER.info(" [ SOLR SQL 语法: {}] ", query);
    }

    public static void queryCategorys(SeoRequest request, SolrQuery query) {
        query.clear();

        query.set("q", "*:*");

        if (!StringUtils.isEmpty(request.getCategory())) {
            query.set("fq", request.getCategory());
        }

        query.set("fl", "category,id");
        query.set("start", "0");
        query.set("rows", "260");
        LOGGER.info(" [ SOLR SQL 语法: {}] ", query);
    }

    public static void setStartAndRows(SolrQuery query) {
        query.set("start", "0");
        query.set("rows", "300");
    }

    /**
     * 店铺搜索参数过滤
     *
     * @param request
     * @param params
     */
    public static void queryShop(SeoRequest request, ModifiableSolrParams params) {

        SolrQuery query = new SolrQuery();

        query.set("q", getQueryQ(request));

        if (!StringUtils.isEmpty(request.getTerminal()) && request.getTerminal().equals("app")) {

            if (!StringUtils.isEmpty(request.getLongitude()) && !StringUtils.isEmpty(request.getLatitude())) {

                query.set("fq", "{!geofilt}");

                query.set("pt", request.getLongitude() + " " + request.getLatitude());

                query.set("sfield", "position");

                query.set("d", "5000");

                if (!StringUtils.isEmpty(request.getSort())) {

                    if (request.getSort().equals("dist")) {

                        String rule = request.getRule() == null ? "asc" : request.getRule();

                        query.set("sort", "geodist() " + rule);

                    } else {
                        query.set("sort", getSortRule(request));
                    }
                }
                query.set("fl", "*,_dist_:geodist(),score");
            }
        }

        if (!StringUtils.isEmpty(request.getOther())) {

            query.addFilterQuery("source:" + request.getOther());
        }
        setHighlight(query);

        setSolrPage(query, request);

        params.add(query);

        LOGGER.info(" [ SOLR SQL 语法: {}] ", params);
    }

    public static void setShopbyGoods(SeoRequest request, SolrQuery query) {
        query.clear();
        query.set("q", "*:*");

        if (!StringUtils.isEmpty(request.getAttribute())) {
            query.set("fq", getQueryQ("shopid", request.getAttribute()));
        }
//        setHighlight(query);
        query.set("start", "0");
        query.set("rows", "3");
        LOGGER.info(" [ SOLR SQL 语法: {}] ", query);
    }

    /**
     * 品牌搜索关联关系
     *
     * @param request
     * @param query
     */
    public static void queryBrandRev(SeoRequest request, SolrQuery query) {
        query.clear();
        query.set("q", "*:*");
        query.set("fl", "brand");
        if (!StringUtils.isEmpty(request.getCategory())) {
            query.set("fq", getQueryQ("category", request.getCategory()));
        }
        LOGGER.info(" [ SOLR SQL 语法: {}] ", query);
    }


    /**
     * 获取类目对应的品牌编号
     *
     * @param brRev 品牌索引集合
     * @return 品牌编号
     */
    public static List<String> getBrandIDCollection(SolrDocumentList brRev) {
        List<String> brsts = Lists.newLinkedList();
        for (int i = 0; i < brRev.size(); i++) {
            brsts.add(SolrUtils.getParameter(brRev, i, "brand"));
        }
        return brsts;
    }


    /**
     * 获取类目属性值
     *
     * @param document 搜索文档集合
     * @param filed    搜索属性
     * @return 属性值
     */
    public static String getSolrDocumentFiled(SolrDocument document, final String filed) {

        return StringUtil.StringConvert(document.get(filed));
    }


    /**
     * Solr Query 请求指令
     *
     * @param request 请求参数
     * @param query   请求 SQL
     */
    public static void queryParameter(SeoRequest request, SolrQuery query) {
        query.clear();
        query.set("q", getQueryQ(request));
        /**
         * 商品属性搜索
         */
        if (!StringUtils.isEmpty(request.getCategory())) {
            query.set("fq", getQueryQ("category", request.getCategory()));
        }
        /**
         * 排序
         */
        if (!StringUtils.isEmpty(request.getSort())) {

            query.set("sort", getSortRule(request));
        }
        setSolrPage(query, request);
        LOGGER.info(" [ SOLR SQL 语法: {}] ", query);
    }

    /**
     * 排序规则封装
     *
     * @param request 用户搜索条件
     * @return sort 值结果
     */
    public static String getSortRule(SeoRequest request) {
        return request.getSort().concat(Constants.SPACE).concat(request.getRule());
    }


    public static String getQueryQ(SeoRequest request) {

        StringBuilder builder = new StringBuilder();

        if (null == request || StringUtils.isEmpty(request.getHotwords())) {
            return Constants.COLON_ASTERISK;
        }
        if (request.getHotwords().length() == 1) {
            return
                    builder.append(EnumUtils.SEO_HOTWORDS.getName())
                            .append(Constants.COLON).append(Constants.ASTERISK)
                            .append(request.getHotwords()).append(Constants.ASTERISK).toString();
        }
        return EnumUtils.SEO_HOTWORDS.getName()
                .concat(Constants.COLON).concat(request.getHotwords());
    }

    /**
     * 商品搜索
     *
     * @param request 用户搜索参数 Q 值
     * @return Q 值结果
     */
    public static String getQueryQs(SeoRequest request) {
        StringBuilder builder = new StringBuilder();
        if (!StringUtils.isEmpty(request.getHotwords())) {
            return
                    builder.append(EnumUtils.SEO_HOTWORDS.getName())
                            .append(Constants.COLON).append(Constants.ASTERISK)
                            .append(request.getHotwords()).append(Constants.ASTERISK).toString();
        }
        return null;
    }

    /**
     * 可选搜索参数
     *
     * @param var1 搜索字段名称
     * @param var2 搜索关键词
     * @return FQ 值
     */
    public static String getQueryQ(final String var1, final String var2) {
        return var1
                .concat(
                        Constants.COLON).concat(var2);
    }

    /**
     * 业务数据处理
     *
     * @param doc  返回搜索集合
     * @param i    遍历节点索引下标
     * @param args 响应结果所需字段名称
     * @return 响应属性名称
     */
    public static String getParameter(SolrDocumentList doc, int i, String args) {
        String var = StringUtil.StringConvert(doc.get(i).getFieldValue(args));
        if (StringUtil.notNull(var)) {
            return var;
        }
        return null;
    }

    /*
     * 通过 制定的属性 比较 两个 搜索后的SolrDocument
     */
    public static boolean solrDocEqualByField(String fieldName, SolrDocument origDoc, SolrDocument destDoc) {
        if (null == origDoc || null == destDoc) {
            return false;
        }
        if (null == origDoc.getFieldValue(fieldName) || null == destDoc.getFieldValue(fieldName)) {
            return false;
        }
        if (StringUtils.isEmpty(origDoc.getFieldValue(fieldName)) || StringUtils.isEmpty(destDoc.getFieldValue(fieldName))) {
            return false;
        }
        return origDoc.getFieldValue(fieldName).equals(destDoc.getFieldValue(fieldName));
    }

    /*
     * add by yhq
     * 添加 索引 参数为实体 bean (属性 必须带注解 @Field)
     */
    public static void addBean(HttpSolrClient solrClient,
                               Object obj) {
        try {
            solrClient.addBean(obj);
        } catch (SolrServerException | IOException e) {
            rollBack(solrClient);
            LOGGER.error("[ solr 服务器异常 : {} ]", e.getMessage());
            throw new SeoException(e.getMessage(),
                    ServerException.SEO_SERVER_URL_CONNECT_ERROR);
        } finally {
            commit(solrClient);
        }
    }

    /*
     * add by yhq 添加索引返回 UpdateResponse
     */
    public static UpdateResponse addThen(HttpSolrClient solrClient,
                                         SolrInputDocument solrInput) {
        UpdateResponse response = null;
        try {
            response = solrClient.add(solrInput);
        } catch (SolrServerException | IOException e) {
            rollBack(solrClient);
            LOGGER.error("[ solr 服务器异常 : {} ]", e.getMessage());
            throw new SeoException(e.getMessage(),
                    ServerException.SEO_SERVER_URL_CONNECT_ERROR);
        } finally {
            commit(solrClient);
        }
        return response;
    }

    /*
     * add by yhq 添加索引，solrInput 已经设置了属性
     */
    public static void add(HttpSolrClient solrClient,
                           SolrInputDocument solrInput) {
        try {
            solrClient.add(solrInput);
        } catch (SolrServerException | IOException e) {
            rollBack(solrClient);
            LOGGER.error("[ solr 服务器异常 : {} ]", e.getMessage());
            throw new SeoException(e.getMessage(),
                    ServerException.SEO_SERVER_URL_CONNECT_ERROR);
        } finally {
            commit(solrClient);
        }
    }

    /*
     * 给索引文档添加 关键字的 拼音和全拼的字段 Field
     */
    public static void convert2PinyinField(SolrInputDocument solrDoc, String keyWords) {
        if (StringUtil.isChinese(keyWords)) {
            List<Pinyin> pinyinList = EasySeg.convertToPinyinList(keyWords);
            String pinyin = "";
            String abbreviation = "";
            for (Pinyin py : pinyinList) {
                // 热词转换pinyin
                pinyin += py.getPinyinWithoutTone();
                abbreviation += py.getHead();
            }
            if (pinyin.indexOf("none") > 0 || abbreviation.indexOf("none") > 0) {//去除非汉子的拼音问题
                pinyin = pinyin.replaceAll("none", "");
                abbreviation = abbreviation.replaceAll("none", "");
            }
            solrDoc.addField("pinyin", pinyin);
            solrDoc.addField("abbre", abbreviation);
        }
    }


    public static LinkedList<SeoGoods> setSeoGoodsResponseInfos(
            Map<String, Map<String, List<String>>> maps, SolrDocumentList doc) {
        LinkedList<SeoGoods> seoGoodses = Lists.newLinkedList();
        for (int i = 0; i < doc.size(); i++) {
            String id = SolrUtils.getParameter(doc, i, "id");
            SeoGoods goods = new SeoGoods();
            goods.setHotwords(String.valueOf(maps.get(id).get("hotwords")).replace("[", "").replace("]", ""));
            goods.setPrices(SolrUtils.getParameter(doc, i, "prices"));
            goods.setPicture(SolrUtils.getParameter(doc, i, "picture"));
            goods.setShopName(SolrUtils.getParameter(doc, i, "shopid"));
            goods.setSales(SolrUtils.getParameter(doc, i, "sales"));
            goods.setGoodsId(id);
            goods.setSource(SolrUtils.getParameter(doc, i, "source"));
            goods.setType(SolrUtils.getParameter(doc, i, "sources"));
            seoGoodses.add(goods);
        }
        return seoGoodses;
    }

    /**
     * 普通商品数据处理
     *
     * @param doc 索引数据
     */
    public static void setSeoGoodsResponseInfo(LinkedList<SeoGoods> seoGoodses, SolrDocumentList doc) {
        int args = doc.size();
        if (!StringUtils.isEmpty(args) && args > 0) {
            for (int i = 0; i < args; i++) {
                SeoGoods goods = new SeoGoods();
                String id = SolrUtils.getParameter(doc, i, "id");
                goods.setHotwords(SolrUtils.getParameter(doc, i, "hotwords"));
                goods.setPrices(SolrUtils.getParameter(doc, i, "prices"));
                goods.setPicture(SolrUtils.getParameter(doc, i, "picture"));
                goods.setShopName(SolrUtils.getParameter(doc, i, "shopid"));
                goods.setSales(SolrUtils.getParameter(doc, i, "sales"));
                goods.setGoodsId(id);
                goods.setSource(SolrUtils.getParameter(doc, i, "source"));
                goods.setType(SolrUtils.getParameter(doc, i, "sources"));
                seoGoodses.add(goods);
            }
        }
    }


    /*
     * add by yhq 回滚事务
     */
    public static void rollBack(HttpSolrClient solrClient) {
        try {
            if (null != solrClient) {
                solrClient.rollback();
            }

        } catch (SolrServerException | IOException e) {
            LOGGER.error("[ solr 服务器异常 : {} ]", e.getMessage());
            throw new SeoException(e.getMessage(),
                    ServerException.SEO_SERVER_URL_CONNECT_ERROR);
        }
    }

    /*
     * add by yhq 提交事务
     */
    public static void commit(HttpSolrClient solrClient) {
        try {
            if (null != solrClient) {
                solrClient.commit();
            }

        } catch (SolrServerException | IOException e) {
            LOGGER.error("[ solr 服务器异常 : {} ]", e.getMessage());
            throw new SeoException(e.getMessage(),
                    ServerException.SEO_SERVER_URL_CONNECT_ERROR);
        }
    }

    /*
     * ad by yhq 回滚事务，关闭资源
     */
    private static void rollBackAndClose(HttpSolrClient solrClient) {
        try {
            if (null != solrClient) {
                solrClient.rollback();
                solrClient.close();
            }

        } catch (SolrServerException | IOException e) {
            LOGGER.error("[ solr 服务器异常 : {} ]", e.getMessage());
            throw new SeoException(e.getMessage(),
                    ServerException.SEO_SERVER_URL_CONNECT_ERROR);
        }
    }

    /*
     * add by yhq 提交事务，关闭资源
     */
    private static void commitAndClose(HttpSolrClient solrClient) {
        try {

            if (null != solrClient) {
                solrClient.commit();
                solrClient.close();
            }
        } catch (SolrServerException | IOException e) {
            LOGGER.error("[ solr 服务器异常 : {} ]", e.getMessage());
            throw new SeoException(e.getMessage(),
                    ServerException.SEO_SERVER_URL_CONNECT_ERROR);
        }
    }

    /*
     * 根据 channel 获取对应热词的字段名称
	 */
    public static String getNameByChannel(String channel) {
        switch (channel) {//根据 入参的类型 判别 排序的字段名称
            case Constants.SEO_GOODS_:
                return Constants.INDEX_GOODSNAME;
            case Constants.SEO_SHOP_:
                return Constants.INDEX_SHOPNAME;
            default:
                return Constants.NOT_EXIST;
        }
    }

    /*
     * 根据热词索引渠道 获取相应的 索引排序字段名称
     */
    public static String getNameByHWChannel(String channel) {
        switch (channel) {//根据 入参的类型 判别 排序的字段名称
            case Constants.SEO_GOODS_HOTWD_:
                return Constants.INDEX_GOODSNAME;
            case Constants.SEO_SHOP_HOTWD_:
                return Constants.INDEX_SHOPNAME;
            default:
                return Constants.NOT_EXIST;
        }
    }

    /*
     * 删除所有的索引,并提交
     */
    public static void deleteByAllQueryAndCnmmit(HttpSolrClient solrClient) {
        try {
            solrClient.deleteByQuery(Constants.COLON_ASTERISK);
        } catch (SolrServerException | IOException e) {
            rollBack(solrClient);
            LOGGER.error("[ solr 服务器异常 : {} ]", e.getMessage());
            throw new SeoException(e.getMessage(),
                    ServerException.SEO_SERVER_URL_CONNECT_ERROR);
        } finally {
            commit(solrClient);
        }
    }

    /*
     * 删除索引 不提交，提供删除 增加的 原子 事务操作
     */
    public static void deleteByAllQuery(HttpSolrClient solrClient) {
        try {
            solrClient.deleteByQuery(Constants.COLON_ASTERISK);
        } catch (SolrServerException | IOException e) {
            rollBack(solrClient);
            LOGGER.error("[ solr 服务器异常 : {} ]", e.getMessage());
            throw new SeoException(e.getMessage(),
                    ServerException.SEO_SERVER_URL_CONNECT_ERROR);
        }
    }

    /**
     * 属性搜搜
     *
     * @param vars 支持多个属性值搜索
     * @return
     */
    public static String getStringAtrr(String vars) {

        if (org.springframework.util.StringUtils.isEmpty(vars)) {
            Assert.notNull(" Commodity attribute not empty ", vars);
        }

        StringBuilder bd = new StringBuilder();
        /**
         * 属性键
         */
        List<String> key = Lists.newLinkedList();

        /**
         * 属性值
         */
        List<String> value = Lists.newLinkedList();

        String[] var = vars.split(",");

        for (int i = 0; i < var.length; i++) {
            String[] vid = var[i].split(":");
            for (int j = 0; j < vid.length; j++) {
                if (j % 2 == 0) {
                    key.add(vid[j]);
                } else {
                    value.add(vid[j]);
                }
            }
        }

        HashSet<String> eqSet = Sets.newHashSet();
        eqSet.addAll(key);

        Iterator<String> iterator = eqSet.iterator();

        while (iterator.hasNext()) {
            String ks = iterator.next();
            List<String> st = Lists.newLinkedList();
            for (int k = 0; k < key.size() && k < value.size(); k++) {
                if (ks.equals(key.get(k))) {
                    st.add(value.get(k));
                }
            }

            int a = st.size();

            bd.append(" ( ");
            for (int i = 0; i < a; i++) {
                bd.append("vid").append(":").append(st.get(i));
                if (i < a - 1) {
                    bd.append(" OR ");
                }
            }
            bd.append(" ) ");
            if (iterator.hasNext()) {
                bd.append(" AND ");
            }
        }
        return new String(bd);
    }

}
