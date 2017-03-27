package com.berchina.seo.server.provider.handle;

import com.berchina.seo.server.provider.server.impl.KeyWordsSplitterServer;
import com.google.common.collect.ImmutableBiMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Map;

/**
 * @Package com.berchina.seo.server.provider.handle
 * @Description: TODO ( 初始化词库 redis )
 * @Author rxbyes
 * @Date 2017 上午10:41
 * @Version V1.0
 */
@RestController
@RequestMapping("/seo")
public class KeyWordsController {

    @Autowired
    private KeyWordsSplitterServer spillterServer;

    @RequestMapping(value = "/keywords/{dictype}")
    public ResponseEntity<Map<String, String>> keyWords(@PathVariable(value = "dictype") String dictype) throws IOException {

        spillterServer.participle(dictype);

        return new ResponseEntity(ImmutableBiMap.of("result", "ok"), HttpStatus.OK);
    }
}
