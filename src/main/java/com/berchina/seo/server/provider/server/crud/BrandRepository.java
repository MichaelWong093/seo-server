package com.berchina.seo.server.provider.server.crud;

import com.berchina.seo.server.configloader.config.solr.SolrServerFactoryBean;
import com.berchina.seo.server.provider.utils.Constants;
import com.berchina.seo.server.provider.utils.SolrUtils;
import com.berchina.seo.server.provider.utils.StringUtil;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.CommonParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.lang.invoke.MethodHandles;

/**
 * @Package com.berchina.seo.server.provider.server.crud
 * @Description: 品牌操作
 * @Author rxbyes
 * @Date 2017 下午2:39
 * @Version V1.0
 */
@Repository
public class BrandRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired
    private SolrServerFactoryBean bean;

    public String brand(SolrQuery query) throws IOException, SolrServerException {
        String fq = query.get(CommonParams.FQ);

        query.remove(CommonParams.FQ);
        query.setFields(this.BRAND);
        query.setStart(0);
        query.setRows(1024);
        query.addFilterQuery("{!join fromIndex=category from=category to=category}".concat(fq));

        LOGGER.warn("[ 后台类目关联品牌关系 Query 指令：{} ]", query.toQueryString());

       return SolrUtils.setBrandQuery(bean.solrClient().query(this.BRAND_REV,query).getResults(), BRAND_ID, BRAND);
    }

    public QueryResponse brand(String filterQuery, SolrQuery query) throws IOException, SolrServerException
    {
        if (StringUtil.notNull(filterQuery))
        {
            query.clear();
            query.setQuery(Constants.COLON_ASTERISK);
            if (StringUtil.notNull(filterQuery))
            {
                query.addFilterQuery(filterQuery);
            }
            query.setFields(this.BRAND_ID,this.CHINESE_NAME,this.BRAND_LOGE);

            LOGGER.warn("[ 品牌搜索 Query 指令：{} ]", query);
            return bean.solrClient().query(this.BRAND,query);
        }
       return new QueryResponse();
    }

    private final String BRAND_ID = "id";
    private final String BRAND = "brand";
    private final String BRAND_REV = "brandrev";
    private final String BRAND_LOGE = "brandLogo";
    private final String CHINESE_NAME = "chineseName";
}
