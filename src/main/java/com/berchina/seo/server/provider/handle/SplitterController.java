package com.berchina.seo.server.provider.handle;

import com.berchina.seo.server.provider.server.SplitterServer;
import com.hankcs.hanlp.seg.common.Term;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @Package com.berchina.seo.server.provider.handle
 * @Description: TODO (分词手动维护)
 * @Author rxbyes
 * @Date 2017 下午10:33
 * @Version V1.0
 */
@RestController
@RequestMapping("/seo")
public class SplitterController {

    @Autowired
    private SplitterServer server;

    @RequestMapping(value = "/splitter/stop", method = RequestMethod.DELETE)
    public ResponseEntity deleteStop(@RequestParam(value = "key") String key) {

        server.deleteStop(key);

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/splitter/custom", method = RequestMethod.DELETE)
    public ResponseEntity deleteCustom(@RequestParam(value = "key") String key) {

        server.deleteCustom(key);

        return new ResponseEntity(HttpStatus.OK);
    }

    @RequestMapping(value = "/splitter/stop", method = RequestMethod.PUT)
    public ResponseEntity<StringBuilder> addStop(@RequestParam(value = "key") String key, @RequestParam(value = "value") String value) {

        server.addStop(key, value);

        return new ResponseEntity<>(new StringBuilder().append(key).append(" : ").append(value), HttpStatus.OK);
    }

    @RequestMapping(value = "/splitter/custom", method = RequestMethod.PUT)
    public ResponseEntity<StringBuilder> addCustom(@RequestParam(value = "key") String key, @RequestParam(value = "value") String value) {

        server.addCustom(key, value);

        return new ResponseEntity<>(new StringBuilder().append(key).append(" : ").append(value), HttpStatus.OK);
    }

    @RequestMapping(value = "/splitter", method = RequestMethod.POST)
    public List<Term> splitter(@RequestParam String keywords) {

        return server.splitter(keywords);
    }

    @RequestMapping(value = "/splitter/{keys}/{key}", method = RequestMethod.GET)
    public String put(@PathVariable String keys, @PathVariable String key) {

        return server.put(keys, key);
    }
}
