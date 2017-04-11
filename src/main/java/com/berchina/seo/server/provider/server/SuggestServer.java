package com.berchina.seo.server.provider.server;

import com.berchina.seo.server.configloader.config.logger.LoggerConfigure;
import com.berchina.seo.server.configloader.exception.server.ServerException;
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

    public List<Map<String,Object>> search(int start, int rows) throws IOException, SolrServerException {
        return this.suggestCollection(repository.search(start, rows).getResults());
    }

    /**
     * 联想词查询
     *
     * @param keyword
     * @return
     */
    public List<Map<String,Object>>  search(String keyword) throws IOException, SolrServerException {

        return suggestCollection(repository.search(keyword).getResults());
    }

    public List<Map<String,Object>> suggestCollection(SolrDocumentList documents) {
        List<Map<String,Object>> suggest = Lists.newLinkedList();
        if (!StringUtils.isEmpty(documents) && documents.size() > 0)
        {
            for (SolrDocument doc : documents)
            {
                Map<String,Object> maps = Maps.newHashMap();
                String correlation = StringUtil.StringConvert(doc.get("correlation"));
                if (StringUtil.notNull(correlation))
                {
                    List<Map<String,String>> lists = Lists.newLinkedList();
                    correlation = correlation.replace("[", "").replace("]", "");
                    List<String> correlations = StringUtil.splitter(",",correlation);
                    for (String attr : correlations)
                    {
                        List<String> tres = StringUtil.splitter(":",attr);
                        Map<String,String> map = Maps.newHashMap();
                        map.put("type",tres.get(0));
                        map.put("id",tres.get(1));
                        map.put("name",tres.get(2));
                        lists.add(map);
                    }
                    maps.put("corr",lists);
                }
                maps.put("keyword",StringUtil.StringConvert(doc.get("keyword")));
                suggest.add(maps);
            }
        }
        return suggest;
    }

    /**
     * 新增联想词
     *
     * @param keyword
     * @return
     */
    public Map<String, Object> add(String keyword, String correlation) throws IOException, SolrServerException {

        Map<String, Object> maps = Maps.newHashMap();
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
            if (StringUtil.notNull(pinyin)) {
                doc.addField("pinyin", pinyin);
            }
            if (StringUtil.notNull(py)) {
                doc.addField("abbre", py);
            }
            if (StringUtil.notNull(correlation))
            {
                List<String> correlations = StringUtil.splitter(",", correlation);
                for (int i = 0; i < correlations.size(); i++)
                {
                    doc.addField("correlation", correlations.get(i));
                }
            }
            UpdateResponse response = repository.add(doc);
            if (response.getStatus() == 0)
            {
                return this.setResult(maps, HttpStatus.SC_OK, "Success");
            }
        }
        return this.setResult(maps, ServerException.SEO_SUGGEST_ADD_FAIL_ERROR.getErrCode(), ServerException.SEO_SUGGEST_ADD_FAIL_ERROR.getErroMssage());
    }


    public Map<String, Object> setResult(Map<String, Object> maps, Integer code, String message) {
        maps.put("code", code);
        maps.put("result", message);
        return maps;
    }

    /**
     * 删除联想词
     *
     * @param id
     * @return
     */
    public Map<String, Object> delete(String id) throws IOException, SolrServerException {
        Map<String, Object> maps = Maps.newHashMap();
        UpdateResponse response = repository.delete(id);
        if (response.getStatus() == 0) {
            return this.setResult(maps, HttpStatus.SC_OK, "Success");
        }
        return this.setResult(maps, ServerException.SEO_SUGGEST_DEL_FAIL_ERROR.getErrCode(), ServerException.SEO_SUGGEST_DEL_FAIL_ERROR.getErroMssage());
    }

    /**
     * 更新联想词
     *
     * @param keyword
     * @return
     */
    public Map<String, Object> update(String id, String keyword, String correlation) throws IOException, SolrServerException {
        Map<String, Object> maps = Maps.newHashMap();

        Map<String, Object> delSuggest = this.delete(id);
        Map<String, Object> addSuggest = this.add(keyword, correlation);

        if (StringUtil.StringConvert(delSuggest.get("code")).equals(StringUtil.StringConvert(HttpStatus.SC_OK))
                && StringUtil.StringConvert(addSuggest.get("code")).equals(StringUtil.StringConvert(HttpStatus.SC_OK))) {
            return this.setResult(maps, HttpStatus.SC_OK, "Success");
        }
        return this.setResult(maps, ServerException.SEO_SUGGEST_MOD_FAIL_ERROR.getErrCode(), ServerException.SEO_SUGGEST_MOD_FAIL_ERROR.getErroMssage());
    }
}
