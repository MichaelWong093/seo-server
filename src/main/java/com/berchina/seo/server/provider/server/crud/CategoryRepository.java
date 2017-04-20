package com.berchina.seo.server.provider.server.crud;

import com.berchina.seo.server.configloader.config.solr.SolrServerFactoryBean;
import com.berchina.seo.server.provider.model.SeoCateGory;
import com.berchina.seo.server.provider.utils.CateUtils;
import com.berchina.seo.server.provider.utils.Constants;
import com.berchina.seo.server.provider.utils.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.assertj.core.util.Sets;
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
 * @Description: 类目搜索操作 Collection category
 * @Author rxbyes
 * @Date 2017 下午9:49
 * @Version V1.0
 */
@Repository
public class CategoryRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private SolrServerFactoryBean bean;

    private HttpSolrClient solrClient;

    public CategoryRepository(HttpSolrClient solrClient) {

        this.solrClient = solrClient;
    }

    /**
     * 展示类目二级类目所有数据
     *
     * @param query
     * @return
     * @throws IOException
     * @throws SolrServerException
     */
    public List<SeoCateGory> twoCategories(SolrQuery query) throws IOException, SolrServerException {

        return twoCategories(twoCategory(query));
    }

    public QueryResponse categoryRev(String solrQuery, SolrQuery query) throws IOException, SolrServerException {
        if (StringUtil.notNull(solrQuery)) {
            query.clear();
            query.setQuery(solrQuery);
            query.setFields(this.REVID, this.CATEGORY);
            query.setFacet(true);
            query.addFacetField(this.REVID);
            query.setFacetLimit(20);
            query.setRows(1);
            LOGGER.warn("[ 系统类目与展示类目关联 Query 指令：{} ]", query.toQueryString());
            return response("caterev", query);
        }
        throw new SolrServerException(" unknow error");
    }

    public QueryResponse response(String collection, SolrQuery query) throws IOException, SolrServerException {
        if (null == solrClient) {
            return bean.solrClient().query(collection, query);
        } else {
            return solrClient.query(collection, query);
        }
    }


    public Set<SeoCateGory> changeCategory(List<String> list, SolrQuery query, String alias) throws IOException, SolrServerException {

        return this.getSeoCateGories(this.twoCategories(query), this.categories(list, query, alias));
    }

    public SolrDocumentList categories(List<String> list, SolrQuery query, String alias) throws IOException, SolrServerException {
        query.clear();
        query.setQuery(Constants.COLON_ASTERISK);
        query.setFields(this.REVNAME, this.PARENTID, this.CATEGORY, this.REVLEVEL);
        query.setRows(1024);
        alias = StringUtil.notNull(alias) ? alias : this.CATEGORY;
        category(list, query, alias);
        LOGGER.warn("[ 展示类目 Query 指令：{} ]", query.toQueryString());
        return response("categorys", query).getResults();
    }

    public List<String> getCategoryRev(String solrQuery, SolrQuery query) throws IOException, SolrServerException {

        return CateUtils.setfacet(this.categoryRev(solrQuery, query).getFacetFields().get(0).getValues());
    }

    public List<SeoCateGory> twoCategories(QueryResponse response) {
        List<SeoCateGory> lists = Lists.newLinkedList();
        response.getResults().forEach(document -> lists.add(
                new SeoCateGory(StringUtil.StringConvert(document.get(this.CAT_ID)), StringUtil.StringConvert(document.get(this.REVNAME)))));
        return lists;
    }

    public QueryResponse twoCategory(SolrQuery query) throws IOException, SolrServerException {
        query.clear();
        query.setQuery(Constants.COLON_ASTERISK);
        query.setFilterQueries(this.REVLEVEL_1);
        query.setRows(1024);
        LOGGER.warn("[ 展示类目二级类目搜索 Query 指令：{} ]", query.toQueryString());
        return response(this.SORL_HOME_CATEGORYS, query);
    }


    /**
     * 系统展示类目在结果页展示分类区域 @ 与 筛选类目无关
     *
     * @param ids
     * @param query
     * @return
     * @throws IOException
     * @throws SolrServerException
     */
    public List<Object> category(List<String> ids, SolrQuery query) throws IOException, SolrServerException {
        query.clear();
        query.setQuery(Constants.COLON_ASTERISK);
        query.setFields(this.CAT_NAME, this.CAT_ID);
        category(ids, query, this.CAT_ID);
        LOGGER.warn("[ 类目搜索 Query 指令：{} ]", query.toQueryString());
        return category(response(this.CAT_ID, query).getResults());
    }

    /**
     * 刷选 分类
     *
     * @param twoCategories   展示类目二级分类
     * @param threeCategories 展示类目三级分类
     * @return 展示类目结构
     */
    public Set<SeoCateGory> getSeoCateGories(List<SeoCateGory> twoCategories, SolrDocumentList threeCategories) {
        Set<SeoCateGory> set = Sets.newLinkedHashSet();
        for (SeoCateGory cateGory : twoCategories) {
            boolean flag = false;
            SeoCateGory seoCateGory = new SeoCateGory();
            LinkedList<SeoCateGory> childs = Lists.newLinkedList();
            for (SolrDocument doc : threeCategories
                    ) {
                String id = StringUtil.StringConvert(doc.get(this.PARENTID)),
                        key = StringUtil.StringConvert(doc.get(this.CATEGORY)),
                        val = StringUtil.StringConvert(doc.get(this.REVNAME));
                if (cateGory.getKey().equals(id)) {
                    childs.add(new SeoCateGory(key, val));
                    flag = true;
                }
            }
            if (flag) {
                seoCateGory.setKey(cateGory.getKey());
                seoCateGory.setValue(cateGory.getValue());
                seoCateGory.setChilds(childs);
                set.add(seoCateGory);
            }
        }
        return set;
    }

    /* "category": [ { "id": "1020341,2353", "name": "小菜" },{ "id": "1020342,2352", "name": "牛肉" } ]*/
    public List<Object> category(SolrDocumentList documents) {
        List<Object> lists = Lists.newLinkedList();
        boolean flag = true;
        for (SolrDocument doc : documents) {
            Map<String, String> map = Maps.newTreeMap();
            String name = StringUtil.StringConvert(doc.get(this.CAT_NAME));
            String id = StringUtil.StringConvert(doc.get(this.CAT_ID));
            for (int i = 0; i < lists.size(); i++) {
                Map<String, String> val = (Map<String, String>) lists.get(i);
                String key = val.get(this.NAME);
                if (key.equals(name)) {
                    flag = false;
                    String k = val.get(this.ID).concat(",").concat(id);
                    map.put(this.ID, name);
                    map.put(this.NAME, k);
                    lists.add(map);
                    lists.remove(lists.get(i));
                    break;
                }
            }
            if (flag) {
                map.put(this.ID, id);
                map.put(this.NAME, name);
                lists.add(map);
            }
        }
        return lists;
    }

    private final String CAT_NAME = "catname";

    private final String CAT_ID = "category";

    private final String REVNAME = "revname";

    private final String PARENTID = "parentid";

    private final String CATEGORY = "category";

    private final String REVID = "revid";

    private final String REVLEVEL_1 = "revlevel:1";

    private final String REVLEVEL = "revlevel";

    private final String SORL_HOME_CATEGORYS = "categorys";

    private final String ID = "id";

    private final String NAME = "name";

    public void category(List catList, SolrQuery query, String category) {
        setSolrQuery(catList, query, category);
    }

    public void category(List catList, Map<String, String[]> params, String category) {
        setSolrQuery(catList, params, category);
    }

    public void setSolrQuery(List catList, Object object, String category) {
        boolean flag = true;
        Map<String, String[]> params = Maps.newConcurrentMap();
        if (object instanceof Map) {
            params = (Map<String, String[]>) object;
            flag = true;
        }
        SolrQuery query = new SolrQuery();
        if (object instanceof SolrQuery) {
            query = (SolrQuery) object;
            flag = false;
        }
        StringBuffer builder = new StringBuffer();
        int a = catList.size();
        if (catList instanceof SolrDocumentList) {
            SolrDocumentList docs = (SolrDocumentList) catList;
            for (int j = 0; j < a; j++) {
                SolrDocument cateDoc = docs.get(j);
                builder.append(category).append(":").append(cateDoc.get(this.CAT_ID));
                if (j < a - 1) {
                    builder.append(" OR ");
                }
            }
            if (flag) {
                params.put("fq", new String[]{builder.toString()});
            } else {
                query.addFilterQuery(new String(builder));
            }
        } else {
            if (!StringUtils.isEmpty(catList) && a > 0) {
                for (int i = 0; i < a; i++) {
                    builder.append(category).append(":").append(catList.get(i));
                    if (i < a - 1) {
                        builder.append(" OR ");
                    }
                }
                if (flag) {
                    params.put("fq", new String[]{builder.toString()});
                } else {
                    query.addFilterQuery(new String(builder));
                }
            }
        }
    }
}
