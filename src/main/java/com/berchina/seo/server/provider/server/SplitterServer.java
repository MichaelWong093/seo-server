package com.berchina.seo.server.provider.server;

import com.berchina.seo.server.configloader.config.logger.LoggerConfigure;
import com.berchina.seo.server.provider.utils.StringUtil;
import com.dianping.cat.Cat;
import com.dianping.cat.message.Transaction;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableMap;
import com.hankcs.hanlp.HanLP;
import com.hankcs.hanlp.dictionary.CustomDictionary;
import com.hankcs.hanlp.dictionary.stopword.CoreStopWordDictionary;
import com.hankcs.hanlp.seg.common.Term;
import com.hankcs.hanlp.tokenizer.IndexTokenizer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    @Autowired
    private LoggerConfigure Logger;

    private static final Logger LOGGER = LoggerFactory.getLogger(SplitterServer.class);

    public void deleteStop(String key) {
        Transaction t = Cat.newTransaction("SOLR.Splitter", "delete");
        try {
            if (this.Logger.info()) {
                LOGGER.info("[ delete stop words,{} ]", key);
            } else {
                Cat.logEvent("Splitter", "delete", Transaction.SUCCESS, key);
            }
            delHashOpsProperty("stop", key);
            CoreStopWordDictionary.remove(key);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception ex) {
            if (this.Logger.info()) {
                LOGGER.error(ex.getMessage());
            } else {
                Cat.logError(ex);
            }
            t.setStatus(ex);
        } finally {
            t.complete();
        }
    }

    public void deleteCustom(String key) {
        Transaction t = Cat.newTransaction("SOLR.Splitter", "delete");
        try {
            if (this.Logger.info()) {
                LOGGER.info("[ delete custom words,{} ]", key);
            } else {
                Cat.logEvent("Splitter", "delete", Transaction.SUCCESS, key);
            }
            delHashOpsProperty("custom", key);
            CustomDictionary.remove(key);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception ex) {
            if (this.Logger.info()) {
                LOGGER.error(ex.getMessage());
            } else {
                Cat.logError(ex);
            }
            t.setStatus(ex);
        } finally {
            t.complete();
        }
    }

    public void addStop(String key, String value) {
        Transaction t = Cat.newTransaction("SOLR.Splitter", "put");
        try {
            if (this.Logger.info()) {
                LOGGER.info("[ add stop words key : {}, value: {} ]", key, value);
            } else {
                Cat.logEvent("Splitter", "add", Transaction.SUCCESS, key.concat(":").concat(value));
            }
            setKVassert(key, value);
            setHashOpsProperty("stop", key, value);
            CoreStopWordDictionary.add(key);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception ex) {
            if (this.Logger.info()) {
                LOGGER.error(ex.getMessage());
            } else {
                Cat.logError(ex);
            }
            t.setStatus(ex);
        } finally {
            t.complete();
        }
    }

    public void addCustom(String key, String value) {
        Transaction t = Cat.newTransaction("SOLR.Splitter", "put");
        try {
            if (this.Logger.info()) {
                LOGGER.info("[ add custom words key : {}, value: {} ]", key, value);
            } else {
                Cat.logEvent("Splitter", "add", Transaction.SUCCESS, key.concat(":").concat(value));
            }
            setKVassert(key, value);
            setHashOpsProperty("custom", key, value);
            /**
             *  必须增加到缓存中，否则不会立即生效，除非重启服务器才会生效
             */
            CustomDictionary.add(key);
            t.setStatus(Transaction.SUCCESS);
        } catch (Exception ex) {
            if (this.Logger.info()) {
                LOGGER.error(ex.getMessage());
            } else {
                Cat.logError(ex);
            }
            t.setStatus(ex);
        } finally {
            t.complete();
        }
    }

    public List<Term> splitter(@RequestParam String keywords) {
        Transaction t = Cat.newTransaction("SOLR.Splitter", "post");
        try {
            if (this.Logger.info()) {
                LOGGER.info("[ keywords splitter result：,{} ]", keywords);
            } else {
                Cat.logEvent("Splitter", "post", Transaction.SUCCESS, keywords);
            }
            HanLP.Config.setRedisTemplate(redisTemplate);
            List<Term> lists = IndexTokenizer.segment(keywords);
            CoreStopWordDictionary.apply(lists);
            t.setStatus(Transaction.SUCCESS);
            return lists;
        } catch (Exception ex) {
            if (this.Logger.info()) {
                LOGGER.error(ex.getMessage());
            } else {
                Cat.logError(ex);
            }
            t.setStatus(ex);
        } finally {
            t.complete();
        }
        return null;
    }

    public Map<String, String> put(@PathVariable String keys, @PathVariable String key) {

        Transaction t = Cat.newTransaction("SOLR.Splitter", "put");
        try {
            if (this.Logger.info()) {
                LOGGER.info("[ Get word keys according to word key：,{} ]", keys, key);
            } else {
                Cat.logEvent("Splitter", "put", Transaction.SUCCESS, keys.concat(":").concat(key));
            }
            setVaildata(keys, key);
            String val = getHashOperations().get(keys, key);
            val = StringUtil.notNull(val) ?
                    key.concat(":").concat(Joiner.on(",").skipNulls().join(val.split("="))) : new String("404");
            Map<String, String> map = ImmutableMap.of("result", val);
            t.setStatus(Transaction.SUCCESS);
            return map;
        } catch (Exception ex) {
            if (this.Logger.info()) {
                LOGGER.error(ex.getMessage());
            } else {
                Cat.logError(ex);
            }
            t.setStatus(ex);
        } finally {
            t.complete();
        }
        return null;
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
