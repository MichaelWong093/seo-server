package com.berchina.seo.server.provider.utils;

import com.berchina.seo.server.provider.model.SeoCateGory;
import com.berchina.seo.server.provider.model.SeoGoods;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Package com.berchina.seo.server.provider.utils
 * @Description: TODO ( 类目工具类 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/9/16 下午4:56
 * @Version V1.0
 */
public class CateUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(CateUtils.class);


    public static List<Map<String, Object>> getCategoryCollection(Set<SeoCateGory> skuK, SolrDocumentList skuV) {

        List<Map<String, Object>> categorys = Lists.newLinkedList();

        for (SeoCateGory aSkuK : skuK) {

            LinkedList<SeoCateGory> values = Lists.newLinkedList();

            Map<String, Object> seoCates = Maps.newHashMap();

            String key = aSkuK.getKey();

            String value = aSkuK.getValue();

            skuV.stream().filter(aSkuV ->
                    key.equals(StringUtil.StringConvert(aSkuV.get("propid")))).forEach(aSkuV -> {

                SeoCateGory var = new SeoCateGory();

                var.setKey(StringUtil.StringConvert(aSkuV.get("vid")));

                var.setValue(StringUtil.StringConvert(aSkuV.get("prvName")));

                values.add(var);
            });
            if (!StringUtils.isEmpty(key)) {
                seoCates.put("key", key);
            }
            if (!StringUtils.isEmpty(value)) {
                seoCates.put("value", value);
            }
            if (!StringUtils.isEmpty(values) && values.size() > 0) {
                seoCates.put("childs", values);
            }
            if (!StringUtils.isEmpty(seoCates)) {
                categorys.add(seoCates);
            }
        }
        return categorys;
    }

    /**
     * 父子类目所属的叶子类目
     *
     * @param cateId       父子类目ID
     * @param documentList 索引类目集合
     * @return 父子类目所属的叶子类目
     */
    public static LinkedList<SeoCateGory> getSeoCateGories(final String cateId, SolrDocumentList documentList) {

        LinkedList<SeoCateGory> list = setCategoryTree(cateId, documentList);

        Map<String, LinkedList<SeoCateGory>> map = Maps.newHashMap();
        /**
         * 创建叶子目录
         */
        for (SeoCateGory var : list) {

            LinkedList<SeoCateGory> lists = setCategoryTree(var.getKey(), documentList);

            if (!StringUtils.isEmpty(lists) && lists.size() > 0) {

                map.put(var.getKey(), lists);

                var.setChilds(map.get(var.getKey()));
            }
        }
        return list;
    }

    public static LinkedList<SeoCateGory> setCategoryTree(String cateId, SolrDocumentList documentList) {

        LinkedList<SeoCateGory> gories = Lists.newLinkedList();

        for (SolrDocument doc : documentList) {

            String parentId = SolrUtils.getSolrDocumentFiled(doc, Constants.SEO_PARENTID);
            /**
             * 根据类目编号获取叶子类目
             */
            if (!StringUtils.isEmpty(parentId) && parentId.equals(cateId)) {

                SeoCateGory cateGory = new SeoCateGory();

                cateGory.setValue(StringUtil.StringConvert(doc.get("revname")));

                cateGory.setKey(StringUtil.StringConvert(doc.get("id")));

                gories.add(cateGory);
            }
        }
        return gories;
    }

    public static LinkedList<String> setEndCategoryQueryString(LinkedList<SeoCateGory> cateGories) {

        LinkedList<String> gories = Lists.newLinkedList();

        if (!StringUtils.isEmpty(cateGories) && cateGories.size() > 0) {
            for (int i = 0; i < cateGories.size(); i++) {

                LinkedList<SeoCateGory> seoCateGories = cateGories.get(i).getChilds();

                if (!StringUtils.isEmpty(seoCateGories) && seoCateGories.size() > 0) {
                    for (SeoCateGory gory : seoCateGories
                            ) {
                        gories.add(gory.getKey());
                    }
                }
                else {
                    gories.add(cateGories.get(i).getKey());
                }
            }
        }
        return gories;
    }

    // 解析 Goods 信息
    public static List<Object> setGoodsDoc(SolrDocumentList documents) {
        List<Object> seoGoods = Lists.newLinkedList();
        for (SolrDocument doc : documents) {
            SeoGoods goods = new SeoGoods();
            goods.setHotwords(StringUtil.StringConvert(doc.get(HOTWORDS)));
            goods.setPrices(StringUtil.StringConvert(doc.get(PICTURE)));
            goods.setPicture(StringUtil.StringConvert(doc.get(PRICES)));
            goods.setShopName(StringUtil.StringConvert(doc.get(SHOPID)));
            goods.setSales(StringUtil.StringConvert(doc.get(SALES)));
            goods.setGoodsId(StringUtil.StringConvert(doc.get(ID)));
            String integral = StringUtil.StringConvert(doc.get(INTEGRAL));
            goods.setIntegralflag(StringUtil.notNull(integral) ? StringUtil.StringConvert(integral) : 0);
            goods.setSource(StringUtil.StringConvert(doc.get(SOURCE)));
            goods.setType(StringUtil.StringConvert(doc.get(SOURCES)));
            seoGoods.add(goods);
        }
        return seoGoods;
    }

    private final static String PICTURE = "picture";
    private final static String INTEGRAL = "integralflag";
    private final static String PRICES = "prices";
    private final static String SHOPID = "shopid";
    private final static String SALES = "sales";
    private final static String HOTWORDS = "hotwords";
    private final static String SOURCE = "source";
    private final static String SOURCES = "sources";
    private final static String ID = "id";


    public static List<Map<String,String>> setLogistics(List<String> logisticsFacet) {
        Map<String,String> map = Maps.newTreeMap();
        for (String log : logisticsFacet)
        {
            List<String> logistics = StringUtil.splitter(",",log);
            for (String var : logistics)
            {
                if (var.equals(LogisticsEnum.WDZT.getCode()))
                {
                    map.put(var,LogisticsEnum.WDZT.getName());
                }
                if (var.equals(LogisticsEnum.SHSM.getCode()))
                {
                    map.put(var,LogisticsEnum.SHSM.getName());
                }
                if(var.equals(LogisticsEnum.WLPS.getCode()))
                {
                    map.put(var,LogisticsEnum.WLPS.getName());
                }
            }
        }
        return org.assertj.core.util.Lists.newArrayList(map);
    }


    public static List<String> setfacet(List<FacetField.Count> counts) {
        List<String> objects = org.assertj.core.util.Lists.newArrayList();
        if (StringUtil.notNull(counts))
        {
            for (FacetField.Count count : counts)
            {
                if (count.getCount() > 0)
                {
                    objects.add(count.getName());
                }
            }
        }
        return objects;
    }
}
