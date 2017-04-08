package com.berchina.seo.server.provider.server.crud;

import com.berchina.seo.server.configloader.config.solr.SolrServerFactoryBean;
import com.berchina.seo.server.configloader.exception.SeoException;
import com.berchina.seo.server.provider.utils.StringUtil;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.Assert;

import java.io.IOException;

/**
 * @Package com.berchina.seo.server.provider.server.crud
 * @Description: 联想词操作
 * @Author rxbyes
 * @Date 2017 下午5:14
 * @Version V1.0
 */
@Repository
public class SuggestRepository {

    @Autowired
    private SolrServerFactoryBean factoryBean;

    private ModifiableSolrParams params = new ModifiableSolrParams();

    private SolrQuery query = new SolrQuery();

    private static final String COLLECTION_NAME = "gdhotwd";

    public QueryResponse search(String keyword) throws IOException, SolrServerException {

        HttpSolrClient solrClient = factoryBean.solrClient();

        query.setQuery(keyword);

        query.add(CommonParams.DF, "suggest");

        params.add(query);

        System.out.println("  =======  " + params + " ==========");

        QueryResponse response = solrClient.query(COLLECTION_NAME, params);

        query.clear();
        params.clear();
        return response;
    }

    public boolean add(String keyword, String correlation) throws IOException, SolrServerException {

        HttpSolrClient solrClient = factoryBean.solrClient();

        SolrInputDocument doc = new SolrInputDocument();

        Assert.notNull("keyword is not empty ",keyword);

        doc.addField("keyword",keyword);



        if (StringUtil.notNull(correlation))
        {
            doc.addField("correlation",correlation);
        }
        solrClient.add(COLLECTION_NAME, doc);

        return true;
    }

    public void delete(String keyword) throws SeoException {

    }

    public void update(String keyword) throws SeoException {

    }
}
