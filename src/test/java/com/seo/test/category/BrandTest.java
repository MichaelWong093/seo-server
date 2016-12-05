package com.seo.test.category;

import com.berchina.esb.server.provider.utils.SolrUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;

/**
 * @Package com.seo.server.category
 * @Description: TODO (  )
 * @Author 任小斌 renxiaobin
 * @Date 2016 上午11:32
 * @Version V1.0
 */
public class BrandTest {

    private final Logger logger = LoggerFactory.getLogger(BrandTest.class);

    private HttpSolrClient solrClient;

    private SolrQuery query = new SolrQuery();

    @Before
    public void b() {
        solrClient = new HttpSolrClient.Builder("http://127.0.0.1:8983/solr/goods").build();
    }

    @Test
    public void brand() throws IOException, SolrServerException {

        query.add("q", "*:*");

        query.setFilterQueries("brand:304");

        query.setFields("brand", "hotwords");

        logger.info("query command ,{}", query);

        QueryResponse response = solrClient.query(query);


        for (SolrDocument doc : response.getResults()
                ) {
            String hotwords = String.valueOf(doc.get("hotwords"));
            String brand = String.valueOf(doc.get("brand"));
        }

        Assert.assertEquals(" a = b", 403, response.getResults().getNumFound());

        logger.info(" document collection info , {}", Arrays.asList(
                response.getResults().get(9).get("brand"), response.getResults().get(9).get("hotwords")));

//        Assert.assertThat(Arrays.asList(
//                response.getResults().get(9).get("brand"), 2), Matchers.hasItems(304, 2));

//        Assert.assertThat(Arrays.asList(new String[]{"【满88元包邮】教父"}),Matchers.everyItem(Matchers.containsString("教父")));

//        assertThat("good", allOf(equalTo("good"), startsWith("good")));
//            assertThat("good", not(allOf(equalTo("bad"), equalTo("good"))));
//            assertThat("good", anyOf(equalTo("bad"), equalTo("good")));
//            assertThat(7, not(CombinableMatcher.<Integer> either(equalTo(3)).or(equalTo(4))));
//            assertThat(new Object(), not(sameInstance(new Object())));
    }

    @After
    public void a() throws IOException {

        SolrUtils.commitAndClose(solrClient);
    }
}
