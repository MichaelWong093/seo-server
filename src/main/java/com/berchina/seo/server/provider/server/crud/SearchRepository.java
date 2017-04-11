package com.berchina.seo.server.provider.server.crud;

import com.berchina.seo.server.configloader.config.solr.SolrServerFactoryBean;
import com.berchina.seo.server.provider.client.SeoRequest;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;

/**
 * @Package com.berchina.seo.server.provider.server.crud
 * @Description: 商品搜索操作 Collection Goods
 * @Author rxbyes
 * @Date 2017 下午8:11
 * @Version V1.0
 */
@Repository
public class SearchRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SearchRepository.class);

    @Autowired
    private SolrServerFactoryBean solrClient;
    private ModifiableSolrParams params = new ModifiableSolrParams();
    private SolrQuery query = new SolrQuery();

    public QueryResponse search(SeoRequest request) throws IOException, SolrServerException {

        query.setQuery(request.getHotwords());
        query.add(CommonParams.DF, "hotwords");
//        query.setFields("hotwords", "category");
        query.addFacetField("category","logistics");
        query.setFacet(true);
        query.setFacetLimit(10);

        query.setStart(Integer.valueOf(request.getStart()));
        query.setRows(Integer.valueOf(request.getRows()));

        params.add(query);

        LOGGER.warn("[ 商品搜索 Query 指令：{} ]", query.toQueryString());
        return  solrClient.solrClient().query("goods",params);
    }
}
