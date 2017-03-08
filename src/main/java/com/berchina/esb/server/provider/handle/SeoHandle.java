package com.berchina.esb.server.provider.handle;

import javax.servlet.http.HttpServletRequest;

import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/seo")
public class SeoHandle {

    @Autowired
    private SeoServerFactory factory;

    /**
     * 商品搜索
     *
     * @param request 关键字搜索channel: goods 商品搜索, hotwd 智能分词
     * @return 商品列表信息
     */
    @RequestMapping(value = "/{channel}")
    ResponseEntity<Response> seoGoods(HttpServletRequest request) {

        return new ResponseEntity<Response>(factory.setSeoServer(new SeoRequest(request)), HttpStatus.OK);
    }

    @ApiOperation(value = "搜索服务总入口", notes = "更多细节请联系开发人员")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "channel", value = "服务地址标示", required = true, dataType = "String"),
            @ApiImplicitParam(name = "hotwords", value = "搜索关键词", required = true, dataType = "String"),
            @ApiImplicitParam(name = "sort", value = "排序属性名称", required = true, dataType = "String"),
            @ApiImplicitParam(name = "rule", value = "排序规则，desc || asc", required = true, dataType = "String"),
            @ApiImplicitParam(name = "start", value = "页码", required = true, dataType = "String"),
            @ApiImplicitParam(name = "rows", value = "页数", required = true, dataType = "String")
    })
    @RequestMapping(value = "/gd", method = RequestMethod.POST)
    ResponseEntity<Response> getGoods(HttpServletRequest request) {

        return new ResponseEntity<Response>(new Response(), HttpStatus.OK);
    }
}
