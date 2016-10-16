package seo.shop.test;

import com.berchina.esb.server.provider.client.SeoRequest;
import com.berchina.esb.server.provider.model.SeoShop;
import com.berchina.esb.server.provider.server.impl.SeoShopServer;
import com.berchina.esb.server.provider.utils.SolrUtils;
import com.google.common.collect.Lists;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrDocumentList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;

/**
 * @Package seo.shop.test
 * @Description: TODO ( )
 * @Author xum
 * @Date 2016/9/16 16:37
 * @Version V1.0
 */
public class TestSolrConnect {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestSolrConnect.class);

    private HttpSolrClient solrClient;

    private SolrQuery query = null;

    @Before
    public void init(){
        solrClient = new HttpSolrClient.Builder("http://127.0.0.1:8983/solr/shop").build();
    }

    @Test
    public void solrConnect() throws IOException,SolrServerException{
        query=new SolrQuery();
        SeoRequest seoRequest=new SeoRequest();
        seoRequest.setHotwords("测试");
//        SolrUtils.queryCategory(seoRequest, query);
        SolrDocumentList doc = solrClient.query(query).getResults();
        LinkedList<SeoShop> shopLinkedList = Lists.newLinkedList();
        for (int i = 0; i < doc.size(); i++) {
            SeoShop shop = new SeoShop();
            shop.setShopid(SolrUtils.getParameter(doc, i, "id"));
            shop.setShopName(SolrUtils.getParameter(doc, i, "shopName"));
            shopLinkedList.add(shop);
        }
        LOGGER.info(" [ 搜索商铺输出信息 ], {} ", shopLinkedList);
    }
    @After
    public void solrClean(){
        SolrUtils.commit(solrClient);
    }

}
