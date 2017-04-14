package com.berchina.seo.server.provider.server.crud;

import com.berchina.seo.server.configloader.config.solr.SolrServerFactoryBean;
import com.berchina.seo.server.provider.utils.Constants;
import com.berchina.seo.server.provider.utils.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;

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

    public List<Object> category(List<String> ids,SolrQuery query) throws IOException, SolrServerException {
        query.clear();

        query.setQuery(Constants.COLON_ASTERISK);
        query.setFields(this.CAT_NAME,this.CAT_ID);

        category(ids,query,this.CAT_ID);

        LOGGER.warn("[ 类目搜索 Query 指令：{} ]", query.toQueryString());
        return category(bean.solrClient().query(this.CAT_ID,query).getResults());
    }

    /* "category": [ { "id": "1020341,2353", "name": "小菜" },{ "id": "1020342,2352", "name": "牛肉" } ]*/
    public List<Object> category(SolrDocumentList documents) {
        List<Object> lists = Lists.newLinkedList();
        boolean flag = true;
        for (SolrDocument doc : documents)
        {
            Map<String,String> map = Maps.newTreeMap();
            String name = StringUtil.StringConvert(doc.get(this.CAT_NAME));
            String id = StringUtil.StringConvert(doc.get(this.CAT_ID));
            for (int i =0 ; i < lists.size(); i ++)
            {
                Map<String,String> val = (Map<String, String>) lists.get(i);
                String key = val.get(this.NAME);
                if (key.equals(name))
                {
                    flag = false;
                    String k = val.get(this.ID).concat(",").concat(id);
                    map.put(this.ID,name);
                    map.put(this.NAME,k);
                    lists.add(map);
                    lists.remove(lists.get(i));
                    break;
                }
            }
            if (flag){
                map.put(this.ID,id);
                map.put(this.NAME,name);
                lists.add(map);
            }
        }
        return lists;
    }

    private final String CAT_NAME = "catname";
    private final String CAT_ID = "category";

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
        if (object instanceof Map)
        {
            params = (Map<String, String[]>) object;
            flag = true;
        }
        SolrQuery query = new SolrQuery();
        if (object instanceof SolrQuery)
        {
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
