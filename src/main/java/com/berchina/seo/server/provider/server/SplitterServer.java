package com.berchina.seo.server.provider.server;

import com.berchina.seo.server.configloader.exception.SeoException;
import com.berchina.seo.server.provider.utils.StringUtil;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.IndexTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Map;

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

    public void deleteStop(String key) throws SeoException {

        Assert.notNull(key, "key is not empty");

        delHashOpsProperty("stop", key);

        CoreStopWordDictionary.remove(key);
    }

    public void deleteCustom(String key) throws SeoException {

        Assert.notNull(key, "key is not empty");

        delHashOpsProperty("custom", key);

        CustomDictionary.remove(key);
    }

    public void addStop(String key, String value) throws SeoException {

        setKVassert(key, value);

        setHashOpsProperty("stop", key, value);

        CoreStopWordDictionary.add(key);
    }

    public void addCustom(String key, String value) throws SeoException {

        setKVassert(key, value);

        setHashOpsProperty("custom", key, value);
        /**
         *  必须增加到缓存中，否则不会立即生效，除非重启服务器才会生效
         */
        CustomDictionary.add(key);
    }

    public List<Term> splitter(@RequestParam String keywords) throws SeoException {
        HanLP.Config.setRedisTemplate(redisTemplate);
        List<Term> lists = IndexTokenizer.segment(keywords);
        CoreStopWordDictionary.apply(lists);
        return lists;
    }

    public Map<String, String> put(@PathVariable String keys, @PathVariable String key) throws SeoException {

        setVaildata(keys, key);

        String val = getHashOperations().get(keys, key);

        val = StringUtil.notNull(val) ?
                key.concat(":").concat(Joiner.on(",").skipNulls().join(val.split("="))) : new String("404");

        return ImmutableMap.of("result", val);
    }

    private void setVaildata(@PathVariable String keys, @PathVariable String key) {
        Assert.notNull(keys, " keys is not empty, please input 'custom' or 'stop',think you!");
        Assert.notNull(key, " key is not empty, please input 'custom' or 'stop',think you!");
    }

    private void setKVassert(String key, String value) {
        Assert.notNull(value, " value is not empty, think you!");
        Assert.notNull(key, " key is not empty, think you!");
    }

    private void delHashOpsProperty(String keys, String key) {
        /**
         * 先删除redis，后删除缓存数据
         */
        getHashOperations().delete(keys, key);
    }

    /**
     * 获取redis 链接实例
     *
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
