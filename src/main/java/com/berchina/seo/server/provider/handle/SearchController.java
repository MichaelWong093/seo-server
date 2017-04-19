package com.berchina.seo.server.provider.handle;

import com.berchina.seo.server.provider.client.SeoRequest;
import com.berchina.seo.server.provider.server.SearchServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @Package com.berchina.seo.server.provider.handle
 * @Description: TODO ( 商品搜索 )
 * @Author rxbyes
 * @Date 2017 下午7:51
 * @Version V1.0
 */
@RestController
@RequestMapping("/seo")
public class SearchController {

    @Autowired
    private SearchServer search;

    @RequestMapping(value = "/search/v2")
    ResponseEntity search(HttpServletRequest request) throws IOException, SolrServerException {

        return new ResponseEntity(search.search(new SeoRequest(request)), HttpStatus.OK);
    }

    @RequestMapping(value = "/search/change/v2")
    ResponseEntity change(@RequestParam String category) throws IOException, SolrServerException {

        return new ResponseEntity(search.change(category),HttpStatus.OK);
    }
}
