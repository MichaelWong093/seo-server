package seo;

import com.alibaba.fastjson.JSON;
import com.berchina.esb.server.provider.model.SeoCateGory;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Package seo
 * @Description: TODO ( 商品搜索 )
 * @Author 任小斌 renxiaobin
 * @Date 2016 下午3:02
 * @Version V1.0
 */
public class GoodsTest {

    private final static Logger LOGGER = LoggerFactory.getLogger(GoodsTest.class);

    private HttpSolrClient goods;

    @Before
    public void init() {
        goods = new HttpSolrClient.Builder("http://127.0.0.1:8983/solr/goods").build();
    }

    @Test
    public void goods() {

        String var = "1:a,2:0000,1:b,3:99999";

        String[] args = var.split(",");

        List<SeoCateGory> lst = Lists.newLinkedList();

        Set<String> keies = Sets.newConcurrentHashSet();

        for (int i = 0; i < args.length; i++) {

            String[] key = args[i].split(":");
            for (int j = 0; j < key.length; j++) {
                SeoCateGory gory = new SeoCateGory();
                if (j % 2 == 0) {
                    gory.setKey(key[j]);
                    gory.setValue(key[j + 1]);
                    keies.add(gory.getKey());
                    lst.add(gory);
                }
            }
        }

        StringBuilder builder = new StringBuilder();

        Iterator<String> iterator = keies.iterator();

        while (iterator.hasNext()) {
            String key = iterator.next();

            for (SeoCateGory gory : lst) {

                if (key.equals(gory.getKey())) {

                    builder.append("vid:").append(gory.getValue()).append(" OR ");

                    System.out.println(gory.getValue());

                }
            }
        }

        System.out.println(new String(builder));

//        System.out.println(JSON.toJSONString(keies));

//        System.out.println(JSON.toJSONString(lst));
    }
}
