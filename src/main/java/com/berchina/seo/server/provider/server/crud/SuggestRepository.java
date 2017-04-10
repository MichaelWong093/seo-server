package com.berchina.seo.server.provider.server.crud;

import com.berchina.seo.server.configloader.config.solr.SolrServerFactoryBean;
import com.berchina.seo.server.provider.utils.Constants;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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

    private static final String COLLECTION_NAME = "sphotwd";

    public QueryResponse search(int start,int rows) throws IOException, SolrServerException {
        clearQuery();
        query.setQuery(Constants.ASTERISK);
        query.setStart(start);
        query.setRows(rows);
        params.add(query);
        return factoryBean.solrClient().query(this.COLLECTION_NAME,params);
    }

    public QueryResponse search(String keyword) throws IOException, SolrServerException {
        clearQuery();
        query.setQuery(keyword.concat(Constants.ASTERISK));
        query.add(CommonParams.DF, "suggest");
        query.setRows(10);
        params.add(query);
        return  factoryBean.solrClient().query(this.COLLECTION_NAME, params);
    }

    public UpdateResponse add(SolrInputDocument doc) throws IOException, SolrServerException {
        HttpSolrClient solrClient = factoryBean.solrClient();
        UpdateResponse response = solrClient.add(this.COLLECTION_NAME, doc);
        solrClient.commit(this.COLLECTION_NAME);
        return response;
    }

    public long isCheckKeyWordsBe(String keyword) throws SolrServerException, IOException {
        clearQuery();
        query.setQuery(keyword);
        query.add(CommonParams.DF, "keyword");
        params.add(query);
        return factoryBean.solrClient().query(this.COLLECTION_NAME, params).getResults().getNumFound();
    }

    public void clearQuery() {
        query.clear();
        params.clear();
    }

    public UpdateResponse delete(String id) throws IOException, SolrServerException {
        HttpSolrClient solrClient = factoryBean.solrClient();
        UpdateResponse response = solrClient.deleteById(this.COLLECTION_NAME,id);
        solrClient.commit(this.COLLECTION_NAME);
        return response;
    }
}
