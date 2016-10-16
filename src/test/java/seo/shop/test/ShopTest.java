package seo.shop.test;

import com.berchina.esb.server.provider.client.SeoRequest;
import com.berchina.esb.server.provider.client.base.Request;
import com.berchina.esb.server.provider.model.SeoShop;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import seo.category.test.CategoryTest;

/**
 * @Package seo.shop.test
 * @Description: TODO ( 店铺搜索测试,支持 PC, App 端搜索 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/10/14 上午11:09
 * @Version V1.0
 */
public class ShopTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryTest.class);

    private HttpSolrClient shop;

    private SolrQuery query = null;


    private SeoRequest request = null;

    @Before
    public void init() {
        shop = new HttpSolrClient.Builder("http://127.0.0.1:8983/solr/shop").build();

        request = new SeoRequest();

        request.setHotwords("");

    }

    @Test
    public void shop() {

        int i = 26;

        int b = 27;

        int c = b / i;

        if (b % i > 0) {
            c ++;
        }

        System.out.printf("c == " + c);


    }
}
