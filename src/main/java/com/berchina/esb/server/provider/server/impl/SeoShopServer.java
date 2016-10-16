package com.berchina.esb.server.provider.server.impl;

import com.berchina.esb.server.configloader.exception.SeoException;
import com.berchina.esb.server.configloader.exception.server.ServerException;
import com.berchina.esb.server.provider.client.SeoRequest;
import com.berchina.esb.server.provider.client.SeoResponse;
import com.berchina.esb.server.provider.model.SeoShop;
import com.berchina.esb.server.provider.server.SeoServer;
import com.berchina.esb.server.provider.server.crud.SeoShopRepository;
import com.berchina.esb.server.provider.utils.EnumUtils;
import com.berchina.esb.server.provider.utils.SolrPageUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

/**
 * @Package com.berchina.esb.server.provider.server.impl
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

        /**
         * 店铺名称匹配搜索 
         * 优先 按商铺名称匹配搜索 商铺  商品
         * 再次 按商品名称匹配搜索 商铺，按商品创建时间降序
         */





        Map<String, Object> goods = Maps.newConcurrentMap();
//        try {
//            Object objects = args[0];
//            if (!StringUtils.isEmpty(objects)) {
//                if (objects instanceof SeoRequest) {
//                    SeoRequest seoRequest = (SeoRequest) objects;
//                    repository.seoGoodsRepository(goods, seoRequest);
//                    return new SeoResponse(goods, seoRequest);
//                }
//            }
//        } catch (SolrServerException | IOException ex) {
//            LOGGER.error("[ 搜索商品异常 : {} ]", ex.getMessage());
//            throw new SeoException(ex.getMessage(), ServerException.SEO_RESPONSE_HANDLE_ERROR);
//        }



       /* Map<String, SeoShop> seoShopMap = Maps.newHashMap();

        Map<String, Object> shop = Maps.newHashMap();

        SolrPageUtil page = new SolrPageUtil();
        try {
            Object objects = args[0];
            if (!StringUtils.isEmpty(objects)) {
                if (objects instanceof SeoRequest) {

                    SeoRequest request = (SeoRequest) objects;

                    page.setCurrentPage(Long.valueOf(request.getStart()));

                    page.setPageSize(Long.valueOf(request.getRows()));

                    if ("1".equals(request.getStart())) {//分页计算
                        request.setStart("0");
                    } else {
                        request.setStart(String.valueOf(Integer.valueOf(request.getStart()) * Integer.valueOf(request.getRows())));
                    }

                    QueryResponse response = repository.querySolrDocuments(request);

                    page.setTotalNum(response.getResults().getNumFound());

                    repository.setSeoShopMap(seoShopMap, response);

                    repository.setSeoGoodsMap(shop, seoShopMap, repository.querySolrDocuments(objects, EnumUtils.SEO_GOODS.getName()));

                    return new SeoResponse(shop, request);
                }
            }
        } catch (SolrServerException | IOException e) {
            LOGGER.error("[ 搜索商铺异常 : {} ]", e.getMessage());
            throw new SeoException(e.getMessage(), ServerException.SEO_RESPONSE_HANDLE_ERROR);
        }*/
        throw new SeoException(ServerException.SEO_RESPONSE_HANDLE_ERROR);
    }
}
