package com.seo.server.category;

import com.berchina.esb.server.provider.client.SeoRequest;
import com.berchina.esb.server.provider.utils.SolrUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrDocumentList;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import java.io.IOException;

/**
 * @Package com.seo.server.category
 * @Description: TODO ( 展示类目递归，验证搜索类目是否是叶子类目 )
 * @Author 任小斌 renxiaobin
 * @Date 2016 下午4:18
 * @Version V1.0
 */
public class CategoryLeveTests {

    private HttpSolrClient solrClient;

    private SolrQuery query = new SolrQuery();

    @Before
    public void before() {
        solrClient = new HttpSolrClient.Builder("http://127.0.0.1:8983/solr/categorys").build();
    }

    @Test
    public void category() throws IOException, SolrServerException {

        // 类目编号
        String parentId = "4922";

        SeoRequest request = new SeoRequest();

        request.setCategory(parentId);

        query.set("q", "*:*");
        query.set("fl", "id");

        query.set("fq", SolrUtils.getQueryQ("parentid", request.getCategory()));

        SolrDocumentList leves = solrClient.query(query).getResults();

        Assert.assertArrayEquals(new int[0], new int[leves.size()]);

        Assert.assertNotNull(" 类目搜索为三级类目 ", leves);

    }
}
