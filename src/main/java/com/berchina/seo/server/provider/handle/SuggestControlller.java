package com.berchina.seo.server.provider.handle;

import com.berchina.seo.server.provider.server.SuggestServer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("seo")
public class SuggestControlller {

    @Autowired
    private SuggestServer suggestServer;

    @RequestMapping(value = "/suggest/search/v2/{keyword}", method = RequestMethod.GET)
    public ResponseEntity search(@PathVariable String keyword) {

        return new ResponseEntity(suggestServer.search(keyword), HttpStatus.OK);
    }

    @RequestMapping(value = "/suggest/post/v2", method = RequestMethod.POST)
    public ResponseEntity add(@RequestParam String keyword, @RequestParam String correlation) {

        return new ResponseEntity(suggestServer.add(keyword, correlation), HttpStatus.OK);
    }

    @RequestMapping(value = "/suggest/del/v2/{keyword}", method = RequestMethod.DELETE)
    public ResponseEntity delete(@PathVariable String keyword) {

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/suggest/put/v2/{keyword}", method = RequestMethod.PUT)
    public ResponseEntity update(@PathVariable String keyword) {

        return new ResponseEntity(HttpStatus.OK);
    }
}
