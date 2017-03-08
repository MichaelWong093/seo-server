package com.berchina.esb.server.provider.server.impl;

import java.io.IOException;
import java.util.Map;


import com.google.common.collect.Maps;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.berchina.esb.server.configloader.exception.SeoException;
import com.berchina.esb.server.configloader.exception.server.ServerException;
import com.berchina.esb.server.provider.client.SeoRequest;
import com.berchina.esb.server.provider.client.SeoResponse;
import com.berchina.esb.server.provider.server.SeoServer;
import com.berchina.esb.server.provider.server.crud.SeoHotWordRepository;

/**
 * @Package com.berchina.esb.server.provider.server.impl
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

    @Override
    public SeoResponse seoGoods(Object... args) {

        Map<String, Object> hotwords = Maps.newConcurrentMap();
        try {
            Object objects = args[0];
            if (!StringUtils.isEmpty(objects)) {
                if (objects instanceof SeoRequest) {
                    SeoRequest seoRequest = (SeoRequest) objects;

                    repository.setSeoResponseInfo(hotwords, seoRequest);

                    return new SeoResponse(hotwords, seoRequest);
                }
            }
        } catch (SolrServerException e) {
            LOGGER.error("[ 热词搜索异常 : {} ]", e.getMessage());
            throw new SeoException(e.getMessage(), ServerException.SEO_RESPONSE_HANDLE_ERROR);
        } catch (IOException e) {
            LOGGER.error("[ 热词搜索异常 : {} ]", e.getMessage());
            throw new SeoException(e.getMessage(), ServerException.SEO_RESPONSE_HANDLE_ERROR);
        }
        throw new SeoException(ServerException.SEO_RESPONSE_HANDLE_ERROR);
    }
}
