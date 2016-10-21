package seo.shop.test;

import com.alibaba.fastjson.JSON;
import com.berchina.esb.server.provider.client.SeoRequest;
import com.berchina.esb.server.provider.model.SeoGoods;
import com.berchina.esb.server.provider.model.SeoShop;
import com.berchina.esb.server.provider.utils.SolrUtils;
import com.berchina.esb.server.provider.utils.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
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
import java.util.List;
import java.util.Map;

public class TestSolrConnect {
    private static final Logger LOGGER = LoggerFactory.getLogger(TestSolrConnect.class);

    private HttpSolrClient solrClient;
    private HttpSolrClient goods;

    private SolrQuery query = null;

    @Before
    public void init() {
        solrClient = new HttpSolrClient.Builder("http://127.0.0.1:8983/solr/shop").build();
        goods = new HttpSolrClient.Builder("http://127.0.0.1:8983/solr/goods").build();
    }

    @Test
    public void solrConnect() throws IOException, SolrServerException {

        query = new SolrQuery();

        SeoRequest seoRequest = new SeoRequest();

        seoRequest.setHotwords("宝");

        SolrUtils.queryShop(seoRequest, query);

        List<SeoShop> shops = Lists.newLinkedList();

        getShopCollection(seoRequest, query, shops);

        /** 获取店铺集合 */

        StringBuilder builder = new StringBuilder();

        SolrUtils.setShopSolrQuery(shops, query, builder, "shopid");

        seoRequest.setAttribute(new String(builder));

        seoRequest.setOther("shop");

        SolrUtils.queryShop(seoRequest, query);

        SolrDocumentList goodses = goods.query(query).getResults();

        LinkedList<SeoGoods> vars = Lists.newLinkedList();

        SolrUtils.setSeoGoodsResponseInfo(vars, goodses);

        Map maps = Maps.newHashMap();

        maps.put("shop", shops);
        maps.put("goods", vars);


        System.out.println(JSON.toJSON(maps));


    }

    private void getShopCollection(SeoRequest request, SolrQuery query, List<SeoShop> shops) throws SolrServerException, IOException {
        SolrDocumentList doc = solrClient.query(query).getResults();
        for (int i = 0; i < doc.size(); i++) {
            SeoShop shop = new SeoShop();
            shop.setShopid(SolrUtils.getParameter(doc, i, "id"));
            shop.setShopName(SolrUtils.getParameter(doc, i, "hotwords"));
            shop.setLogo(SolrUtils.getParameter(doc, i, "logo"));
            shop.setAddress(SolrUtils.getParameter(doc, i, "address"));
            shop.setSource(SolrUtils.getParameter(doc, i, "source"));
            getShopByGoodsCollection(request, query, shop);
            shops.add(shop);
        }
        LOGGER.info(" [ 搜索商铺输出信息 ], {} ", JSON.toJSON(shops));
    }

    private void getShopByGoodsCollection(SeoRequest request, SolrQuery query, SeoShop shop) throws SolrServerException, IOException {
        /** 商品信息组装*/
        request.setAttribute(shop.getShopid());

        SolrUtils.queryShop(request, query);

        SolrDocumentList gdDoc = goods.query(query).getResults();

        LinkedList<SeoGoods> list = Lists.newLinkedList();

        SolrUtils.setSeoGoodsResponseInfo(list, gdDoc);

        shop.setGoodsList(list);
    }

    @After
    public void solrClean() {
        SolrUtils.commit(solrClient);
    }

}
