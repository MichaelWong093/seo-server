package com.seo.server.category;

import com.alibaba.fastjson.JSON;
import com.berchina.esb.server.SeoApplication;
import com.berchina.esb.server.provider.client.SeoRequest;
import com.berchina.esb.server.provider.client.SeoResponse;
import com.berchina.esb.server.provider.server.impl.SeoCategoryServer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.rule.OutputCapture;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Map;

/**
 * @Package com.seo.server.category
 * @Description: TODO ( 类目搜索测试 )
 * @Author 任小斌 renxiaobin
 * @Date 2016 上午9:30
 * @Version V1.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = SeoApplication.class)
@WebAppConfiguration
//@WebIntegrationTest
public class CategoryServerTests {

    private static final Logger LOGGER = LoggerFactory.getLogger(CategoryServerTests.class);

    @Rule
    public OutputCapture capture = new OutputCapture();

    @Autowired
    public SeoCategoryServer seoCategoryServer;

    @Test
    public void category() {

        SeoRequest request = new SeoRequest();

        request.setChannel("3000");
        request.setTerminal("pc");
        request.setBrand("279");
        request.setRows("16");
        request.setStart("0");

        SeoResponse response = seoCategoryServer.seoGoods(request);

        Map<String, Object> goods = response.getSeoGoods();

        LOGGER.info(" 类目搜索输出信息, {}", JSON.toJSONString(goods.get("goods")));
    }
}
