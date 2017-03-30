package com.berchina.seo.server.provider.server.impl;

import com.berchina.seo.server.configloader.exception.SeoException;
import com.berchina.seo.server.configloader.exception.server.ServerException;
import com.berchina.seo.server.provider.client.SeoRequest;
import com.berchina.seo.server.provider.client.SeoResponse;
import com.berchina.seo.server.provider.server.SeoServer;
import com.berchina.seo.server.provider.server.crud.SeoShopRepository;
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
 * @Description: TODO (  商铺搜索 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/9/6 上午9:29
 * @Version V1.0
 */
@Service
public class SeoShopServer implements SeoServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeoShopServer.class);
    @Autowired
    private SeoShopRepository repository;

    @Override
    public SeoResponse seoGoods(Object... args) {

        Transaction t = Cat.newTransaction("SEO.Server", "SeoShopServer");

        Map<String, Object> shops = Maps.newConcurrentMap();
        try {
            Object objects = args[0];
            if (!StringUtils.isEmpty(objects)) {
                if (objects instanceof SeoRequest) {
                    SeoRequest seoRequest = (SeoRequest) objects;

                    Cat.logEvent("SELECT", "seoShop", Transaction.SUCCESS, seoRequest.toString());

                    repository.querySolrDocuments(shops, seoRequest);

                    SeoResponse response = new SeoResponse(shops, seoRequest);

                    t.setStatus(Transaction.SUCCESS);

                    return response;
                }
            }
        } catch (SolrServerException | IOException ex) {
            LOGGER.error(ex.getMessage());
            t.setStatus(ex);
            Cat.logError(ex);
//            throw new SeoException(ex.getMessage(), ServerException.SEO_RESPONSE_HANDLE_ERROR);
        }finally {
            t.complete();
        }
        throw new SeoException(ServerException.SEO_RESPONSE_HANDLE_ERROR);
    }
}
