package com.berchina.seo.server.provider.handle;

import com.alibaba.fastjson.JSON;
import com.berchina.seo.server.provider.client.base.Response;
import com.berchina.seo.server.provider.server.SplitterServer;
import com.berchina.seo.server.provider.utils.SerialNumber;
import com.hankcs.hanlp.seg.common.Term;
import io.swagger.annotations.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * @Package com.berchina.seo.server.provider.handle
 * @Description: TODO (分词维护)
 * @Author rxbyes
 * @Date 2017 下午10:33
 * @Version V1.0
 */
@Api(value = "Seo - Splitter", description = "搜索分词控制器详情")
@RestController
@RequestMapping("/seo")
public class SplitterController {

    @Autowired
    private SplitterServer server;

    /**
     * 删除停词
     *
     * @param key 停词名称
     * @return
     */
    @ApiOperation(value = "删除停词", response = String.class,
            notes = "[ 删除停词，输入停词名称， eg：key: 牛肉面")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "key", value = "停词名称",
                            required = true, defaultValue = "牛肉面", paramType = "query", dataType = "String")
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 204, message = "服务器成功处理了请求，但没有返回任何内容"),
                    @ApiResponse(code = 401, message = "未授权客户机访问数据"),
                    @ApiResponse(code = 403, message = "服务器接受请求，但是被拒绝处理"),
            }
    )
    @RequestMapping(value = "/splitter/v2/stop", method = RequestMethod.DELETE)
    public ResponseEntity<Response<String>> deleteStop(@RequestParam(value = "key") String key) {

        server.deleteStop(key);

        return new ResponseEntity(
                new Response<String>(new Date().toString(), HttpStatus.OK.name(),
                        "ok", SerialNumber.getInstance().generaterNextNumber()), HttpStatus.OK);
    }


    /**
     * 删除自定义热词
     *
     * @param key 自定义热词
     * @return
     */
    @ApiOperation(value = "删除自定义热词", response = String.class,
            notes = "[ 删除自定义热词，输入热词名称 eg：key: 牛肉面")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "key", value = "停词名称",
                            required = true, defaultValue = "牛肉面", paramType = "query", dataType = "String")
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 204, message = "服务器成功处理了请求，但没有返回任何内容"),
                    @ApiResponse(code = 401, message = "未授权客户机访问数据"),
                    @ApiResponse(code = 403, message = "服务器接受请求，但是被拒绝处理"),
            }
    )
    @RequestMapping(value = "/splitter/v2/custom", method = RequestMethod.DELETE)
    public ResponseEntity<String> deleteCustom(@RequestParam(value = "key") String key) {

        server.deleteCustom(key);

        return new ResponseEntity(
                new Response<String>(new Date().toString(), HttpStatus.OK.name(),
                        "ok", SerialNumber.getInstance().generaterNextNumber()), HttpStatus.OK);
    }

    /**
     * 增加停词
     *
     * @param key   停词名称
     * @param value 停词名称
     * @return
     */
    @ApiOperation(value = "增加停词", response = String.class,
            notes = "[ 增加停词，停词键值名称相同， eg：key: 牛肉面, value: 牛肉面")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "key", value = "停词名称",
                            required = true, defaultValue = "牛肉面", paramType = "query", dataType = "String"),
                    @ApiImplicitParam(name = "value", value = "停词名称",
                            required = true, defaultValue = "牛肉面", paramType = "query", dataType = "String")
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "请求成功并且服务器创建了新的资源"),
                    @ApiResponse(code = 401, message = "未授权客户机访问数据"),
                    @ApiResponse(code = 403, message = "服务器接受请求，但是被拒绝处理"),
                    @ApiResponse(code = 404, message = "请求服务器资源不存在"),
            }
    )
    @RequestMapping(value = "/splitter/v2/stop", method = RequestMethod.PUT)
    public ResponseEntity<Response<String>> addStop(@RequestParam(value = "key") String key, @RequestParam(value = "value") String value) {

        server.addStop(key, value);

        return new ResponseEntity(
                new Response<String>(new Date().toString(), HttpStatus.OK.name(),
                        new String().concat(key).concat(" : ").concat(value), SerialNumber.getInstance().generaterNextNumber()), HttpStatus.OK);
    }


    /**
     * 自定义热词
     *
     * @param key   热词名称
     * @param value 热词值  nz=12 词性=词频
     * @return
     */
    @ApiOperation(value = "增加自定义热词", response = String.class,
            notes = "[ 增加自定义热词，键为关键词，值为词性等于词频，请注意输入格式， eg：key: 牛肉面, value: n=40")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "key", value = "热词名称",
                            required = true, defaultValue = "牛肉面", paramType = "query", dataType = "String"),
                    @ApiImplicitParam(name = "value", value = "词性与词频",
                            required = true, defaultValue = "n=40", paramType = "query", dataType = "String")
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 201, message = "请求成功并且服务器创建了新的资源"),
                    @ApiResponse(code = 401, message = "未授权客户机访问数据"),
                    @ApiResponse(code = 403, message = "服务器接受请求，但是被拒绝处理"),
                    @ApiResponse(code = 404, message = "请求服务器资源不存在"),
            }
    )
    @RequestMapping(value = "/splitter/v2/custom", method = RequestMethod.PUT)
    public ResponseEntity<StringBuilder> addCustom(@RequestParam(value = "key") String key, @RequestParam(value = "value") String value) {

        server.addCustom(key, value);

        return new ResponseEntity(
                new Response<String>(new Date().toString(), HttpStatus.OK.name(),
                        new String().concat(key).concat(" : ").concat(value), SerialNumber.getInstance().generaterNextNumber()), HttpStatus.OK);
    }

    /**
     * 分词接口
     *
     * @param keywords
     * @return
     */
    @ApiOperation(value = "热词分词接口", response = String.class,
            notes = "[ 该函数支持长尾词、商品名称、短语、等有效词分词，eg：牛肉面 —— 拆词：牛肉面、牛肉 ]")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "keywords", value = "关键词、如商品名称、短语等",
                            required = true, defaultValue = "牛肉面", paramType = "query", dataType = "String")
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "请求完成"),
                    @ApiResponse(code = 201, message = "请求成功并且服务器创建了新的资源"),
                    @ApiResponse(code = 400, message = "请求中有语法问题，或者不能满足请求"),
                    @ApiResponse(code = 401, message = "未授权客户机访问数据"),
                    @ApiResponse(code = 403, message = "服务器接受请求，但是被拒绝处理"),
                    @ApiResponse(code = 404, message = "请求服务器资源不存在"),
                    @ApiResponse(code = 500, message = "服务器不能完成请求")
            }
    )
    @RequestMapping(value = "/splitter/v2/", method = RequestMethod.POST)
    public ResponseEntity<Response<List<Term>>> splitter(@RequestParam String keywords) {

        Assert.notNull(keywords, " keywords is not empty");

        return new ResponseEntity(
                new Response<String>(new Date().toString(), HttpStatus.OK.name(),
                        JSON.toJSONString(server.splitter(keywords)), SerialNumber.getInstance().generaterNextNumber()), HttpStatus.OK);
    }


    /**
     * 查询热词、停词接口
     *
     * @param keys
     * @param key
     * @return
     */
    @ApiOperation(value = "查询热词、停词接口", response = String.class,
            notes = "[ 根据热词类型或者停词类型（custom、stop）、热词名称（工程师、其他）查询热词是否存在，成功返回热词信息，失败返回 404 ]")
    @ApiImplicitParams(
            {
                    @ApiImplicitParam(name = "keys", value = "热词类型或者停词类型",
                            required = true, defaultValue = "custom", paramType = "path", dataType = "String"),
                    @ApiImplicitParam(name = "key", value = "停词或者热词名称",
                            required = true, defaultValue = "工程师", paramType = "path", dataType = "String")
            }
    )
    @ApiResponses(
            value = {
                    @ApiResponse(code = 200, message = "请求完成"),
                    @ApiResponse(code = 201, message = "请求成功并且服务器创建了新的资源"),
                    @ApiResponse(code = 400, message = "请求中有语法问题，或者不能满足请求"),
                    @ApiResponse(code = 401, message = "未授权客户机访问数据"),
                    @ApiResponse(code = 403, message = "服务器接受请求，但是被拒绝处理"),
                    @ApiResponse(code = 404, message = "请求服务器资源不存在"),
                    @ApiResponse(code = 500, message = "服务器不能完成请求")
            }
    )
    @RequestMapping(value = "/splitter/v2/{keys}/{key}", method = RequestMethod.GET)
    public ResponseEntity<Map<String, String>> put(@PathVariable String keys, @PathVariable String key) {

        return new ResponseEntity(
                new Response<String>(new Date().toString(), HttpStatus.OK.name(),
                        JSON.toJSONString(server.put(keys, key)), SerialNumber.getInstance().generaterNextNumber()), HttpStatus.OK);
    }
}
