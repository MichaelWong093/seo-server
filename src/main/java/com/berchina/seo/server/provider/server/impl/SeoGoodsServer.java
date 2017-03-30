package com.berchina.seo.server.provider.server.impl;

import com.berchina.seo.server.configloader.exception.SeoException;
import com.berchina.seo.server.configloader.exception.server.ServerException;
import com.berchina.seo.server.provider.client.SeoRequest;
import com.berchina.seo.server.provider.client.SeoResponse;
import com.berchina.seo.server.provider.server.SeoServer;
import com.berchina.seo.server.provider.server.crud.SeoGoodsRepository;
import com.dianping.cat.Cat;
import com.google.common.collect.Maps;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.IOException;
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

//    private static final Logger LOGGER = LoggerFactory.getLogger(SeoGoodsServer.class);
    @Autowired
    private SeoGoodsRepository repository;

    public SeoResponse seoGoods(Object... args) {
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
                    repository.seoGoodsRepository(goods, seoRequest);
                    return new SeoResponse(goods, seoRequest);
                }
            }
        } catch (SolrServerException | IOException ex) {
            Cat.logError("[ 搜索商品异常:  ]", ex);
//            LOGGER.error("[  : {} ]", ex.getMessage());
//            throw new SeoException(ex.getMessage(), ServerException.SEO_RESPONSE_HANDLE_ERROR);
        }
        throw new SeoException(ServerException.SEO_RESPONSE_HANDLE_ERROR);
    }
}
