package com.berchina.seo.server.provider.server.crud;

import com.berchina.seo.server.configloader.config.solr.SolrServerFactoryBean;
import com.berchina.seo.server.provider.utils.Constants;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.Map;

/**
 * @Package com.berchina.seo.server.provider.server.crud
 * @Description: TODO ( 服务相关基类 )
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

    /**
     * 搜索查询字符串初始化
     */
    public SolrQuery query = new SolrQuery();

    /**
     * 商品服务
     */
    public HttpSolrClient goodsClient;

    /**
     * 商品SKU服务
     */
    public HttpSolrClient skuClient;

    /**
     * 展示类目服务
     */
    public HttpSolrClient categorysClient;

    /**
     * 系统类目服务
     */
    public HttpSolrClient categoryClient;

    /**
     * 展示类目与系统类目关联服务
     */
    public HttpSolrClient caterevClient;

    /**
     * 品牌服务
     */
    public HttpSolrClient brandClient;

    /**
     * 品牌与类目关联服务
     */
    public HttpSolrClient bdrevClient;

    /**
     * 店铺服务
     */
    public HttpSolrClient shopClient;

    /**
     * 商品相关 Solr 实例化
     */
    public void InitGoods() {
        this.setFactory();

        goodsClient = solrClient.get(Constants.SEO_GOODS_);

        bdrevClient = solrClient.get(Constants.SEO_BRANDREV_);

        brandClient = solrClient.get(Constants.SEO_BRAND);

        skuClient = solrClient.get(Constants.SEO_SKU);
    }

    /**
     * 初始化 SolrHttpClient 实例
     */
    private void setFactory() {
        solrClient = factoryBean.httpSolrServer();
    }

    /**
     * 类目相关 Solr 实例化
     */
    public void InitCategory() {
        this.setFactory();

        categorysClient = solrClient.get(Constants.SEO_CATEGORYS_SHOP_);

        caterevClient = solrClient.get(Constants.SEO_CATEREV_SHOP_);

        categoryClient = solrClient.get(Constants.SEO_CATEGORY_SHOP_);

        skuClient = solrClient.get(Constants.SEO_SKU);

        goodsClient = solrClient.get(Constants.SEO_GOODS_);
    }

    /**
     * 店铺相关 Solr 实例化
     */
    public void InitShop() {
        this.setFactory();

        goodsClient = solrClient.get(Constants.SEO_GOODS_);

        shopClient = solrClient.get(Constants.SEO_SHOP_);
    }
}
