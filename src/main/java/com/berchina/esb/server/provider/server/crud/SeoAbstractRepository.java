package com.berchina.esb.server.provider.server.crud;

import com.berchina.esb.server.configloader.config.SolrServerFactoryBean;
import com.berchina.esb.server.provider.utils.Constants;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @Package com.berchina.esb.server.provider.server.crud
 * @Description: TODO (  )
 * @Author 任小斌 renxiaobin
 * @Date 2016 下午3:04
 * @Version V1.0
 */
@Repository
public abstract class SeoAbstractRepository {

    /**
     * solr 实例资源工厂
     */
    @Autowired
    private SolrServerFactoryBean factoryBean;

    /**
     * Solr 实例资源池
     */
    public Map<String, HttpSolrClient> solrClient;

    public SolrQuery query = new SolrQuery();

    public HttpSolrClient goodsClient;

    public HttpSolrClient skuClient;

    public HttpSolrClient categorysClient;

    public HttpSolrClient categoryClient;

    public HttpSolrClient caterevClient;

    public HttpSolrClient brandClient;

    public HttpSolrClient bdrevClient;


    public void init() {

        solrClient = factoryBean.httpSolrServer();

        goodsClient = solrClient.get(Constants.SEO_GOODS_);

        bdrevClient = solrClient.get(Constants.SEO_BRANDREV_);

        brandClient = solrClient.get(Constants.SEO_BRAND);

        skuClient = solrClient.get(Constants.SEO_SKU);
    }

    public void InitCategory() {

        categorysClient = solrClient.get(Constants.SEO_CATEGORYS_SHOP_);

        caterevClient = solrClient.get(Constants.SEO_CATEREV_SHOP_);

        categoryClient = solrClient.get(Constants.SEO_CATEGORY_SHOP_);
    }

    public void InitShop() {


    }

    /**
     * 初始化 Solr 实例资源
     */
//    public SeoAbstractRepository() {
//
//        solrClient = factoryBean.httpSolrServer();
//
//        categorysClient = solrClient.get(Constants.SEO_CATEGORYS_SHOP_);
//
//
//
//        goodsClient = solrClient.get(Constants.SEO_GOODS_);
//
//
//
//
//
//
//
//
//
//    }
}
