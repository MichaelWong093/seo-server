package com.berchina.seo.server.provider.server.crud;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.FacetField.Count;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.assertj.core.util.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.berchina.seo.server.configloader.config.SolrServerFactoryBean;
import com.berchina.seo.server.configloader.exception.SeoException;
import com.berchina.seo.server.configloader.exception.server.ServerException;
import com.berchina.seo.server.provider.segment.EasySeg;
import com.berchina.seo.server.provider.utils.Constants;
import com.berchina.seo.server.provider.utils.SolrUtils;
import com.berchina.seo.server.provider.utils.StringUtil;
import com.google.common.collect.Maps;
import com.hankcs.hanlp.corpus.util.StringUtils;
import com.hankcs.hanlp.seg.common.Term;

/**
 * 分词后 solr 索引创建 常用方法
 *
 * @author halley (yanhuiqing)
 */
@Repository
public class SegmentIndexRepository {

    private static final Logger LOGGER = LoggerFactory.getLogger(SegmentIndexRepository.class);

    @Autowired
    private SolrServerFactoryBean factoryBean;

    private HttpSolrClient solrClient;

    public SegmentIndexRepository() {
    }

    /*
     * 通过渠道号 获取solr的client连接
     */
    public void getSolrClient(String channal) {
        Map<String, HttpSolrClient> solrMap = factoryBean.httpSolrServer();
        solrClient = solrMap.get(channal);
    }

    /*
     * 对关键词进行分词
     */
    public List<String> segKeyWords(String keyWords) {
        List<String> result = Lists.newArrayList();
        if (StringUtil.isChinese(keyWords) && keyWords.length() > Constants.SEG_LEN) {
            List<Term> segs = EasySeg.newSegment().seg(keyWords);
            filterSegmentsList(segs, result);
        }
        result.add(keyWords);
        return result;
    }

	/*
     * 过滤 分词后无用的词
	 */

    private void filterSegmentsList(List<Term> segs, List<String> result) {
        for (Term t : segs) {
            String s = t.nature.toString();
            if (s.startsWith("n")
                    || s.startsWith("a")
                    || s.startsWith("v")
                    || s.startsWith("b")) {
                result.add(t.word);
            }
        }

    }

    /*
     * 创建历史搜索索引 无须分词（目前根据mac地址分区存储）
     */
    @Deprecated
    public void createIndex(String keyWords, String channel, String mac) {
        getSolrClient(channel);//获得索引连接
        SolrInputDocument solrDoc = new SolrInputDocument();
        assembleSolrFiedlByHisSearch(solrDoc, channel, keyWords, mac);
        UpdateResponse response = SolrUtils.addThen(solrClient, solrDoc);
        LOGGER.info("在渠道[" + channel + "]创建关键字索引消耗的时间为：" + response.getElapsedTime() + ",索引内容为：" + solrDoc.toString());
    }

    /*
     * 创建历史搜索索引 无须分词（目前根据mac地址分区存储）
     */
    @Deprecated
    public void createIndex(List<String> list, String channel) {
        for (String words : list) {
            if (!StringUtils.isBlankOrNull(words)) {
                String[] strLine = words.split(Constants.COLON);//[0]:mac 地址；[1] 历史搜索关键字
                createIndex(strLine[1], channel, strLine[0]);
            }

        }

    }

    //设置历史搜索的关键字 索引
    @Deprecated
    private void assembleSolrFiedlByHisSearch(SolrInputDocument solrDoc,
                                              String channel, String keyWords, String mac) {
        if (channel.equals(Constants.SEO_GOODS_HOTWD_)) {
            solrDoc.addField("goodsHisSearch", keyWords);
            solrDoc.addField("mac", mac);
            solrDoc.addField("id", StringUtil.uniqueId());

        } else if (channel.equals(Constants.SEO_SHOP_HOTWD_)) {
            solrDoc.addField("shopHisSearch", keyWords);
            solrDoc.addField("mac", mac);
            solrDoc.addField("id", StringUtil.uniqueId());

        } else {
        }

    }

