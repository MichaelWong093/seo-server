package com.berchina.seo.server.provider.server;

import com.berchina.seo.server.configloader.config.logger.LoggerConfigure;
import com.berchina.seo.server.configloader.exception.server.ServerException;
import com.berchina.seo.server.provider.model.SeoHotWords;
import com.berchina.seo.server.provider.server.crud.SuggestRepository;
import com.berchina.seo.server.provider.utils.SerialNumber;
import com.berchina.seo.server.provider.utils.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.hankcs.hanlp.HanLP;
import org.apache.http.HttpStatus;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * @Package com.berchina.seo.server.provider.server
 * @Description: TODO ( 联想词服务 )
 * @Author rxbyes
 * @Date 2017 下午5:03
 * @Version V1.0
 */
@Service
public class SuggestServer {

    @Autowired
    private SuggestRepository repository;

    @Autowired
    private LoggerConfigure Logger;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SuggestServer.class);
    /**
     * 联想词查询
     *
     * @param keyword
     * @return
     */
    public Map<String,List<SeoHotWords>> search(String keyword) throws IOException, SolrServerException {
        Map<String, List<SeoHotWords>> maps = Maps.newLinkedHashMap();
        SolrDocumentList list = repository.search(keyword).getResults();
        if (!StringUtils.isEmpty(list) && list.size() > 0)
        {
            List<SeoHotWords> words = Lists.newLinkedList();
            for (SolrDocument doc : list)
            {
                SeoHotWords hotWord = new SeoHotWords();
                hotWord.setKeyword(StringUtil.StringConvert(doc.get("keyword")));
                String correlation = StringUtil.StringConvert(doc.get("correlation"));
                if (StringUtil.notNull(correlation))
                {
                    hotWord.setCorrelation(Arrays.asList(correlation.split(" ")));
                }
                words.add(hotWord);
            }
            maps.put("message", words);
        }
        return maps;
    }

    /**
     * 新增联想词
     *
     * @param keyword
     * @return
     */
    public  Map<String,Object> add(String keyword, String correlation) throws IOException, SolrServerException {

        Map<String,Object> maps = Maps.newHashMap();
        SolrInputDocument doc = new SolrInputDocument();
        Assert.notNull("keyword is not empty ", keyword);

        if (repository.isCheckKeyWordsBe(keyword) == 0)
        {
            HanLP.Config.setRedisTemplate(redisTemplate);

            String pinyin = HanLP.convertToPinyinString(keyword, "", true);
            String py = HanLP.convertToPinyinFirstCharString(keyword, "", true);

            doc.addField("keyword", keyword);
            doc.addField("id", SerialNumber.getInstance().generaterNextNumber());
            doc.addField("frequency", 0);
            if (StringUtil.notNull(pinyin))
            {
                doc.addField("pinyin", pinyin);
            }
            if (StringUtil.notNull(py))
            {
                doc.addField("abbre", py);
            }
            if (StringUtil.notNull(correlation))
            {
                doc.addField("correlation", correlation);
            }
            UpdateResponse response = repository.add(doc);
            if (response.getStatus() == 0)
            {
                return this.setResult(maps,HttpStatus.SC_OK,"Success");
            }
        }
        return this.setResult(maps,ServerException.SEO_SUGGEST_ADD_FAIL_ERROR.getErrCode(),ServerException.SEO_SUGGEST_ADD_FAIL_ERROR.getErroMssage());
    }

    public Map<String, Object> setResult(Map<String, Object> maps,Integer code, String message)
    {
        maps.put("code",code);
        maps.put("message",message);
        return maps;
    }

    /**
     * 删除联想词
     *
     * @param id
     * @return
     */
    public Map<String, Object> delete(String id) throws IOException, SolrServerException {
        Map<String, Object> maps  = Maps.newHashMap();
        UpdateResponse response = repository.delete(id);
        if (response.getStatus() == 0)
        {
            return this.setResult(maps, HttpStatus.SC_OK,"Success");
        }
        return this.setResult(maps,ServerException.SEO_SUGGEST_DEL_FAIL_ERROR.getErrCode(),ServerException.SEO_SUGGEST_DEL_FAIL_ERROR.getErroMssage());
    }

    /**
     * 更新联想词
     *
     * @param keyword
     * @return
     */
    public Map<String, Object> update(String id, String keyword,String correlation) throws IOException, SolrServerException {
        Map<String, Object> maps  = Maps.newHashMap();

        Map<String,Object> delSuggest = this.delete(id);
        Map<String,Object> addSuggest = this.add(keyword,correlation);

        if (StringUtil.StringConvert(delSuggest.get("code")).equals(StringUtil.StringConvert(HttpStatus.SC_OK))
                && StringUtil.StringConvert(addSuggest.get("code")).equals(StringUtil.StringConvert(HttpStatus.SC_OK)))
        {
            return this.setResult(maps,HttpStatus.SC_OK,"Success");
        }
        return this.setResult(maps,ServerException.SEO_SUGGEST_MOD_FAIL_ERROR.getErrCode(),ServerException.SEO_SUGGEST_MOD_FAIL_ERROR.getErroMssage());
    }
}
