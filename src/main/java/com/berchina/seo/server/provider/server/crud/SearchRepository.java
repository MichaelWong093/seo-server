package com.berchina.seo.server.provider.server.crud;

import com.berchina.seo.server.configloader.config.solr.SolrServerFactoryBean;
import com.berchina.seo.server.provider.client.SeoRequest;
import com.berchina.seo.server.provider.utils.SolrUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.DisMaxParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

/**
 * @Package com.berchina.seo.server.provider.server.crud
 * @Description: 商品搜索操作 Collection Goods
 * @Author rxbyes
 * @Date 2017 下午8:11
 * @Version V1.0
 */
@Repository
public class SearchRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private SolrServerFactoryBean solrClient;
    private ModifiableSolrParams params = new ModifiableSolrParams();
    private SolrQuery query = new SolrQuery();

    public QueryResponse search(SeoRequest request,ModifiableSolrParams params) throws IOException, SolrServerException
    {
        this.params = params;
        return this.search(request);
    }

    public QueryResponse search(SeoRequest request) throws IOException, SolrServerException {

//        df=hotwords&fl=hotwords&indent=on&q=苹果手机&wt=json&defType=synonym_edismax&synonyms=true&rows=20

        query.clear();
        params.clear();

        query.setQuery(request.getHotwords());
        query.add(CommonParams.DF, "hotwords");
//        query.setFields("hotwords", "category");
        query.addFacetField("category","logistics");
        query.setFacet(true);
        query.setFacetLimit(10);

        query.add("synonyms","true");
        query.add("defType","synonym_edismax");
        query.add(DisMaxParams.MM,"100%");


        query.setStart(Integer.valueOf(request.getStart()));
        query.setRows(Integer.valueOf(request.getRows()));

        SolrUtils.setSolrPage(query, request);

        params.add(query);

        LOGGER.warn("[ 商品搜索 Query 指令：{} ]", query.toQueryString());
        return  search(request.getType());
    }

    /**
     *
     * @param type 请求类型 1：全站搜索、0：特产频道搜索
     * @return
     * @throws IOException
     * @throws SolrServerException
     */
    private QueryResponse search(String type) throws IOException, SolrServerException
    {
        return solrClient.solrClient().query("goods",params);
    }
}
