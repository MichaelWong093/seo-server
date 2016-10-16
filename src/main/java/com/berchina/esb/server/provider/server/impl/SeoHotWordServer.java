package com.berchina.esb.server.provider.server.impl;

import java.io.IOException;
import java.util.LinkedList;

import javax.servlet.http.HttpServletRequest;

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
import com.berchina.esb.server.provider.client.SeoResponse;
import com.berchina.esb.server.provider.model.SeoHotWords;
import com.berchina.esb.server.provider.server.SeoServer;
import com.berchina.esb.server.provider.server.crud.SeoHotWordRepository;
import com.google.common.collect.Lists;

/**
 * 
 * @Package com.berchina.esb.server.provider.server.impl
 * @Description: TODO ( 热词联想)
 * @Author yanhuiqing
 * @Date  2016年9月9日 下午2:42:28
 * @Version V1.0
 */
@Service
public class SeoHotWordServer implements SeoServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeoHotWordServer.class);
    
    @Autowired
    private SeoHotWordRepository repository;
    
    @Override
    public HotWordResponse seoGoods(Object... args) {
    	
        LOGGER.info("=======   热词联想  ========");
        
        //第一步 保存关键字热词到 文件
        //第二部 从索引中获取搜索结果
        
        LinkedList<SeoHotWords> seoHotWordsList = Lists.newLinkedList();
        try {
            Object objects = args[0];
            if (!StringUtils.isEmpty(objects)) {
                if (objects instanceof SeoRequest) {
                    repository.setSeoResponseInfo(seoHotWordsList, repository.querySolrDocuments(objects),(SeoRequest)objects);
                }
            }
        } catch (SolrServerException | IOException e) {
            LOGGER.error("[ 热词搜索异常 : {} ]", e.getMessage());
            throw new SeoException(e.getMessage(), ServerException.SEO_RESPONSE_HANDLE_ERROR);
        }
        return new HotWordResponse(seoHotWordsList);
    }
}
