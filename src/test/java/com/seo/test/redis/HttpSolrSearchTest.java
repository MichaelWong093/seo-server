package com.seo.test.redis;

import com.alibaba.fastjson.JSON;
import com.berchina.seo.server.provider.model.SeoCateGory;
import com.berchina.seo.server.provider.server.CategoryServer;
import com.berchina.seo.server.provider.server.crud.CategoryRepository;
import com.berchina.seo.server.provider.utils.CateUtils;
import com.berchina.seo.server.provider.utils.Constants;
import com.berchina.seo.server.provider.utils.SolrUtils;
import com.berchina.seo.server.provider.utils.StringUtil;
import com.google.common.collect.Maps;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.CommonParams;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.Map;
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

    private Map<String,String> categoryQuery = Maps.newConcurrentMap();

    @Test
    public void search() throws IOException, SolrServerException {

        HttpSolrClient solrClient = new HttpSolrClient.Builder(SOLR_HOME).build();

        setGoods(solrClient);
    }

    public void setGoods(HttpSolrClient solrClient) throws SolrServerException, IOException {

        CategoryRepository repository = new CategoryRepository(solrClient);

        String keywords = "柠檬";
        ModifiableSolrParams params = new ModifiableSolrParams();
        SolrQuery query = new SolrQuery();

        QueryResponse goodsRsp = getGoodsQueryResponse(solrClient, keywords, params, query);

        System.out.println("商品商量：" + goodsRsp.getResults().getNumFound());

        List<FacetField> facetFields = getGoodsFacetFields(solrClient, repository, query, goodsRsp);


        /**
         *  配送方式
         */
        setLogisticsGacet(facetFields);
        /**
         *  品牌
         *
         *  {!join fromIndex=brand from=id to=brand}id:165
         *
         *  {!join fromIndex=category from=category to=category}category:2356
         *
         */
        setBrand(solrClient, params, query);

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
        setChange(repository, query);

    }

    public void setChange(CategoryRepository repository, SolrQuery query) throws IOException, SolrServerException {
        if (StringUtil.notNull(categoryQuery))
        {
            List<SeoCateGory> twoCategories = repository.twoCategories(query);

            System.out.println(JSON.toJSON(twoCategories));

            List<String> revCategory = repository.getCategoryRev(categoryQuery.get("categories"),query);

            System.out.println(revCategory);

            if (StringUtil.notNull(revCategory))
            {
                Set<SeoCateGory> set = repository.changeCategory(revCategory,query,"category");

                System.out.println(JSON.toJSON(set));
            }
        }
    }


    public void setBrand(HttpSolrClient solrClient, ModifiableSolrParams params, SolrQuery query) throws SolrServerException, IOException {
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
    }

    public void setLogisticsGacet(List<FacetField> facetFields) {
        List<String> logisticsFacet = CateUtils.setfacet(facetFields.get(1).getValues());

        if (StringUtil.notNull(logisticsFacet)) {
            System.out.println(JSON.toJSON(logisticsFacet));

            List logistics = CateUtils.setLogistics(logisticsFacet);

            System.out.println(JSON.toJSON(logistics));
        }
    }

    public List<FacetField> getGoodsFacetFields(HttpSolrClient solrClient, CategoryRepository repository, SolrQuery query, QueryResponse goodsRsp) throws SolrServerException, IOException {
        List<FacetField> facetFields = goodsRsp.getFacetFields();
        query.clear();
        query.setQuery(Constants.COLON_ASTERISK);
        query.setFields("catname", "category");
        /**
         *  系统类目
         */
        List<String> category = CateUtils.setfacet(facetFields.get(0).getValues());

        if (StringUtil.notNull(category)) {
            System.out.println(JSON.toJSON(category));

            repository.category(category, query, "category");

            if (StringUtil.notNull(category)) {

                categoryQuery.put("categories",query.get(CommonParams.FQ));
            }
            System.out.println("系统类目：" + query);
            QueryResponse categoryRsp = solrClient.query("category", query);
            SolrDocumentList categoryDoc = categoryRsp.getResults();

            List<Object> categories = repository.category(categoryDoc);
            System.out.println(JSON.toJSON(categories));
        }
        return facetFields;
    }

    public QueryResponse getGoodsQueryResponse(HttpSolrClient solrClient, String keywords, ModifiableSolrParams params, SolrQuery query) throws SolrServerException, IOException {
        query.setQuery(keywords);
        query.add(CommonParams.DF, "hotwords");

        query.add("defType", "synonym_edismax");
        query.add("synonyms", "true");

        query.addFacetField("category", "logistics");
        query.setFacet(true);
        query.setFacetLimit(10);
        params.add(query);

        System.out.println(params);

        return solrClient.query("goods", params);
    }
}
