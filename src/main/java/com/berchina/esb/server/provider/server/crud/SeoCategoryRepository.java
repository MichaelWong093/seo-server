package com.berchina.esb.server.provider.server.crud;

import com.alibaba.fastjson.JSON;
import com.berchina.esb.server.configloader.config.SolrServerFactoryBean;
import com.berchina.esb.server.provider.client.SeoRequest;
import com.berchina.esb.server.provider.model.SeoCateGory;
import com.berchina.esb.server.provider.model.SeoGoods;
import com.berchina.esb.server.provider.utils.CateUtils;
import com.berchina.esb.server.provider.utils.SolrPageUtil;
import com.berchina.esb.server.provider.utils.SolrUtils;
import com.google.common.collect.Lists;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Package com.berchina.esb.server.provider.server.crud
 * @Description: TODO ( 类目搜索类 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/9/9 上午11:13
 * @Version V1.0
 */
@Repository
public class SeoCategoryRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeoCategoryRepository.class);

    @Autowired
    private SolrServerFactoryBean factoryBean;

    public void setSeoCategoryResponseInfo(Map<String, Object> seoCateMap, SeoRequest request) throws SolrServerException, IOException {

        Map<String, HttpSolrClient> solrMap = factoryBean.httpSolrServer();

        SolrQuery query = new SolrQuery();

        /** 类目数据 */
        seoCateMap.put("category", getSolrCategorys(request, solrMap, query));

        /** 商品数据 */
        seoCateMap.put("goods", getSolrGoods(seoCateMap, solrMap, query, getSolrCate(request, solrMap, query), request));
    }

    private LinkedList<SeoGoods> getSolrGoods(Map<String, Object> seoCateMap,
                                              Map<String, HttpSolrClient> solrMap, SolrQuery query, List<String> catList, SeoRequest request)
            throws SolrServerException, IOException {

        HttpSolrClient solrGoods = solrMap.get("goods");
        /**
         * 商品信息
         */
        SolrUtils.query(catList, query, request);

        SolrDocumentList goodsDoc = solrGoods.query(query).getResults();

        SolrPageUtil.getPageInfo(seoCateMap, request, goodsDoc);

        SolrUtils.commit(solrGoods);

        LOGGER.info(" [ 系统类目关系商品信息, {} ] ", JSON.toJSONString(goodsDoc));

        return SolrUtils.setSeoGoodsResponseInfo(goodsDoc);
    }


    private List<String> getSolrCate(
            SeoRequest request, Map<String, HttpSolrClient> solrMap, SolrQuery query) throws SolrServerException, IOException {

        HttpSolrClient solrCate = solrMap.get("caterev");
        /**
         * 展示类目与系统类目关联关系
         */
        SolrUtils.queryCategorys(request, query);

        SolrDocumentList revDoc = solrCate.query(query).getResults();

        List<String> catList = Lists.newArrayList();

        catList.addAll(
                revDoc.stream().map(doc ->
                        SolrUtils.getSolrDocumentFiled(doc, "category"))
                        .collect(Collectors.toList()));

        LOGGER.info(" [ 展示类目与系统类目关联关系, {} ] ", JSON.toJSONString(catList));

        return catList;
    }

    private LinkedList<SeoCateGory> getSolrCategorys(
            SeoRequest request, Map<String, HttpSolrClient> solrMap, SolrQuery query) throws SolrServerException, IOException {

        HttpSolrClient solrCates = solrMap.get(request.getChannel());
        /**
         * 展示类目
         */
        SolrUtils.queryCategorys(query);

        SolrDocumentList cateGoryDoc = solrCates.query(query).getResults();

        LinkedList<SeoCateGory> seoCateGorys = CateUtils.getSeoCateGories(request.getCategory(), cateGoryDoc);

        SolrUtils.commit(solrCates);

        LOGGER.info(" [ 展示类目数据封装, {} ] ", JSON.toJSONString(seoCateGorys));

        return seoCateGorys;
    }
}
