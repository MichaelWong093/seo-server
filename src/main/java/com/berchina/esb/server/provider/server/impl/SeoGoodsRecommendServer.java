package com.berchina.esb.server.provider.server.impl;

import java.io.IOException;
import java.util.LinkedList;

import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.berchina.esb.server.configloader.exception.SeoException;
import com.berchina.esb.server.configloader.exception.server.ServerException;
import com.berchina.esb.server.provider.client.HotWordResponse;
import com.berchina.esb.server.provider.client.SeoRequest;
import com.berchina.esb.server.provider.model.SeoHotWords;
import com.berchina.esb.server.provider.server.SeoServer;
import com.berchina.esb.server.provider.server.crud.SeoHotWordRepository;
import com.google.common.collect.Lists;

@Service
public class SeoGoodsRecommendServer implements SeoServer{
	
    private static final Logger LOGGER = LoggerFactory.getLogger(SeoGoodsRecommendServer.class);
    
    @Autowired
    private SeoHotWordRepository repository;
    
	@Override
	public HotWordResponse seoGoods(Object... args) {
    	
        LOGGER.info("=======   商品热词推荐  ========");
        LinkedList<SeoHotWords> seoGoodsRecommendList = Lists.newLinkedList();
        try {
            Object objects = args[0];
            if (!StringUtils.isEmpty(objects)) {
                if (objects instanceof SeoRequest) {
                    repository.setSeoResponseInfo(seoGoodsRecommendList, repository.queryRecommentdDocuments(objects),(SeoRequest)objects);
                }
            }
        } catch (SolrServerException | IOException e) {
            LOGGER.error("[ 热词搜索异常 : {} ]", e.getMessage());
            throw new SeoException(e.getMessage(), ServerException.SEO_RESPONSE_HANDLE_ERROR);
        }
        return new HotWordResponse(seoGoodsRecommendList);
    }

}
