package com.seo.test.redis;

import com.alibaba.fastjson.JSON;
import com.berchina.seo.server.provider.model.SeoGoods;
import com.berchina.seo.server.provider.utils.StringUtil;
import com.google.common.collect.Lists;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.StreamingResponseCallback;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;

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

    public LinkedList<SeoGoods> analyse(String hotwords) {

        ModifiableSolrParams params = new ModifiableSolrParams();

        LinkedList<SeoGoods> seoGoods = Lists.newLinkedList();

        StreamingResponseCallback callback = new StreamingResponseCallback() {

            @Override
            public void streamSolrDocument(SolrDocument doc) {
                SeoGoods goods = new SeoGoods();

                goods.setHotwords(StringUtil.StringConvert(doc.get("hotwords")));
                goods.setShopName(StringUtil.StringConvert(doc.get("shopname")));

                seoGoods.add(goods);
            }

            @Override
            public void streamDocListInfo(long numFound, long start, Float maxScore) {

                System.out.println(numFound);
            }
        };

        SolrQuery query = new SolrQuery();
        // search q
        query.setQuery(hotwords);
        query.add(CommonParams.DF ,"hotwords");
        // fl
        query.setFields("hotwords", "shopname");
        //  start and rows
        query.setStart(0);
        query.setRows(20);
        // facet
        query.setFacet(true);
//        query.addFacetQuery("hotwords");
        query.setFacetPrefix(hotwords);
        query.addFacetField("hotwords");

        // highlight
        query.setHighlight(true);
        query.addHighlightField("hotwords");
        query.setHighlightSimplePre("<span style='color:red'>");
        query.setHighlightSimplePost("</span>");

        params.add(query);

        try {

            LOGGER.info("params request info: {}", params);

            QueryResponse goodsRsp = solrClient.queryAndStreamResponse("goods", params, callback);

            LOGGER.info(" response info : {}", JSON.toJSON(seoGoods));

            LOGGER.info(" response facet info : {}", goodsRsp.getFacetFields());

            LOGGER.info(" response highlight info : {}", JSON.toJSON(goodsRsp.getHighlighting()));

        } catch (SolrServerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return seoGoods;
    }
}
