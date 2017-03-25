package com.seo.test.redis;

import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.IndexTokenizer;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.List;

/**
 * @Package com.seo.test.redis
 * @Description: TODO (  )
 * @Author rxbyes
 * @Date 2017 下午5:54
 * @Version V1.0
 */

@SpringBootApplication
public class HanlpTest implements CommandLineRunner {

    public static void main(String[] args) {

        SpringApplication.run(HanlpTest.class,args).close();
    }

    public void hanlp(){

//        HanLP.Config.enableDebug();
        List<Term> termList = IndexTokenizer.segment("棒棒糖");

        CoreStopWordDictionary.apply(termList);

        System.out.println(termList);
    }

    @Override
    public void run(String... strings) throws Exception {

//        hanlp();
    }
}
