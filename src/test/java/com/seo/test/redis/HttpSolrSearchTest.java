package com.seo.test.redis;

import com.alibaba.fastjson.JSON;
import com.berchina.seo.server.provider.server.crud.CategoryRepository;
import com.berchina.seo.server.provider.utils.Constants;
import com.berchina.seo.server.provider.utils.LogisticsEnum;
import com.berchina.seo.server.provider.utils.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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

    public static final String SOLR_HOME = "http://127.0.0.1:8983/solr/";

    public static final String SEO_CATEREV = "http://127.0.0.1:8983/solr/caterev";

    @Test
    public void search() throws IOException, SolrServerException {

//        HttpSolrClient categoryClient = new HttpSolrClient.Builder(SEO_CATEGORYS).build();

        HttpSolrClient solrClient = new HttpSolrClient.Builder(SOLR_HOME).build();

//        HttpSolrClient cateRevClient = new HttpSolrClient.Builder(SEO_CATEREV).build();
        String keywords = "牛肉面";
        ModifiableSolrParams params = new ModifiableSolrParams();
        SolrQuery query = new SolrQuery();
        query.setQuery(keywords);
        query.add(CommonParams.DF, "hotwords");
        query.setFields("hotwords", "category");
        query.addFacetField("category", "logistics");
        query.setFacet(true);
        query.setFacetLimit(10);
        params.add(query);

        System.out.println(params.toQueryString());
        QueryResponse goodsRsp = solrClient.query("goods", params);
        List<FacetField> facetFields = goodsRsp.getFacetFields();

        query.clear();
        query.setQuery(Constants.COLON_ASTERISK);
        query.setFields("catname", "id");


        List<String> category = setfacet(facetFields.get(0).getValues());

        System.out.println(JSON.toJSON(category));

        CategoryRepository repository = new CategoryRepository();

        repository.category(category, query, "id");
        System.out.println("系统类目：" + query.toQueryString());
        QueryResponse categoryRsp = solrClient.query("category", query);
        SolrDocumentList categoryDoc = categoryRsp.getResults();

        List<Object> categories = repository.category(categoryDoc);
        System.out.println(JSON.toJSON(categories));

        /**
         *  配送方式
         */
        List<String> logisticsFacet = setfacet(facetFields.get(1).getValues());
        System.out.println(JSON.toJSON(logisticsFacet));

        Map<String,String> map = Maps.newTreeMap();
        for (String log : logisticsFacet)
        {
            List<String> logistics = StringUtil.splitter(",",log);
            for (String var : logistics)
            {
                if (var.equals(LogisticsEnum.WDZT.getCode())){
                    map.put(var,LogisticsEnum.WDZT.getName());
                }
                if (var.equals(LogisticsEnum.SHSM.getCode())){
                    map.put(var,LogisticsEnum.SHSM.getName());
                }
                if(var.equals(LogisticsEnum.WLPS.getCode()))
                {
                    map.put(var,LogisticsEnum.WLPS.getName());
                }
            }
        }
        System.out.println(JSON.toJSON(map));
    }


    public List<String> setfacet(List<FacetField.Count> counts) {
        List<String> objects = Lists.newArrayList();
        if (StringUtil.notNull(counts)) {
            for (FacetField.Count count : counts) {
                if (count.getCount() > 0) {
                    objects.add(count.getName());
                }
            }
        }
        return objects;
    }
}
