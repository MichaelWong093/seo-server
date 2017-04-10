package com.seo.test.nio;

import com.alibaba.fastjson.JSON;
import com.berchina.seo.server.provider.utils.StringUtil;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * @Package com.seo.test.nio
 * @Description: TODO (  )
 * @Author rxbyes
 * @Date 2017 上午11:34
 * @Version V1.0
 */
public interface DefaultTest {

    static DefaultTest create(Supplier<DefaultTest> testSupplier) {
        return testSupplier.get();
    }

    default String hello() {
        return "hello liming";
    }

    String print();

    class DefaultTestImpl implements DefaultTest {

        @Override
        public String print() {
            return "ok";
        }
    }

    class DefaultTestImpls implements DefaultTest {
        @Override
        public String hello() {
            return "你好啊？";
        }

        @Override
        public String print() {
            return "are you ok!";
        }
    }

    static void main(String[] args) {

        String var1 = "brands : 165 : 正林 , brands : 2346 : 香辣, brands: 2134 : 食品";

        List<String> list = Splitter.on(",").omitEmptyStrings().omitEmptyStrings().splitToList(var1);

        Map<String,Object> maps = Maps.newHashMap();

        List<Map<String,String>> lists = Lists.newLinkedList();
        for (String attr : list)
        {
            List<String> tres = StringUtil.correlationToSplitter(":",attr);
            Map<String,String> map = Maps.newHashMap();
            for (int i = 0; i < 1; i++)
            {
                map.put("type",tres.get(0));
                map.put("id",tres.get(1));
                map.put("name",tres.get(2));
            }
            lists.add(map);
        }
        maps.put("corr",lists);

        System.out.println(JSON.toJSON(maps));

//        setSuggest(var1);
    }

    static void setSuggest(String var1) {
        //        { "corr":{ "brands0":{ "165":"正林" },"categories2":{ "2134":"食品" },"skus1":{ "2346":"香辣" } },"keyword":"牛肉干" },
        Map<String, Object> maps = Maps.newHashMap();
        List<String> list = Splitter.on(",").omitEmptyStrings().omitEmptyStrings().splitToList(var1);
        for (String var : list)
        {
            Map<String, String> map = Maps.newHashMap();
            List<String> corr = Splitter.on(":").omitEmptyStrings().omitEmptyStrings().splitToList(var);
            map.put(corr.get(1), corr.get(2));
            maps.put(corr.get(0), map);
        }
        System.out.println(JSON.toJSON(maps));
    }

    static void test1() {

        String var = "'brand':'正林','sku':'香辣','category':'食品'";

        List<String> correlation = Splitter.on(",").omitEmptyStrings().omitEmptyStrings().splitToList(var);

        Map<String, String> map = Maps.newHashMap();

        for (String str : correlation) {
            List<String> list = Splitter.on(":").omitEmptyStrings().omitEmptyStrings().splitToList(str);

            map.put(list.get(0), list.get(1));
        }
        System.out.println(JSON.toJSON(map));
    }

    static void tet() {
        DefaultTest defaultTest = DefaultTest.create(DefaultTestImpl::new);

        System.out.println(defaultTest.hello());

        DefaultTest test = DefaultTest.create(DefaultTestImpls::new);

        System.out.println(test.print());
    }
}
