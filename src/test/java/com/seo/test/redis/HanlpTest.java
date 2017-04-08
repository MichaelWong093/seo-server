package com.seo.test.redis;

import com.berchina.seo.server.provider.model.SeoGoods;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.tokenizer.IndexTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.LinkedList;

/**
 * @Package com.seo.test.redis
 * @Description: TODO (  )
 * @Author rxbyes
 * @Date 2017 下午5:54
 * @Version V1.0
 */

@SpringBootApplication
public class HanlpTest implements CommandLineRunner {

    private final static Logger LOGGER = LoggerFactory.getLogger(HanlpTest.class);

    @Autowired
    private StringRedisTemplate redisTemplate;

    public static void main(String[] args) {

        SpringApplication.run(HanlpTest.class, args).close();
    }

    @Override
    public void run(String... strings) throws Exception {

        hanlp();
    }

    String parameter = "Apple/苹果 iPhone 7 全网通4G手机";

    public void hanlp() {

        HanLP.Config.setRedisTemplate(redisTemplate);

//        HanLP.Config.enableDebug();

        HanlpAnalyseGoodsNameTest hanlpAnalyseGoods = new HanlpAnalyseGoodsNameTest();

//        HanLP.Config.enableDebug();

        System.out.println(IndexTokenizer.segment(parameter));

        System.out.println(HanLP.extractKeyword(parameter,6));

        LinkedList<SeoGoods> seoGoods  = hanlpAnalyseGoods.analyse(parameter);

//        LOGGER.warn("搜索结果：{}", JSON.toJSON(seoGoods));


//        for (Object obj : set
//                ) {

//            List keyWords = HanLP.extractKeyword((obj.toString()), 3);

//            System.out.println(JSON.toJSON(keyWords));
//        }
//        LOGGER.warn("搜索结果：{}", JSON.toJSON(set));
    }
}
