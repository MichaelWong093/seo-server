package com.berchina.esb.server.provider.server;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import com.berchina.esb.server.provider.client.SeoRequest;
import com.berchina.esb.server.provider.client.base.Response;


/**
 * @Package com.berchina.esb.server.provider.server
 * @Description: TODO ( 搜索工厂 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/8/31 上午11:05
 * @Version V1.0
 */
@Service
public class SeoServerFactory {

    @Autowired
    private ApplicationContext context;

    public Response setSeoServer(SeoRequest request) {

        SeoServer seoServer = (SeoServer) context.getBean(request.getInstance());

        return seoServer.seoGoods(request);
    }
}
