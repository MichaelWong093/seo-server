package com.berchina.seo.server.provider.handle;

import com.berchina.seo.server.provider.client.SeoRequest;
import com.berchina.seo.server.provider.server.SearchServer;
import io.swagger.annotations.*;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
@Api(value = "seo - search", produces = "搜索引擎入口")
@RestController
@RequestMapping(value = "/seo/search")
public class SearchController {

    @Autowired
    private SearchServer search;

    @ApiOperation(value = "百合生活网全站搜索", response = String.class,
            notes = "[ 全站搜索入口 ]")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "hotwords", value = "商品搜索词",
                            required = true, defaultValue = "水果", paramType = "query", dataType = "String"),
                    @ApiImplicitParam(name = "type", value = "商品来源渠道：1.社区商超 2.生活服务 3.汽车金融 4. 跨境电商 5.实物类 7.牛肉面 8. 特产频道 9. 兰铁易行",
                            paramType = "query", dataType = "String"),
                    @ApiImplicitParam(name = "start", value = "页数，缺省是 1 ", defaultValue = "1", paramType = "query", dataType = "String"),
                    @ApiImplicitParam(name = "rows", value = "条数， 缺省是 30", defaultValue = "16", paramType = "query", dataType = "String")
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 401, message = "未授权客户机访问数据"),
                    @ApiResponse(code = 403, message = "服务器接受请求，但是被拒绝处理"),
                    @ApiResponse(code = 404, message = "请求服务器资源不存在")
            }
    )
    @RequestMapping(value = "/v2", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    ResponseEntity search(HttpServletRequest request) throws IOException, SolrServerException {

        return new ResponseEntity(search.search(new SeoRequest(request)), HttpStatus.OK);
    }

    @ApiOperation(value = "百合生活网筛选搜索", response = String.class,
            notes = "[ 全站刷选入口-只支持移动端 ]")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "category",
                            value = "商品所属类目 eg：category=2119,2143 ,2322,1019626,200000030", required = true,
                            defaultValue = "2119 ,2143,2322 ,1019626,200000030", paramType = "query", dataType = "String"),
                    @ApiImplicitParam(name = "brand",
                            value = "商品所属品牌 eg：brand=165,43,101,185", defaultValue = "165,43,101,185", paramType = "query", dataType = "String")
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 401, message = "未授权客户机访问数据"),
                    @ApiResponse(code = 403, message = "服务器接受请求，但是被拒绝处理"),
                    @ApiResponse(code = 404, message = "请求服务器资源不存在")
            }
    )
    @RequestMapping(value = "/change/v2", produces = MediaType.APPLICATION_JSON_UTF8_VALUE, method = RequestMethod.GET)
    ResponseEntity change(@RequestParam String category, @RequestParam String brand) throws IOException, SolrServerException {

        return new ResponseEntity(search.change(category, brand), HttpStatus.OK);
    }
}