    /*
     * 根据关键词分词 后 创建索引
     */
    public void segThenCreateIndex(String keyWords, String channel) {
        List<String> segments = segKeyWords(keyWords);
        //filterSegments(segments,channel);//过滤分词,过滤规则为：如果存在 商品和店铺的索引库则创建
        getSolrClient(channel);//获得索引连接
        for (String words : segments) {
            if (StringUtil.isChinese(
                    StringUtil.replaceBlank(words)) && words.length() > 1) {
                SolrInputDocument solrDoc = new SolrInputDocument();
                SolrUtils.convert2PinyinField(solrDoc, words);//拼音转换
                assembleSolrFieldByChannel(solrDoc, channel, words);//根据渠道设置 solr 字段属性
                assembleSolrFieldByCount(solrDoc, channel, words);//根据 渠道设置 solr对应索引库字段的词频
                UpdateResponse response = SolrUtils.addThen(solrClient, solrDoc);
                LOGGER.info("在渠道[" + channel + "]创建关键字索引消耗的时间为：" + response.getElapsedTime() + ",索引内容为：" + solrDoc.toString());
            }
        }
    }

    /*
     * 设置关键字的词频
     */
    private void assembleSolrFieldByCount(SolrInputDocument solrDoc,
                                          String channel, String words) {
        Map<String, Object> map = queryListCountSByHotWords(solrClient, words, channel);
        Long count = (Long) map.get(words);
        String cnt = (String) map.get("frequency");
        Map<String, Object> oper = new HashMap<String, Object>();

        if (null == count) {
            solrDoc.addField("id", StringUtil.uniqueId());
            solrDoc.addField("frequency", 1);
        } else {
            solrDoc.addField("id", map.get("id"));
            oper.put("set", Integer.valueOf(cnt) + 1);
            solrDoc.addField("frequency", oper);
        }

    }

    /*
     * 过滤分词结果 过滤规则匹配索引库
     */
    private void filterSegments(List<String> segments, String channel) {
        for (int i = 0; i < segments.size(); i++) {
            String words = segments.get(i);
            if (!keyWordsLikeThis(words, channel)) {//如果找不到则为 无关的词汇
                segments.remove(i);
            }
        }
    }

    /*
     * 根据 商品和店铺的热词渠道号 切换 商品和店铺的索引库 连接
     */
    public void shiftSolrClient(String channel) {
        if (channel.equals(Constants.SEO_GOODS_HOTWD_)) {
            getSolrClient(Constants.SEO_GOODS_);//商品 索引库

        } else if (channel.equals(Constants.SEO_SHOP_HOTWD_)) {
            getSolrClient(Constants.SEO_SHOP_);//店铺 索引库

        } else {
        }
    }

    /*
     * 根据渠道号 判断 关键字在对应的索引库是否能查到，避免创建 关键字的垃圾索引
     */
    public boolean keyWordsLikeThis(String words, String channel) {
        shiftSolrClient(channel);
        SolrDocumentList list = this.queryDocListSolrByKeyWords(solrClient, words);
        if (null != list && list.getNumFound() > 0) {//如果搜索到结果则返回true
            return true;
        }
        return false;
    }

    /*
     * 从solr索引库查询 关键字 返回 SolrDocumentList
     */
    public SolrDocumentList queryDocListSolrByKeyWords(HttpSolrClient solrClient, String words) {
        QueryResponse response = queryResponseSolrByKeyWords(solrClient, words);
        return response.getResults();
    }

    /*
     * 从solr 索引库查询 关键字 返回 Map<String,Long> 字符串：出现的次数
     */
    public Map<String, Long> queryListCountSolrByKeyWords(HttpSolrClient solrClient, String words) {
        QueryResponse response = queryResponseSolrByKeyWords(solrClient, words);
        List<FacetField> facets = response.getFacetFields();
        Map<String, Long> result = Maps.newHashMap();
        for (FacetField facet : facets) {
            List<Count> facetCounts = facet.getValues();
            for (FacetField.Count count : facetCounts) {
                result.put(count.getName(), count.getCount());
                LOGGER.info("字符串[" + count.getName() + "] 出现的次数为 ： " + count.getCount());
            }
        }

        return result;
    }

