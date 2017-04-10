package com.seo.test.redis;

import com.alibaba.fastjson.JSON;
import com.berchina.seo.server.provider.model.SeoCateGory;
import com.berchina.seo.server.provider.utils.CateUtils;
import com.berchina.seo.server.provider.utils.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.solr.common.params.MultiMapSolrParams;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.*;

/**
 * @Package com.seo.test.redis
 * @Description: TODO (  )
 * @Author rxbyes
 * @Date 2017 上午11:44
 * @Version V1.0
 */
public class HttpSolrSearchTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HttpSolrSearchTest.class);

    public static final String SEO_CATEGORYS = "http://127.0.0.1:8983/solr/categorys";
    public static final String SEO_GOODS = "http://127.0.0.1:8983/solr/";

    public static final String SEO_CATEREV = "http://127.0.0.1:8983/solr/caterev";

    @Test
    public void search() throws IOException, SolrServerException {

        HttpSolrClient categoryClient = new HttpSolrClient.Builder(SEO_CATEGORYS).build();

        HttpSolrClient goodsClient = new HttpSolrClient.Builder(SEO_GOODS).build();

        HttpSolrClient cateRevClient = new HttpSolrClient.Builder(SEO_CATEREV).build();

        String keywords = "牛肉面";

        ModifiableSolrParams params = new ModifiableSolrParams();

        SolrQuery query = new SolrQuery();

        query.setQuery(keywords);
        query.add(CommonParams.DF,"hotwords");

        params.add(query);

        LOGGER.info(params.toQueryString());

        QueryResponse response =  goodsClient.query("goods",params);

        LOGGER.warn(JSON.toJSONString(response.getResults()));














//        setGoods(categoryClient, goodsClient, cateRevClient, keywords);
    }

    public static void setCollectSolrQuery(List catList, Map<String, String[]> params, String category) {
        StringBuilder builder = new StringBuilder();
        int a = catList.size();

        if (catList instanceof SolrDocumentList) {
            SolrDocumentList docs = (SolrDocumentList) catList;
            for (int j = 0; j < a; j++) {
                SolrDocument cateDoc = docs.get(j);
                builder.append(category).append(":").append(cateDoc.get("category"));
                if (j < a - 1) {
                    builder.append(" OR ");
                }
            }
            params.put("fq", new String[]{builder.toString()});
        } else {
            if (!StringUtils.isEmpty(catList) && a > 0) {
                for (int i = 0; i < a; i++) {
                    builder.append(category).append(":").append(catList.get(i));
                    if (i < a - 1) {
                        builder.append(" OR ");
                    }
                }
                params.put("fq", new String[]{builder.toString()});
            }
        }
    }


    @Deprecated
    public void setGoods(HttpSolrClient categoryClient, HttpSolrClient goodsClient, HttpSolrClient cateRevClient, String keywords) {
        /**
         * 创建分类搜索条件
         */
        Map<String, String[]> map = Maps.newHashMap();

        map.put("q", new String[]{"revname:*" + keywords + "*"});

        map.put("fl", new String[]{"revname,parentid,id,revlevel"});

        MultiMapSolrParams params = new MultiMapSolrParams(map);

        LOGGER.info("根据词条、短语构成搜索条件：{}", params.toString());

        try {
            QueryResponse response = categoryClient.query(params);
            // 词条搜索分类
            SolrDocumentList docs = response.getResults();
            // 保存词条分类编号
            Set<LinkedList<SeoCateGory>> sets = Sets.newHashSet();
            /**
             *  获取所有类目结构
             */
            params.getMap().clear();
            map.put("q", new String[]{"*:*"});
            map.put("start", new String[]{"0"});
            map.put("rows", new String[]{"1024"});

            QueryResponse categoriesRsp = categoryClient.query(params);

            SolrDocumentList solrDocuments = categoriesRsp.getResults();

            for (SolrDocument doc : docs) {

                sets.add(CateUtils.getSeoCateGories(doc.get("id").toString(), solrDocuments));
            }
//            LOGGER.info("搜索结果：{}", JSON.toJSON(sets));
            /**
             *   只保留三级分类
             */
            Iterator<LinkedList<SeoCateGory>> iterator = sets.iterator();

            Set<SeoCateGory> categories = Sets.newHashSet();

            while (iterator.hasNext()) {
                LinkedList<SeoCateGory> linkedList = iterator.next();
                for (SeoCateGory gory : linkedList) {
                    if (StringUtil.notNull(gory.getChilds()) && gory.getChilds().size() > 0) {
                        for (SeoCateGory cateGory : gory.getChilds()) {
                            categories.add(cateGory);
                        }
                    }
                }
            }
            /**
             * 保存三级分类提供 App 客户端使用
             */
            LOGGER.info("分类：{}", JSON.toJSON(categories));

            /**
             * 根据展示类目三级分类获取系统分类
             */
            params.getMap().clear();

            List<String> list = Lists.newLinkedList();

            Iterator<SeoCateGory> cateGoryId = categories.iterator();

            while (cateGoryId.hasNext()) {

                list.add(cateGoryId.next().getKey());
            }

            params.getMap().clear();
            map.put("q", new String[]{"*:*"});
            map.put("start", new String[]{"0"});
            map.put("rows", new String[]{"300"});
            map.put("fl", new String[]{"category"});

            setCollectSolrQuery(list, map, "revid");

            LOGGER.info("根据展示分类获取系统类目：{}", params);
            /**
             *  返回系统类目信息
             */
            QueryResponse cateRsp = cateRevClient.query(params);

            SolrDocumentList cates = cateRsp.getResults();

            params.getMap().clear();
            map.put("q", new String[]{"hotwords:" + keywords});
//            map.put("q",new String[]{"*:*"});

            map.put("start", new String[]{"0"});
            map.put("rows", new String[]{"100"});

            setCollectSolrQuery(cates, map, "category");

            LOGGER.info("系统类目编号：{}", params);


            QueryResponse goodRsp = goodsClient.query(params);

            System.out.println(JSON.toJSON(goodRsp.getResults()));
            System.out.println(JSON.toJSON(goodRsp.getResults().getNumFound()));


//            goodsClient.query(params);

        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
