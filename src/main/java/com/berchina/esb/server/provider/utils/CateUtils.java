package com.berchina.esb.server.provider.utils;

import com.berchina.esb.server.provider.client.SeoRequest;
import com.berchina.esb.server.provider.model.SeoCateGory;
import com.berchina.esb.server.provider.model.SeoGoods;
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
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @Package com.berchina.esb.server.provider.utils
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
            }
        }
        return gories;
    }
}