    /*
     * 从solr索引库查询 返回QueryResponse
     */
    public QueryResponse queryResponseSolrByKeyWords(HttpSolrClient solrClient, String words) {
        try {
            SolrQuery params = new SolrQuery();
            params.setQuery("hotwords:*" + words + "*");

            params.add("start", Constants.ZERO + "");
            params.add("rows", Constants.DEFALT_ROWS + "");
            params.addSort("hotwords", ORDER.desc);
            params.setFacet(true)
                    .setFacetMinCount(1)
                    .setFacetLimit(Constants.DEFALT_ROWS)//段
                    .addFacetField("hotwords");
            QueryResponse response = solrClient.query(params);
            return response;
        } catch (SolrServerException e) {
            SolrUtils.rollBack(solrClient);
            LOGGER.error("[ solr 服务器异常 : {} ]", e.getMessage());
            throw new SeoException(e.getMessage(),
                    ServerException.SEO_SERVER_URL_CONNECT_ERROR);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            SolrUtils.commit(solrClient);
        }
        return null;
    }

    /*
     * 从solr 索引库查询 关键字 返回 Map<String,Long> 字符串：出现的次数
     */
    public Map<String, Object> queryListCountSByHotWords(HttpSolrClient solrClient, String words, String channel) {
        QueryResponse response = queryResponseByHotWords(solrClient, words, channel);
        Map<String, Object> result = Maps.newHashMap();
        SolrDocumentList docs = response.getResults();
        if (docs.getNumFound() > 0) {
            String id = SolrUtils.getParameter(docs, 0, "id");
            String frequency = SolrUtils.getParameter(docs, 0, "frequency");
            result.put("id", id);
            result.put("frequency", frequency);
            LOGGER.info("字符串[" + words + "] 在索引中的频次为 ： " + frequency);
        }

        List<FacetField> facets = response.getFacetFields();

        for (FacetField facet : facets) {
            List<Count> facetCounts = facet.getValues();
            for (FacetField.Count count : facetCounts) {
                result.put(count.getName(), count.getCount());
                LOGGER.info("字符串[" + count.getName() + "] 分片查询出现的次数为 ： " + count.getCount());
            }
        }

        return result;
    }

    /*
     * 从热词 索引库提取 出现的词组
     */
    public QueryResponse queryResponseByHotWords(HttpSolrClient solrClient, String words, String channel) {
        try {
            SolrQuery params = equalMatchQuery(words, channel);//全等 匹配
            QueryResponse response = solrClient.query(params);
            return response;
        } catch (IOException e) {
            SolrUtils.rollBack(solrClient);
            LOGGER.error("[ solr 服务器异常 : {} ]", e.getMessage());
            throw new SeoException(e.getMessage(),
                    ServerException.SEO_SERVER_URL_CONNECT_ERROR);
        } catch (SolrServerException e) {
            e.printStackTrace();
        } finally {
            SolrUtils.commit(solrClient);
        }
        return null;
    }

    /*
     * 构造提取热词 的全等匹配 查询
     */
    public SolrQuery equalMatchQuery(String words, String channel) {
        SolrQuery params = new SolrQuery();
        String sortName = Constants.HW_SUGGEST;
        String facetName = SolrUtils.getNameByHWChannel(channel);
        //SOLR 表达式 匹配
        params.setQuery(sortName + Constants.COLON + words);

        params.add("start", Constants.ZERO + "");
        params.add("rows", Constants.DEFALT_ROWS + "");
        params.addSort("frequency", ORDER.desc);
        params.setFacet(true)
                .setFacetMinCount(1)
                .setFacetLimit(Constants.DEFALT_ROWS)//段
                .addFacetField(facetName);
        return params;
    }

    /*
     * 根据 数据集合 分词后创建索引
     */
    public void segThenCreateIndex(List<String> keyWords, String channel) {

        for (String words : keyWords) {
            segThenCreateIndex(words, channel);
        }
    }

    /*
     * 添加 商品热词 和 店铺热词 的索引字段
     */
    private void assembleSolrFieldByChannel(SolrInputDocument solrDoc, String channel, String keyWords) {

        if (channel.equals(Constants.SEO_GOODS_HOTWD_)) {
            solrDoc.addField("goodsName", keyWords);

        } else if (channel.equals(Constants.SEO_SHOP_HOTWD_)) {
            solrDoc.addField("shopName", keyWords);

        } else {
        }
    }

}
