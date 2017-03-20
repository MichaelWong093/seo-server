package com.berchina.seo.server.configloader.config.solr;

import com.berchina.seo.server.configloader.exception.SeoException;
import com.berchina.seo.server.configloader.exception.server.ServerException;
import com.berchina.seo.server.provider.utils.Constants;
import com.google.common.collect.Maps;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.util.Map;


/**
 * @Package com.berchina.seo.server.provider.server
 * @Description: TODO ( 创建 Solr 连接实例 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/9/5 下午3:04
 * @Version V1.0
 */

@Configuration
@EnableAutoConfiguration
public class SolrServerFactoryBean {

    private static final Logger LOGGER = LoggerFactory.getLogger(SolrServerFactoryBean.class);

    @Autowired
    private Environment environment;

    @Bean
    public Map<String, HttpSolrClient> httpSolrServer() {

        Map<String, HttpSolrClient> map = Maps.newHashMap();
        try {
            String[] args = this.getSorlConnection();

            for (int i = 0; i < args.length; i++) {

                map.put(args[i], new HttpSolrClient.Builder(getBaseSolrUrl().concat(args[i])).build());
            }
        } catch (Exception ex) {
            LOGGER.error("[ 搜索服务器地址不存在异常: {} ]", ex.getMessage());
            throw new SeoException(ex.getMessage(), ServerException.SEO_SERVER_URL_ERROR);
        }
        return map;
    }

    @Bean
    public String getBaseSolrUrl() {
        try {
            return this.environment.getRequiredProperty(Constants.PROPERTY_NAME_SOLR_SERVER_URL);
        } catch (Exception ex) {
            LOGGER.error("[ 系统获取搜索服务器地址异常: {} ]", ex.getMessage());
            throw new SeoException(ex.getMessage(), ServerException.SEO_REQUEST_PARAMTER);
        }
    }

    @Bean
    public String[] getSorlConnection() {
        try {
            return this.environment.getRequiredProperty(Constants.SEO_SYSTEM_REQUEST_ADAPTER).split(",");
        } catch (Exception ex) {
            LOGGER.error(" [ 系统获取搜索索引实例名称异常: {} ] ", ex.getMessage());
            throw new SeoException(ex.getMessage(), ServerException.SEO_REQUEST_PARAMTER);
        }
    }
}


