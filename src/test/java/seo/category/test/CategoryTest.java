package seo.category.test;

import com.alibaba.fastjson.JSON;
import com.berchina.esb.server.provider.client.SeoRequest;
import com.berchina.esb.server.provider.model.SeoCateGory;
import com.berchina.esb.server.provider.utils.CateUtils;
import com.berchina.esb.server.provider.utils.SolrUtils;
import com.google.common.collect.Lists;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @Package seo.category.test
 * @Description: TODO ( 类目测试 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/9/16 下午3:46
 * @Version V1.0
 */

public class CategoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryTest.class);

    private HttpSolrClient categorys;
    private HttpSolrClient caterv;
    private HttpSolrClient category;
    private HttpSolrClient goods;

    private SolrQuery query = null;

    @Before
    public void init() {
        categorys = new HttpSolrClient.Builder("http://127.0.0.1:8983/solr/categorys").build();
        caterv = new HttpSolrClient.Builder("http://127.0.0.1:8983/solr/caterev").build();
        category = new HttpSolrClient.Builder("http://127.0.0.1:8983/solr/category").build();
        goods = new HttpSolrClient.Builder("http://127.0.0.1:8983/solr/goods").build();
    }



    public void page() throws IOException, SolrServerException {

        query = new SolrQuery();

        List<String> querys = Lists.newArrayList();

        SeoRequest request = new SeoRequest();

        // 当前页
        request.setStart("2");

        // 页面显示条数
        request.setRows("10");

//        request.setCategory("4922");

        SolrUtils.query(querys, query, request);

        SolrDocumentList goodsDoc = goods.query(query).getResults();

        for (SolrDocument doc : goodsDoc
             ) {
            System.out.println("==================");

            System.out.println(doc.get("hotwords"));
            System.out.println(doc.get("id"));

            System.out.println("==================");
        }

        System.out.println(JSON.toJSONString(goodsDoc));

    }


    @Test
    public void category() throws IOException, SolrServerException {

        query = new SolrQuery();

        SeoRequest request = new SeoRequest();

        request.setCategory("5186");

        SolrUtils.queryCategorys(request, query);

        /**
         * 展示类目
         */
        SolrDocumentList allCategories = categorys.query(query).getResults();

//        System.out.println( "类目条数 "+  allCategories.getNumFound() +" 所有类目信息 " +JSON.toJSON(allCategories));


        LinkedList<SeoCateGory> goryLinkedList = CateUtils.getSeoCateGories(request.getCategory(), allCategories);

        System.out.println("展示类目："+ JSON.toJSONString(goryLinkedList));



//        System.out.println(JSON.toJSON(goryLinkedList));

//        SolrUtils.query(request, query);

        /**
         * 展示类目与系统类目关联关系
         */
//        SolrDocumentList revDoc = caterv.query(query).getResults();
//
//        List<String> catList = Lists.newArrayList();
//
//        catList.addAll(
//                revDoc.stream().map(doc ->
//                        SolrUtils.getSolrDocumentFiled(doc, "catid"))
//                        .collect(Collectors.toList()));
//        /**
//         * 系统类目
//         */
//        SolrUtils.query(catList, query, request);
//
//        SolrDocumentList goryList = goods.query(query).getResults();
//
//        System.out.println(JSON.toJSONString(goryList));

    }


    @After
    public void close() throws IOException {
        SolrUtils.commit(categorys);
        SolrUtils.commit(caterv);
        SolrUtils.commit(category);
        SolrUtils.commit(goods);
    }
}
