package com.berchina.seo.server.provider.server;

import com.berchina.seo.server.configloader.config.logger.LoggerConfigure;
import com.berchina.seo.server.provider.model.SeoHotWords;
import com.berchina.seo.server.provider.server.crud.SuggestRepository;
import com.berchina.seo.server.provider.utils.StringUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
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

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(SuggestServer.class);

    /**
     * 联想词查询
     *
     * @param keyword
     * @return
     */
    public Map<String, List<SeoHotWords>> search(String keyword) {

        Map<String, List<SeoHotWords>> maps = Maps.newLinkedHashMap();

        List<SeoHotWords> words = Lists.newLinkedList();
        try {
            if (this.Logger.info()) {
                LOGGER.info("suggest out info : {}", keyword);
            }
            SolrDocumentList list = repository.search(keyword).getResults();

            if (!StringUtils.isEmpty(list) && list.size() > 0) {
                for (SolrDocument doc : list
                        ) {
                    SeoHotWords word = new SeoHotWords();

                    word.setKeyword(StringUtil.StringConvert(doc.get("goodsName")));

                    words.add(word);
                }
                maps.put("suggest", words);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        return maps;
    }

    /**
     * 新增联想词
     *
     * @param keyword
     * @return
     */
    public boolean add(String keyword, String correlation) {

        try {
            return repository.add(keyword, correlation);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SolrServerException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 删除联想词
     *
     * @param keyword
     * @return
     */
    public void delete(String keyword) {

        repository.delete(keyword);
    }

    /**
     * 更新联想词
     *
     * @param keyword
     * @return
     */
    public void update(String keyword) {

        repository.update(keyword);
    }
}
