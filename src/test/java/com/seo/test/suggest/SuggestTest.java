package com.seo.test.suggest;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.py.Pinyin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.util.List;

/**
 * @Package com.seo.test.suggest
 * @Description: TODO (  )
 * @Author rxbyes
 * @Date 2017 下午5:44
 * @Version V1.0
 */
@SpringBootApplication
public class SuggestTest implements CommandLineRunner{

    private String keywords = "牛肉面";

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Override
    public void run(String... strings) throws Exception {

        HanLP.Config.setRedisTemplate(redisTemplate);

        List<Pinyin> pinyins = HanLP.convertToPinyinList(keywords);

        String py = HanLP.convertToPinyinFirstCharString(keywords, " ", true);

        System.out.println(pinyins);

        System.out.println(py);
    }

    public static void main(String[] args) {

        SpringApplication.run(SuggestTest.class,args).close();
    }
}
