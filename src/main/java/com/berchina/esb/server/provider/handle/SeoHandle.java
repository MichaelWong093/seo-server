package com.berchina.esb.server.provider.handle;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.berchina.esb.server.provider.client.SeoRequest;
import com.berchina.esb.server.provider.client.base.Response;
import com.berchina.esb.server.provider.server.SeoServerFactory;

/**
 * @Package com.berchina.esb.server.provider.handle
 * @Description: TODO ( SEO 服务入口 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/8/30 上午11:31
 * @Version V1.0
 */
@RestController
public class SeoHandle {

    @Autowired
    private SeoServerFactory factory;

    /**
     * 商品搜索
     *
     * @param request 关键字搜索channel: goods 商品搜索, hotwd 智能分词
     * @return 商品列表信息
     */
    @RequestMapping("/seo/{channel}")
    @ResponseBody
    ResponseEntity<Response> seoGoods(HttpServletRequest request) {

        return new ResponseEntity<>(factory.setSeoServer(new SeoRequest(request)), HttpStatus.OK);
    }
}
