package com.berchina.seo.server.provider.server.crud;

import com.berchina.seo.server.configloader.config.solr.SolrServerFactoryBean;
import com.berchina.seo.server.configloader.exception.SeoException;
import com.berchina.seo.server.provider.utils.Constants;
import com.berchina.seo.server.provider.utils.SerialNumber;
import com.berchina.seo.server.provider.utils.StringUtil;
import com.hankcs.hanlp.HanLP;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
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

    @Autowired
    private StringRedisTemplate redisTemplate;

    private ModifiableSolrParams params = new ModifiableSolrParams();

    private SolrQuery query = new SolrQuery();

    private static final String COLLECTION_NAME = "sphotwd";

    public QueryResponse search(String keyword) throws IOException, SolrServerException {

        HttpSolrClient solrClient = factoryBean.solrClient();

        query.setQuery(keyword.concat(Constants.ASTERISK));

        query.add(CommonParams.DF, "suggest");

        params.add(query);

        QueryResponse response = solrClient.query(COLLECTION_NAME, params);

        return response;
    }

    public boolean add(String keyword, String correlation) throws IOException, SolrServerException {
        HttpSolrClient solrClient = factoryBean.solrClient();
        SolrInputDocument doc = new SolrInputDocument();
        Assert.notNull("keyword is not empty ", keyword);

        if (isCheckKeyWordsBe(keyword, solrClient) == 0)
        {
            doc.addField("keyword", keyword);
            doc.addField("id", SerialNumber.getInstance().generaterNextNumber());
            doc.addField("frequency", 0);

            HanLP.Config.setRedisTemplate(redisTemplate);

            String pinyin = HanLP.convertToPinyinString(keyword, "", true);
            String py = HanLP.convertToPinyinFirstCharString(keyword, "", true);


            if (StringUtil.notNull(pinyin))
            {
                doc.addField("pinyin", pinyin);
            }
            if (StringUtil.notNull(py))
            {
                doc.addField("abbre", py);
            }
            if (StringUtil.notNull(correlation))
            {
                doc.addField("correlation", correlation);
            }
            UpdateResponse response = solrClient.add(this.COLLECTION_NAME, doc);

            solrClient.commit(this.COLLECTION_NAME);

            System.out.println(response.getStatus());
        }
        return true;
    }

    public long isCheckKeyWordsBe(String keyword, HttpSolrClient solrClient) throws SolrServerException, IOException {
        query.clear();
        params.clear();
        query.setQuery(keyword);
        query.add(CommonParams.DF, "keyword");
        params.add(query);
        return solrClient.query(COLLECTION_NAME, params).getResults().getNumFound();
    }

    public void delete(String keyword) throws SeoException {

    }

    public void update(String keyword) throws SeoException {

    }
}
