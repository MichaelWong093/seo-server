package com.berchina.seo.server.provider.server;

import com.berchina.seo.server.provider.server.crud.BrandRepository;
import com.berchina.seo.server.provider.server.crud.CategoryRepository;
import com.berchina.seo.server.provider.utils.LogisticsEnum;
import com.berchina.seo.server.provider.utils.StringUtil;
import com.google.common.collect.Maps;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.assertj.core.util.Lists;
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

    @Autowired private CategoryRepository categoryRepository;

    @Autowired private BrandRepository brandRepository;
    private SolrQuery query = new SolrQuery();

    public Map<String ,Object> search(QueryResponse response) throws IOException, SolrServerException {

        Map<String ,Object> maps = Maps.newConcurrentMap();
        {
            maps.put("category",categoryRepository.category(
                    this.setfacet(
                            response.getFacetFields().get(0).getValues()),query)
            );
            maps.put("logistic",setLogistics(this.setfacet(response.getFacetFields().get(1).getValues())));

            maps.put("brand",new Brand().brand(brandRepository.brand(brandRepository.brand(query),query).getResults()));


        }
        return maps;
    }


    public List<String> setfacet(List<FacetField.Count> counts) {
        List<String> objects = Lists.newArrayList();
        if (StringUtil.notNull(counts))
        {
            for (FacetField.Count count : counts)
            {
                if (count.getCount() > 0)
                {
                    objects.add(count.getName());
                }
            }
        }
        return objects;
    }

    public List<Map<String,String>> setLogistics(List<String> logisticsFacet) {
        Map<String,String> map = Maps.newTreeMap();
        for (String log : logisticsFacet)
        {
            List<String> logistics = StringUtil.splitter(",",log);
            for (String var : logistics)
            {
                if (var.equals(LogisticsEnum.WDZT.getCode()))
                {
                    map.put(var,LogisticsEnum.WDZT.getName());
                }
                if (var.equals(LogisticsEnum.SHSM.getCode()))
                {
                    map.put(var,LogisticsEnum.SHSM.getName());
                }
                if(var.equals(LogisticsEnum.WLPS.getCode()))
                {
                    map.put(var,LogisticsEnum.WLPS.getName());
                }
            }
        }
        return Lists.newArrayList(map);
    }

    private static String BRAND_ID = "id";
    private static String BRAND_LOGO = "brandLogo";
    private static String BRAND_NAME = "chineseName";

    public static class Brand{

        // 品牌编号
        private String id;
        // 品牌名称
        private String name;
        // 品牌LOGO
        private String logo;

        public List<Brand> brand(SolrDocumentList documents)
        {
            List<Brand> brands = Lists.newArrayList();
            if (StringUtil.notNull(documents))
            {
                for (SolrDocument doc : documents)
                {
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


