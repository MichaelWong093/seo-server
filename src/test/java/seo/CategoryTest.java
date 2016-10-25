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
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.LinkedList;

/**
 * @Package seo.shop.test
 * @Description: TODO (  )
 * @Author 任小斌 renxiaobin
 * @Date 2016 下午2:27
 * @Version V1.0
 */
public class CategoryTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(BrandTest.class);

    @Test
    public void getCategory() throws IOException, SolrServerException {

        HttpSolrClient categorys = new HttpSolrClient.Builder("http://127.0.0.1:8983/solr/categorys").build();

        SeoCategoryRepository repository = new SeoCategoryRepository();

        SeoRequest request = new SeoRequest();

        request.setCategory("56");

        SolrQuery query = new SolrQuery();

        LinkedList<SeoCateGory> cateGories = repository.getSolrCategorys(categorys, request, query);

        LOGGER.info("展示类目，{}", JSON.toJSONString(cateGories));

        LinkedList<String> gories = CateUtils.setEndCategoryQueryString(cateGories);

        LOGGER.info("三级类目,{}", JSON.toJSONString(gories));

        StringBuilder builder = new StringBuilder();

        SolrUtils.setCollectSolrQuery(gories, query, builder, "revid");

        System.out.println(new String(builder));
    }


}
