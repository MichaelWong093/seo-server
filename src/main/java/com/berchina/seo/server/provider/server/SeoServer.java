package com.berchina.seo.server.provider.server;

import com.berchina.seo.server.provider.client.base.Response;

/**
 * @Package com.berchina.seo.server.provider.server
 * @Description: TODO ( 搜索服务类 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/8/30 下午9:21
 * @Version V1.0
 */
public interface SeoServer {

    /**
     * 搜索商品,店铺信息
     *
     * @param args 关键字
     * @return 搜索商品信息
     */
    Response seoGoods(Object... args);

}
