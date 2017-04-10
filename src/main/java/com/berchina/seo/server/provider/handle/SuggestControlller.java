package com.berchina.seo.server.provider.handle;

import com.berchina.seo.server.provider.server.SuggestServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

/**
 * @Package com.berchina.seo.server.provider.handle
 * @Description: GET (选择)：从服务器上获取一个具体的资源或者一个资源列表。
 * POST （创建）： 在服务器上创建一个新的资源。
 * PUT （更新）：以整体的方式更新服务器上的一个资源。
 * PATCH （更新）：只更新服务器上一个资源的一个属性。
 * DELETE （删除）：删除服务器上的一个资源。
 * @Author rxbyes
 * @Date 2017 下午4:45
 * @Version V1.0
 */
@RestController
@RequestMapping("/seo")
public class SuggestControlller {

    @Autowired
    private SuggestServer suggestServer;

    @RequestMapping(value = "/suggest/search/v2/{start}/{rows}", method = RequestMethod.GET)
    public ResponseEntity search(@PathVariable int start, @PathVariable int rows) throws IOException, SolrServerException {

        return new ResponseEntity(suggestServer.search(start, rows), HttpStatus.OK);
    }

    @RequestMapping(value = "/suggest/search/v2", method = RequestMethod.GET)
    public ResponseEntity search(@RequestParam String keyword) throws IOException, SolrServerException {

        return new ResponseEntity(suggestServer.search(keyword), HttpStatus.OK);
    }

    @RequestMapping(value = "/suggest/post/v2", method = RequestMethod.POST)
    public ResponseEntity add(@RequestParam String keyword, @RequestParam String correlation) throws IOException, SolrServerException {

        return new ResponseEntity(suggestServer.add(keyword, correlation), HttpStatus.OK);
    }

    @RequestMapping(value = "/suggest/del/v2/{id}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable String id) throws IOException, SolrServerException {

        return new ResponseEntity(suggestServer.delete(id), HttpStatus.OK);
    }

    @RequestMapping(value = "/suggest/put/v2", method = RequestMethod.PUT)
    public ResponseEntity update(@RequestParam String id, @RequestParam String keyword, @RequestParam String correlation) throws IOException, SolrServerException {

        return new ResponseEntity(suggestServer.update(id, keyword, correlation), HttpStatus.OK);
    }
}
