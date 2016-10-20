package seo;

import com.berchina.esb.server.provider.client.SeoRequest;
import com.berchina.esb.server.provider.utils.SolrUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Package seo.shop.test
 * @Description: TODO (  )
 * @Author 任小斌 renxiaobin
 * @Date 2016 下午2:27
 * @Version V1.0
 */
public class CategoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrandTest.class);

    @Test
    public void getCategory(){

        HttpSolrClient goods = new HttpSolrClient.Builder("http://127.0.0.1:8983/solr/goods").build();

        SeoRequest request = new SeoRequest();

        request.setCategory("200005588");

        SolrQuery query = new SolrQuery();

        query.set("q", "*:*");

        query.setFacet(true);

        query.addFacetField("category");

        query.set("fq", SolrUtils.getQueryQ("category", request.getBrand()));

    }
}
