package seo.category.test;

import com.alibaba.fastjson.JSON;
import com.berchina.esb.server.provider.client.SeoRequest;
import com.berchina.esb.server.provider.model.SeoCateGory;
import com.berchina.esb.server.provider.utils.CateUtils;
import com.berchina.esb.server.provider.utils.Constants;
import com.berchina.esb.server.provider.utils.SolrUtils;
import com.berchina.esb.server.provider.utils.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Package seo.category.test
 * @Description: TODO ( 全站搜索 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/9/19 下午10:45
 * @Version V1.0
 */
public class SeoGoodsTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryTest.class);

    private HttpSolrClient categorys;
    private HttpSolrClient caterv;
    private HttpSolrClient category;
    private HttpSolrClient goods;
    private HttpSolrClient sku;

    private SolrQuery query = null;

    @Before
    public void init() {
        categorys = new HttpSolrClient.Builder("http://127.0.0.1:8983/solr/categorys").build();
        caterv = new HttpSolrClient.Builder("http://127.0.0.1:8983/solr/caterev").build();
        category = new HttpSolrClient.Builder("http://127.0.0.1:8983/solr/category").build();
        goods = new HttpSolrClient.Builder("http://127.0.0.1:8983/solr/goods").build();
        sku = new HttpSolrClient.Builder("http://127.0.0.1:8983/solr/sku").build();
    }


    @Test
    public void page() throws IOException, SolrServerException {

        query = new SolrQuery();

        SeoRequest request = new SeoRequest();

        request.setStart("1");

        request.setRows("10");

        request.setHotwords("商品");

        request.setSort("sales");

        request.setRule("desc");

        SolrUtils.query(request, query);

        QueryResponse response = goods.query(query);

        String category = response.getFacetFields().get(0).getValues().get(0).getName();

        SolrUtils.query(category, query);

        /**
         * 获取所有 catid 为 category sku info
         */
        SolrDocumentList docs = sku.query(query).getResults();

        /**
         * 基类属性
         */
        List<String> list = Lists.newLinkedList();

        list.addAll(docs.stream().map(doc ->
                SolrUtils.getSolrDocumentFiled(doc, "propid")).collect(Collectors.toList()));

//        SolrUtils.query(list, query);

        /**
         * 所有 sku 属性信息
         */
        SolrDocumentList skuDocs = sku.query(query).getResults();

        LinkedList<SeoCateGory> seoCateGories = Lists.newLinkedList();

        for (SolrDocument doc : docs) {

            String parentPropId = SolrUtils.getSolrDocumentFiled(doc, "propid");

            String parentCatId = SolrUtils.getSolrDocumentFiled(doc, "catid");

            SeoCateGory gory = new SeoCateGory();

            gory.setKey(parentPropId);

            gory.setValue(SolrUtils.getSolrDocumentFiled(doc, "proName"));

            LinkedList<SeoCateGory> cateGories = Lists.newLinkedList();

            for (SolrDocument sku : skuDocs) {

                String skuId = SolrUtils.getSolrDocumentFiled(sku, "propid");

                String catId = SolrUtils.getSolrDocumentFiled(sku, "catid");

                if (skuId.equals(parentPropId) && !catId.equals(parentCatId)) {

                    SeoCateGory cateGory = new SeoCateGory();

                    cateGory.setValue(SolrUtils.getSolrDocumentFiled(sku, "proName"));

                    cateGory.setKey(SolrUtils.getSolrDocumentFiled(sku, "catid"));

                    cateGories.add(cateGory);
                }
            }
            gory.setChilds(cateGories);
            seoCateGories.add(gory);
        }

        System.out.println(JSON.toJSONString(seoCateGories));
    }


    @After
    public void close() throws IOException {
        SolrUtils.commit(categorys);
        SolrUtils.commit(caterv);
        SolrUtils.commit(category);
        SolrUtils.commit(goods);
    }

}
