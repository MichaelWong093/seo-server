package com.berchina.seo.server.provider.server;

import com.alibaba.fastjson.JSON;
import com.berchina.seo.server.provider.client.SeoRequest;
import com.berchina.seo.server.provider.client.base.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.invoke.MethodHandles;


/**
 * @Package com.berchina.seo.server.provider.server
 * @Description: TODO ( 搜索工厂 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/8/31 上午11:05
 * @Version V1.0
 */
@Service
public class SeoServerFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());
    @Autowired
    private ApplicationContext context;

    public Response setSeoServer(SeoRequest request) {

        SeoServer seoServer = (SeoServer) context.getBean(request.getInstance());

        Response response = seoServer.seoGoods(request);

        LOGGER.info(" SEO 搜索 响应数据：{}", JSON.toJSONString(response));

        return response;
    }
}
