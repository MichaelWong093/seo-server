package com.berchina.seo.server.provider.server.impl;

import com.berchina.seo.server.configloader.config.logger.LoggerConfigure;
import com.berchina.seo.server.provider.client.SeoRequest;
import com.berchina.seo.server.provider.client.SeoResponse;
import com.berchina.seo.server.provider.server.SeoServer;
import com.berchina.seo.server.provider.server.crud.SeoGoodsRepository;
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
import java.lang.invoke.MethodHandles;
import java.util.Map;

/**
 * @Package com.berchina.seo.server.provider.server.impl
 * @Description: TODO ( 商品搜索 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/8/31 下午2:51
 * @Version V1.0
 */
@Service
public class SeoGoodsServer implements SeoServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    private SeoGoodsRepository repository;
    @Autowired
    private LoggerConfigure Logger;

    public SeoResponse seoGoods(Object... args) {

        Transaction t = Cat.newTransaction("SEO.Solr", "goods");
//        Cat.logMetricForCount("ReqCount");
        /**
         *  @ 热词搜索, 优先匹配自定义词库,是否进行分词操作,如果是自定义热词不进行拆分,否则相反
         *
         *  @ 根据热词搜索普通商品,搜索进行 facet 合并,统计热词搜索结果最多的商品,找到最多商品的所属类目
         *
         *  @ 根据所属商品类目搜索该类目下的所有商品
         *
         *  @ 待完善搜索规则过滤
         */
        Map<String, Object> goods = Maps.newConcurrentMap();
        try {
            Object objects = args[0];
            if (!StringUtils.isEmpty(objects)) {
                if (objects instanceof SeoRequest) {

                    SeoRequest seoRequest = (SeoRequest) objects;

                    if (Logger.info())
                    {
                        LOGGER.info("[ SEO goods request parameter： {} ]", seoRequest.toString());
                    } else {
                        Cat.logEvent("SOLR.Query", "goods", Transaction.SUCCESS, seoRequest.toString());
                    }
                    repository.seoGoodsRepository(goods, seoRequest);

                    SeoResponse response = new SeoResponse(goods, seoRequest);

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
