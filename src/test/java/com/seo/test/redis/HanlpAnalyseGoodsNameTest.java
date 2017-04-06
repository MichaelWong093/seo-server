package com.seo.test.redis;

import com.google.common.collect.Sets;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.StreamingResponseCallback;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Set;

/**
 * @Package com.seo.test.redis
 * @Description: TODO (  )
 * @Author rxbyes
 * @Date 2017 上午9:47
 * @Version V1.0
 */
public class HanlpAnalyseGoodsNameTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(HanlpAnalyseGoodsNameTest.class);

    private HttpSolrClient solrClient = new HttpSolrClient.Builder("http://127.0.0.1:8983/solr/").build();

    public  Set<Object> analyse(String hotwords) {

        ModifiableSolrParams params = new ModifiableSolrParams();

        Set<Object> set = Sets.newConcurrentHashSet();

        StreamingResponseCallback callback = new StreamingResponseCallback() {

            @Override
            public void streamSolrDocument(SolrDocument doc) {

                set.add(doc.get("hotwords"));
            }

            @Override
            public void streamDocListInfo(long numFound, long start, Float maxScore) {

                System.out.println(numFound);
                System.out.println(start);
                System.out.println(maxScore);
            }
        };

        params.set("q", "hotwords:" + hotwords);
        params.set("start", "0");
        params.set("rows", "100");

        try {
            LOGGER.info("params request info: {}", params);

            QueryResponse goodsRsp = solrClient.queryAndStreamResponse("goods", params, callback);

            LOGGER.info("request times : {}", goodsRsp.getElapsedTime());
        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return set;
    }
}
