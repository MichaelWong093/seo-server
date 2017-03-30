package com.berchina.seo.server.provider.server.impl;

import com.berchina.seo.server.configloader.config.logger.LoggerConfigure;
import com.berchina.seo.server.provider.client.SeoRequest;
import com.berchina.seo.server.provider.client.SeoResponse;
import com.berchina.seo.server.provider.server.SeoServer;
import com.berchina.seo.server.provider.server.crud.SeoHotWordRepository;
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
 * @Description: TODO ( 热词联想)
 * @Author yanhuiqing
 * @Date 2016年9月9日 下午2:42:28
 * @Version V1.0
 */
@Service
public class SeoHotWordServer implements SeoServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeoHotWordServer.class);

    @Autowired
    private SeoHotWordRepository repository;

    @Autowired
    private LoggerConfigure Logger;

    @Override
    public SeoResponse seoGoods(Object... args) {

        Transaction t = Cat.newTransaction("SEO.SOLR", "words");

        Map<String, Object> hotwords = Maps.newConcurrentMap();

        try {
            Object objects = args[0];
            if (!StringUtils.isEmpty(objects)) {
                if (objects instanceof SeoRequest) {

                    SeoRequest seoRequest = (SeoRequest) objects;

                    if (this.Logger.info()) {

                        LOGGER.info("[ SEO hotwords request parameter： {} ]", seoRequest.toString());
                    } else {
                        Cat.logEvent("SOLR.Query", "seoHotWords", Transaction.SUCCESS, seoRequest.toString());
                    }
                    repository.setSeoResponseInfo(hotwords, seoRequest);

                    SeoResponse response = new SeoResponse(hotwords, seoRequest);

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
