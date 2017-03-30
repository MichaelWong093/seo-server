package com.berchina.seo.server.provider.handle;

import com.berchina.seo.server.provider.client.SeoRequest;
import com.berchina.seo.server.provider.client.base.Response;
import com.berchina.seo.server.provider.server.SeoServerFactory;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * @Package com.berchina.seo.server.provider.handle
 * @Description: TODO ( SEO 服务入口 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/8/30 上午11:31
 * @Version V1.0
 */
@RestController
@RequestMapping("/seo")
public class SeoController {

    @Autowired
    private SeoServerFactory factory;

    @RequestMapping(value = "/{channel}")
    ResponseEntity<Response> seoGoods(HttpServletRequest request) {
        Transaction t = Cat.newTransaction("SEO", "Controller");
        ResponseEntity entity = null;
        try {
            entity = new ResponseEntity<>(factory.setSeoServer(new SeoRequest(request)), HttpStatus.OK);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception ex) {
            t.setStatus(ex);
            Cat.logError(ex);
        } finally {
            t.complete();
        }
        return entity;
    }
}
