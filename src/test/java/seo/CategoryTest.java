package seo;

import com.alibaba.fastjson.JSON;
import com.berchina.esb.server.provider.client.SeoRequest;
import com.berchina.esb.server.provider.model.SeoCateGory;
import com.berchina.esb.server.provider.server.crud.SeoCategoryRepository;
import com.berchina.esb.server.provider.utils.CateUtils;
import com.berchina.esb.server.provider.utils.SolrUtils;
import com.google.common.collect.Lists;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Package seo.shop.test
 * @Description: TODO ( 类目搜索 )
 * @Author 任小斌 renxiaobin
 * @Date 2016 下午2:27
 * @Version V1.0
 */
public class CategoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrandTest.class);

    @Test
    public void getCategory() throws IOException, SolrServerException {

        HttpSolrClient categorys = new HttpSolrClient.Builder("http://127.0.0.1:8080/solr/categorys").build();

        SeoCategoryRepository repository = new SeoCategoryRepository();

        SeoRequest request = new SeoRequest();

        request.setCategory("1679");

        SolrQuery query = new SolrQuery();

        /**
         * 展示类目集合
         */
        LinkedList<SeoCateGory> cateGories = repository.getSolrCategorys(categorys, request, query);

        LOGGER.info("展示类目：{}", JSON.toJSONString(cateGories));

        /**
         * 展示类目下的叶子类目
         */
        LinkedList<String> gories = CateUtils.setEndCategoryQueryString(cateGories);

        LOGGER.info("三级类目：　{}", JSON.toJSONString(gories));


        /**
         * 展示类目与系统类目关联
         */
        StringBuilder builder = new StringBuilder();

        SolrUtils.setCollectSolrQuery(gories, query, builder, "revid");

        /**
         * 类目属性封装，统一处理
         */
        request.setCategory(new String(builder));

        LOGGER.info("展示类目的叶子类目ID：{}", request.getCategory());

        /**
         * 展示叶子类目与系统类目关联关系查询
         */

        HttpSolrClient caterevs = new HttpSolrClient.Builder("http://127.0.0.1:8080/solr/caterev").build();

        SolrUtils.queryCategorys(request, query);

        SolrDocumentList response = caterevs.query(query).getResults();

        List<String> cates = Lists.newArrayList();

        cates.addAll(
                response.stream().map(doc ->
                        SolrUtils.getSolrDocumentFiled(doc, "category"))
                        .collect(Collectors.toList()));

//        LOGGER.info("展示类目与系统类目关联关系：{}",JSON.toJSON(cates));

        HttpSolrClient category = new HttpSolrClient.Builder("http://127.0.0.1:8080/solr/category").build();

        StringBuilder bd = new StringBuilder();
        /**
         * 系统类目关联分装
         */
        SolrUtils.setCollectSolrQuery(cates, query, bd, "id");
        /**
         * 系统类目统一处理
         */
        request.setCategory(new String(bd));

        SolrUtils.queryCategorys(request, query);

        SolrDocumentList categories = category.query(query).getResults();

        List<String> gorys = Lists.newArrayList();

        gorys.addAll(
                categories.stream().map(doc ->
                        SolrUtils.getSolrDocumentFiled(doc, "id"))
                        .collect(Collectors.toList()));

        LOGGER.info("系统类目ID：{}", JSON.toJSONString(categories));

        HttpSolrClient goods = new HttpSolrClient.Builder("http://127.0.0.1:8080/solr/goods").build();

        StringBuilder gdBuilder = new StringBuilder();

        /**
         * 系统类目统一处理
         */
        request.setCategory(new String(gdBuilder));

        SolrUtils.query(gorys, query, request);

        SolrDocumentList goodses = goods.query(query).getResults();


        System.out.println(JSON.toJSONString(goodses));

        System.out.println(JSON.toJSONString(goodses.getNumFound()));

    }

}
