package com.berchina.seo.server.provider.server;

import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.IndexTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

/**
 * @Package com.berchina.seo.server.provider.server
 * @Description: TODO ( 分词服务)
 * @Author rxbyes
 * @Date 2017 下午10:38
 * @Version V1.0
 */
@Service
public class SplitterServer {

    @Autowired
    private StringRedisTemplate redisTemplate;

    public void deleteStop(String key){
        delHashOpsProperty("stop", key);
        CoreStopWordDictionary.remove(key);
    }

    public void deleteCustom(String key) {
        delHashOpsProperty("custom", key);
        CustomDictionary.remove(key);
    }
    public void addStop(String key,  String value) {
        setHashOpsProperty("stop", key, value);
        CoreStopWordDictionary.add(key);
    }

    public void addCustom(String key,  String value) {
        setHashOpsProperty("custom", key, value);
        /**
         *  必须增加到缓存中，否则不会立即生效，除非重启服务器才会生效
         */
        CustomDictionary.add(key);
    }

    public List<Term> splitter(@RequestParam String keywords) {
        HanLP.Config.setRedisTemplate(redisTemplate);
        List<Term> lists = IndexTokenizer.segment(keywords);
        CoreStopWordDictionary.apply(lists);
        return  lists;
    }

    public String put(@PathVariable String keys, @PathVariable String key) {
        /**
         *
         * 获取词库信息
         * */
        return getHashOperations().get(keys, key);
    }

    private void delHashOpsProperty(String keys, String key) {
        /**
         * 先删除redis，后删除缓存数据
         */
        getHashOperations().delete(keys, key);
    }

    /**
     * 获取redis 链接实例
     * @return
     */
    private HashOperations<String, String, String> getHashOperations() {
        return redisTemplate.opsForHash();
    }
    /**
     * 向分词库中动态添加数据
     *
     * @param keys  分词健
     * @param key   分词
     * @param value 词性 + 频次
     */
    private void setHashOpsProperty(String keys, String key, String value) {
        /**
         * 向自定义词典中增加分词
         */
        getHashOperations().put(keys, key, value);
    }
}
