package com.berchina.seo.server.provider.server;

import com.berchina.seo.server.provider.server.crud.BrandRepository;
import com.berchina.seo.server.provider.server.crud.CategoryRepository;
import com.berchina.seo.server.provider.utils.CateUtils;
import com.berchina.seo.server.provider.utils.StringUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.CommonParams;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Package com.berchina.seo.server.provider.server
 * @Description: TODO ( 类目、物流处理 )
 * @Author rxbyes
 * @Date 2017 下午9:59
 * @Version V1.0
 */
@Service
public class CategoryServer {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private BrandRepository brandRepository;

    private SolrQuery query = new SolrQuery();

    /**
     * 结果页全部分类
     *
     * @param category
     * @return
     */
    public List<Object> change(String category) throws IOException, SolrServerException {

        List<Object> list = Lists.newLinkedList();

        query.clear();

        /**
         *  商品类型
         */
        Map<String, Object> map_shop = Maps.newConcurrentMap();
        /**
         * 商品类型
         */
        map_shop.put("shop_key", "商品类型");
        /**
         * 商品分类值
         */
        map_shop.put("shop_value",
                ImmutableBiMap.of("gd_type_sm", "全部", "gd_type_gp", "全球购", "gd_type_igy", "积分")
        );

        list.add(map_shop);


        /**
         *  价格区间
         */
        Map<String, Object> map_prices = Maps.newConcurrentMap();
        /**
         *  价格区间
         */
        map_prices.put("prices_key", "价格区间");
        /**
         * 最高价、最低价
         */
        map_prices.put("prices_val",
                ImmutableBiMap.of("min_prices", "最低价", "max_prices", "最高价")
        );

        list.add(map_prices);

        /**
         *  配送方式
         */
        Map<String, Object> map_delivery = Maps.newConcurrentMap();
        /**
         * 配送方式
         */
        map_delivery.put("delivery_key", "配送方式");
        /**
         * 配送方式通过请求 key 即可
         */
        map_delivery.put("delivery_val",
                ImmutableBiMap.of("wlps_type", "物流配送", "hdwk_type", "货到付款", "smzt_type", "上门自提", "by_type", "包邮")
        );

        list.add(map_delivery);

        /**
         *  全部分类
         */
        Map<String, Object> map_category = Maps.newConcurrentMap();
        /**
         *  全部分类
         */
        map_category.put("category_key", "全部分类");

        map_category.put("category_val","1111");

        list.add(map_category);

        /**
         * 商品分类
         */
        Map<String, Object> map_cate = Maps.newConcurrentMap();
        /**
         * 分类键
         */
        map_cate.put("cate_key", "分类");

        categoryRepository.category(
                Splitter.on(",").trimResults().omitEmptyStrings().splitToList(category), query, "category");

        /**
         * 分类值
         */
        map_cate.put("cate_val", categoryRepository.changeCategory(categoryRepository.getCategoryRev(query.get(CommonParams.FQ), query), query, "category"));

        list.add(map_cate);


        /**
         * 商品品牌
         */
        Map<String, Object> map_brand = Maps.newConcurrentMap();
        /**
         * 商品品牌 key
         */
        map_brand.put("brand_key", "品牌");

        /**
         * 商品品牌 value
         */
        map_brand.put("brand_val", "4，5，6");

        list.add(map_brand);


        /**
         * 商品属性
         */
        Map<String, Object> map_attr = Maps.newConcurrentMap();
        /**
         * 商品属性 key
         */
        map_attr.put("attr_key", "产地");

        /**
         * 商品属性 value
         */
        map_attr.put("attr_val", "1，2，3");

        list.add(map_attr);

        return list;
    }

    public Map<String, Object> search(QueryResponse response) throws IOException, SolrServerException {

        Map<String, Object> maps = Maps.newConcurrentMap();
        {
            List<String> facet = CateUtils.setfacet(response.getFacetFields().get(0).getValues());

            maps.put("category", categoryRepository.category(facet, query));

            maps.put("categories", facet);

            maps.put("logistic", CateUtils.setLogistics(CateUtils.setfacet(response.getFacetFields().get(1).getValues())));

            maps.put("brand", new Brand().brand(brandRepository.brand(brandRepository.brand(query), query).getResults()));

        }
        return maps;
    }

    private static String BRAND_ID = "id";
    private static String BRAND_LOGO = "brandLogo";
    private static String BRAND_NAME = "chineseName";

    public static class Brand {
        // 品牌编号
        private String id;
        // 品牌名称
        private String name;
        // 品牌LOGO
        private String logo;

        public List<Brand> brand(SolrDocumentList documents) {
            List<Brand> brands = Lists.newArrayList();
            if (StringUtil.notNull(documents)) {
                for (SolrDocument doc : documents) {
                    Brand brand = new Brand();
                    brand.setId(StringUtil.StringConvert(doc.get(BRAND_ID)));
                    brand.setName(StringUtil.StringConvert(doc.get(BRAND_NAME)));
                    brand.setLogo(StringUtil.StringConvert(doc.get(BRAND_LOGO)));
                    brands.add(brand);
                }
            }
            return brands;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getLogo() {
            return logo;
        }

        public void setLogo(String logo) {
            this.logo = logo;
        }
    }
}


