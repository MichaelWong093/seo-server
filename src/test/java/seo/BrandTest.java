package seo;

import com.alibaba.fastjson.JSON;
import com.berchina.esb.server.provider.client.SeoRequest;
import com.berchina.esb.server.provider.utils.SolrUtils;
import com.google.common.collect.Lists;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.List;

/**
 * Created by Administrator on 2016/10/19.
 */
public class BrandTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrandTest.class);

    @Test
    public void getBrand() throws IOException, SolrServerException {

        SeoRequest request = new SeoRequest();

        SolrQuery query = new SolrQuery();

        request.setBrand("249");

        HttpSolrClient  goods = new HttpSolrClient.Builder("http://127.0.0.1:8080/solr/goods").build();

        /**
         * 品牌搜索，根据品牌标识，找到相关类目
         */
        if (!StringUtils.isEmpty(request.getBrand())) {
            /**
             *  根据品牌聚合,展示商品属性，商品信息
             */
            query.set("q", "*:*");

            query.setFacet(true);

            query.addFacetField("vid");

            query.addFacetField("category");

            query.set("fq", SolrUtils.getQueryQ("brand", request.getBrand()));

        }

        QueryResponse response = goods.query(query);

        List<FacetField> facetFields = response.getFacetFields();


        List<FacetField.Count> attr =  facetFields.get(0).getValues();

        List<FacetField.Count> category =  facetFields.get(1).getValues();

        List list = Lists.newArrayList();

        for (int i =0 ; i < attr.size(); i++){

            FacetField.Count count = attr.get(i);

            if (count.getCount() > 0){
                list.add(count.getName());
            }
//            LOGGER.info(" 结果：{} "+ count.getCount());
//            LOGGER.info(" 结果：{} "+ count.getFacetField().getValues());
//            LOGGER.info(" 结果：{} "+ count.getName());
        }

        System.out.println(JSON.toJSON(list));



//            LOGGER.info(" 结果：{} "+ facetFields.get(i).getValues().get(i));
//            LOGGER.info(" 结果：{} "+ facetFields.get(i).getValues().get(i).getName());
//            LOGGER.info(" 结果：{} "+ facetFields.get(i).getValues().get(i).getCount());
    }
}
