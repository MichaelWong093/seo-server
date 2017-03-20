package com.berchina.seo.server.provider.server.impl;

import com.berchina.seo.server.configloader.exception.SeoException;
import com.berchina.seo.server.configloader.exception.server.ServerException;
import com.berchina.seo.server.provider.client.SeoRequest;
import com.berchina.seo.server.provider.client.SeoResponse;
import com.berchina.seo.server.provider.server.SeoServer;
import com.berchina.seo.server.provider.server.crud.SeoShopRepository;
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

        Map<String, Object> shops = Maps.newConcurrentMap();
        try {
            Object objects = args[0];
            if (!StringUtils.isEmpty(objects)) {
                if (objects instanceof SeoRequest) {
                    SeoRequest seoRequest = (SeoRequest) objects;

                    repository.querySolrDocuments(shops, seoRequest);

                    return new SeoResponse(shops, seoRequest);
                }
            }
        } catch (SolrServerException | IOException ex) {
            LOGGER.error("[ 搜索商品异常 : {} ]", ex.getMessage());
            throw new SeoException(ex.getMessage(), ServerException.SEO_RESPONSE_HANDLE_ERROR);
        }

        throw new SeoException(ServerException.SEO_RESPONSE_HANDLE_ERROR);
    }
}
