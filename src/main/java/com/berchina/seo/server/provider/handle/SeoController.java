package com.berchina.seo.server.provider.handle;

import javax.servlet.http.HttpServletRequest;

import com.google.common.collect.Maps;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.IndexTokenizer;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.berchina.seo.server.provider.client.SeoRequest;
import com.berchina.seo.server.provider.client.base.Response;
import com.berchina.seo.server.provider.server.SeoServerFactory;

import java.util.List;
import java.util.Map;

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

        return new ResponseEntity<Response>(factory.setSeoServer(new SeoRequest(request)), HttpStatus.OK);
    }

    @ApiOperation(value = "热词人工维护入口", notes = "若有疑问咨询开发人员（rxbyes）")
    @ApiImplicitParams(
            {@ApiImplicitParam(
                    name = "hotword", value = "需要客户自动维护热词", required = true, dataType = "String")
            })
    @RequestMapping(value = "/goods/add/v1/", method = RequestMethod.GET, consumes = MediaType.APPLICATION_JSON_UTF8_VALUE)
    ResponseEntity<Object> add(@RequestParam(value = "hotword") String hotword) {

        System.out.println(hotword);

        Map<String, Object> map = Maps.newHashMap();

        map.put("name", hotword);

        return new ResponseEntity<Object>(map, HttpStatus.OK);
    }

//    @ApiOperation(value = "搜索服务总入口", notes = "更多细节请联系开发人员")
//    @ApiImplicitParams({
//            @ApiImplicitParam(name = "channel", value = "服务地址标示", required = true, dataType = "String"),
//            @ApiImplicitParam(name = "hotwords", value = "搜索关键词", required = true, dataType = "String"),
//            @ApiImplicitParam(name = "sort", value = "排序属性名称", required = true, dataType = "String"),
//            @ApiImplicitParam(name = "rule", value = "排序规则，desc || asc", required = true, dataType = "String"),
//            @ApiImplicitParam(name = "start", value = "页码", required = true, dataType = "String"),
//            @ApiImplicitParam(name = "rows", value = "页数", required = true, dataType = "String")
//    })
//    @RequestMapping(value = "/gd", method = RequestMethod.POST)
//    ResponseEntity<Response> getGoods(HttpServletRequest request) {
//
//        return new ResponseEntity<Response>(new Response(), HttpStatus.OK);
//    }
}
