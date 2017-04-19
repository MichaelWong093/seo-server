package com.seo.test.redis;

import com.alibaba.fastjson.JSON;
import com.berchina.seo.server.provider.model.SeoCateGory;
import com.berchina.seo.server.provider.server.CategoryServer;
import com.berchina.seo.server.provider.server.SearchServer;
import com.berchina.seo.server.provider.server.crud.CategoryRepository;
import com.berchina.seo.server.provider.utils.CateUtils;
import com.berchina.seo.server.provider.utils.Constants;
import com.berchina.seo.server.provider.utils.SolrUtils;
import com.berchina.seo.server.provider.utils.StringUtil;
import com.google.common.collect.Lists;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Set;

/**
 * @Package com.seo.test.redis
 * @Description: TODO (  )
 * @Author rxbyes
 * @Date 2017 上午11:44
 * @Version V1.0
 */
public class HttpSolrSearchTest {

    //    private static final Logger LOGGER = LoggerFactory.getLogger(HttpSolrSearchTest.class);
    public static final String SEO_CATEGORYS = "http://127.0.0.1:8983/solr/categorys";
    public static final String SEO_GOODS = "http://127.0.0.1:8983/solr/";
    public static final String SEO_CATEREV = "http://127.0.0.1:8983/solr/caterev";

    public static final String SOLR_HOME = "http://127.0.0.1:8983/solr/";

    private String categoryQuery;


    @Test
    public void search() throws IOException, SolrServerException {

//        IntStream is = IntStream.builder().add(10).add(20).add(-2).build();

//        is.filter(e -> e > 0).forEach(System.out::println);

        HttpSolrClient solrClient = new HttpSolrClient.Builder(SOLR_HOME).build();
//        String keywords = "黑加仑葡萄干500g 仅限网点自提";
//        SolrQuery query = new SolrQuery();
//        query.setQuery(keywords);
//        query.add(CommonParams.DF,"hotwords");
//        query.setStart(0);
//        query.setRows(100);
//        query.addField("hotwords");
//
//        QueryResponse response = solrClient.query("goods",query);
//
//        SolrDocumentList documents = response.getResults();
//
//        System.out.println(query.toQueryString());
//
//        for (SolrDocument doc : documents)
//        {
//            System.out.println(doc.get("hotwords"));
//        }
        setGoods(solrClient);
    }

