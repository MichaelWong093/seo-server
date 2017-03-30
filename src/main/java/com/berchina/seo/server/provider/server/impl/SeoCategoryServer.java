package com.berchina.seo.server.provider.server.impl;

import com.berchina.seo.server.configloader.config.logger.LoggerConfigure;
import com.berchina.seo.server.provider.client.SeoRequest;
import com.berchina.seo.server.provider.client.SeoResponse;
import com.berchina.seo.server.provider.server.SeoServer;
import com.berchina.seo.server.provider.server.crud.SeoCategoryRepository;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.google.common.collect.Maps;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Map;

/**
 * @Package com.berchina.seo.server.provider.server.impl
 * @Description: TODO ( 类目搜索 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/9/6 下午6:42
 * @Version V1.0
 */
@Service
public class SeoCategoryServer implements SeoServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeoCategoryServer.class);

    @Autowired
    private SeoCategoryRepository repository;

    @Autowired
    private LoggerConfigure Logger;

    @Override
    public SeoResponse seoGoods(Object... args) {

        Transaction t = Cat.newTransaction("SEO.SOLR", "category");
        /**
         * @ http://poshidi.com/java-create-jsontree  类目递归
         */
        Map<String, Object> category = Maps.newConcurrentMap();
        try {
            Object objects = args[0];
            if (!StringUtils.isEmpty(objects)) {

                if (objects instanceof SeoRequest) {

                    SeoRequest seoRequest = (SeoRequest) objects;

                    if (this.Logger.info()) {

                        LOGGER.info("[ SEO category request parameter： {} ]", seoRequest.toString());
                    } else {
                        Cat.logEvent("SOLR.Query", "category", Transaction.SUCCESS, seoRequest.toString());
                    }

                    this.repository.setSeoCategoryResponse(category, seoRequest);

                    SeoResponse response = new SeoResponse(category, seoRequest);

                    t.setStatus(Transaction.SUCCESS);

                    return response;
                }
            }
        } catch (SolrServerException | IOException ex) {
            t.setStatus(ex);
            Cat.logError(ex);
            if (this.Logger.info()) LOGGER.error(ex.getMessage());
        } finally {
            t.complete();
        }
        return null;
    }
}
