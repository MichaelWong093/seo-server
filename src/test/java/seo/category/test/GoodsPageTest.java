package seo.category.test;

import com.alibaba.fastjson.JSON;
import com.berchina.esb.server.provider.client.SeoRequest;
import com.berchina.esb.server.provider.model.SeoCateGory;
import com.berchina.esb.server.provider.server.crud.SeoGoodsRepository;
import com.berchina.esb.server.provider.utils.CateUtils;
import com.berchina.esb.server.provider.utils.SolrUtils;
import com.google.common.collect.*;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.*;

/**
 * @Package seo.category.test
 * @Description: TODO ( 全站搜索 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/9/19 下午7:54
 * @Version V1.0
 */
public class GoodsPageTest {


    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryTest.class);

    private HttpSolrClient brand;
    private HttpSolrClient brandrev;
    private HttpSolrClient goods;
    private HttpSolrClient sku;

    private SolrQuery query = null;

    private SeoRequest request = null;

    @Before
    public void init() {
        brand = new HttpSolrClient.Builder("http://127.0.0.1:8080/solr/brand").build();
        brandrev = new HttpSolrClient.Builder("http://127.0.0.1:8080/solr/brandrev").build();
        goods = new HttpSolrClient.Builder("http://127.0.0.1:8080/solr/goods").build();
        sku = new HttpSolrClient.Builder("http://127.0.0.1:8080/solr/sku").build();
    }

    @Test
    public void name() throws Exception {

    }

    @Test
    public void goods() throws IOException, SolrServerException {

        request = new SeoRequest();

        request.setHotwords("包邮");

        query = new SolrQuery();

        SolrUtils.query(request, query);

        QueryResponse response = goods.query(query);

        /**
         * 根据 搜索 关键词 匹配 搜索商品最多的 类目
         */
        String catId = response.getFacetFields().get(0).getValues().get(0).getName();

        /**
         * 获取 SKU 属性时 过滤 facet_fields propid 和 大于 0 的 propid
         */
        request.setCategory(catId);

        setCategory(request);
        /**
         *
         * 品牌信息 catID 搜索 商品品牌 200006277
         *
         */
        setBrand();

    }

    private void setBrand() throws SolrServerException, IOException {
        SolrUtils.queryBrandRev(request, query);

        SolrDocumentList brRev = brandrev.query(query).getResults();

        LOGGER.info(" 类目对应的商品品牌 , {}", JSON.toJSON(brRev));

        StringBuilder builder = new StringBuilder();

        SolrUtils.setCollectSolrQuery(SolrUtils.getBrandIDCollection(brRev), query, builder, "id");

        SolrDocumentList brandsDoc = brand.query(query).getResults();

        LOGGER.info(" 品牌信息 , {}", JSON.toJSON(brandsDoc));
    }


    private void setCategory(SeoRequest request) throws SolrServerException, IOException {

        SolrUtils.queryCategoryKey(request, query);

        LinkedList<SeoCateGory> seoSku = Lists.newLinkedList();

        SolrUtils.setSku(seoSku, sku.query(query).getResults());

        Set<SeoCateGory> skuK = Sets.newHashSet();

        skuK.addAll(seoSku);

        LOGGER.info(" 商品属性 KEY , {}", JSON.toJSON(skuK));

        /**
         *
         * SKU Value 搜索, 根据 (propid  or propid ) and catid 搜索
         *
         */
        StringBuilder builder = new StringBuilder();

        SolrUtils.setCollectSolrQuery(skuK, query, builder, "propid", request.getCategory());

        request.setAttribute(new String(builder));

        SolrUtils.queryCategoryValue(request, query);

        SolrDocumentList skuV = sku.query(query).getResults();

        /**
         *
         * @ 类目属性 KEY 对应 类目属性 Value 合并  @skus  《 - 》 @ skuV
         *
         */
        List<Map<String, Object>> categorys = CateUtils.getCategoryCollection(skuK, skuV);

        SolrUtils.queryParameter(request, query);

        SolrDocumentList goodsDoc = goods.query(query).getResults();


        System.out.println(JSON.toJSON(categorys));

        System.out.println("商品信息：" + JSON.toJSONString(goodsDoc));
    }


}