    public void setGoods(HttpSolrClient solrClient) throws SolrServerException, IOException {


        SearchServer server = new SearchServer();
//        HttpSolrClient cateRevClient = new HttpSolrClient.Builder(SEO_CATEREV).build();
        String keywords = "牛肉面";
        ModifiableSolrParams params = new ModifiableSolrParams();
        SolrQuery query = new SolrQuery();
        query.setQuery(keywords);
        query.add(CommonParams.DF, "hotwords");

        query.add("defType", "synonym_edismax");
        query.add("synonyms", "true");

        query.addFacetField("category", "logistics");
        query.setFacet(true);
        query.setFacetLimit(10);
        params.add(query);

        System.out.println(params);

        QueryResponse goodsRsp = solrClient.query("goods", params);

        System.out.println("商品商量：" + goodsRsp.getResults().getNumFound());

        List<Object> goodsList = server.setGoodsDoc(goodsRsp.getResults());

        System.out.println("商品信息：" + JSON.toJSON(goodsList));


        List<FacetField> facetFields = goodsRsp.getFacetFields();

        query.clear();
        query.setQuery(Constants.COLON_ASTERISK);
        query.setFields("catname", "category");


//        CategoryServer categoryServer = new CategoryServer();

        CategoryRepository repository = new CategoryRepository();

        /**
         *  系统类目
         */
        List<String> category = CateUtils.setfacet(facetFields.get(0).getValues());

        if (StringUtil.notNull(category)) {
            System.out.println(JSON.toJSON(category));

            repository.category(category, query, "category");

            if (StringUtil.notNull(category)) {
                categoryQuery = query.get(CommonParams.FQ);
            }
            System.out.println("系统类目：" + query);
            QueryResponse categoryRsp = solrClient.query("category", query);
            SolrDocumentList categoryDoc = categoryRsp.getResults();

            List<Object> categories = repository.category(categoryDoc);
            System.out.println(JSON.toJSON(categories));
        }


        /**
         *  配送方式
         */
        List<String> logisticsFacet = CateUtils.setfacet(facetFields.get(1).getValues());

        if (StringUtil.notNull(logisticsFacet)) {
            System.out.println(JSON.toJSON(logisticsFacet));

            List logistics = CateUtils.setLogistics(logisticsFacet);

            System.out.println(JSON.toJSON(logistics));
        }

        /**
         *  品牌
         *
         *  {!join fromIndex=brand from=id to=brand}id:165
         *
         *  {!join fromIndex=category from=category to=category}category:2356
         *
         */
        params.clear();
        String fq = query.get(CommonParams.FQ);
        if (StringUtil.notNull(fq)) {
            query.remove(CommonParams.FQ);
            query.setFields("brand");
            query.setStart(0);
            query.setRows(1024);
            query.addFilterQuery("{!join fromIndex=category from=category to=category}".concat(fq));

            System.out.println(query);

            SolrDocumentList brvDocs = solrClient.query("brandrev", query).getResults();

            String brands = SolrUtils.setBrandQuery(brvDocs, "id", "brand");

            System.out.println(brands);

            query.remove(CommonParams.FQ);
            query.remove(CommonParams.FL);
            query.addFilterQuery(brands);
            query.setFields("id", "chineseName", "brandLogo");

            System.out.println(query);

            QueryResponse brdRsp = solrClient.query("brand", query);

            CategoryServer.Brand brand = new CategoryServer.Brand();

            System.out.println(JSON.toJSON(brand.brand(brdRsp.getResults())));
        }

        // 品牌与系统类目关联，搜索商品 (单个商品对应--> 单个类目 --> 单个类目关联 --> 多个品牌)
        // 由此推论得出： 一个商品有多个品牌问题，app会出现，品牌搜索有可能无商品的情况（暂无解决方案）
        // 假设搜索" 水果 " 返回三个品牌 其中有二个品牌是无商品的情况，如果商品齐全不会出现这个问题

        /**
         * 高级刷选
         */
        /**
         *  根据展示三级类目获取父级类目
         *
         *  一次行读取 二级类目、遍历查找父级类目下的叶子类目
         */
        if (StringUtil.notNull(categoryQuery))
        {
            query.clear();
            query.setQuery(Constants.COLON_ASTERISK);
            query.setFilterQueries("revlevel:1");
            query.setRows(1204);

            List<SeoCateGory> twoCategories = Lists.newLinkedList();

            SolrDocumentList categoryList = solrClient.query("categorys", query).getResults();

            for (SolrDocument doc : categoryList
                    ) {
                twoCategories.add(new SeoCateGory(StringUtil.StringConvert(doc.get("id")),
                        StringUtil.StringConvert(doc.get("revname"))));
            }
            System.out.println(JSON.toJSON(twoCategories));

            query.clear();
            query.setQuery(categoryQuery);
            query.setFields("revid", "category");
            query.setFacet(true);
            query.addFacetField("revid");
            query.setFacetLimit(20);
            query.setRows(1);
            System.out.println("系统类目与展示类目关联：" + query);
            List<FacetField> revList = solrClient.query("caterev", query).getFacetFields();
            List<String> revCategory = CateUtils.setfacet(revList.get(0).getValues());

            /**
             * 展示类目
             */
            query.clear();
            query.setQuery(Constants.COLON_ASTERISK);
            query.setFilterQueries("revlevel:2");
            query.setFields("revname", "parentid", "id", "revlevel");
            query.setRows(1024);
            repository.category(revCategory, query, "id");

            System.out.println("展示类目：" + query);

            SolrDocumentList threeCategories = solrClient.query("categorys", query).getResults();

            System.out.println("三级展示类目："+JSON.toJSON(threeCategories));

            Set<SeoCateGory> set = repository.getSeoCateGories(twoCategories, threeCategories);

            System.out.println(JSON.toJSON(set));
        }
    }
}
