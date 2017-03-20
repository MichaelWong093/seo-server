package com.berchina.seo.server.provider.server.crud;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Map;

import com.berchina.seo.server.configloader.config.solr.SolrServerFactoryBean;
import com.google.common.collect.Lists;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrQuery.ORDER;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.berchina.seo.server.configloader.exception.SeoException;
import com.berchina.seo.server.configloader.exception.server.ServerException;
import com.berchina.seo.server.provider.client.SeoRequest;
import com.berchina.seo.server.provider.model.SeoHotWords;
import com.berchina.seo.server.provider.utils.Constants;
import com.berchina.seo.server.provider.utils.IOUtills;
import com.berchina.seo.server.provider.utils.SolrUtils;
import com.hankcs.hanlp.corpus.util.StringUtils;

/**
 * 热词搜索 处理逻辑
 *
 * @author halley (yanhuiqing)
 */
@Repository
public class SeoHotWordRepository {
    private static final Logger LOGGER = LoggerFactory.getLogger(SeoHotWordRepository.class);

    @Autowired
    private SolrServerFactoryBean factoryBean;

    @Autowired
    private SegmentIndexRepository segRepository;

    private HttpSolrClient solrClient;

    private QueryResponse response;

    public QueryResponse getResponse() {
        return response;
    }

    public void setSeoResponseInfo( Map<String, Object> maps , SeoRequest request) throws IOException, SolrServerException {

        LinkedList<SeoHotWords> wordses = Lists.newLinkedList();

        if (null != request.getHotwords() && !StringUtils.isBlankOrNull(request.getHotwords().trim())) {

            String fieldName = SolrUtils.getNameByHWChannel(request.getChannel());

            SolrDocumentList docs = this.querySolrDocuments(request);

            shuffleUniqueList(docs, fieldName);

            int args = docs.size();

            for (int i = 0; i < args; i++) {
                SeoHotWords hotwords = new SeoHotWords();
                hotwords.setHotWord(SolrUtils.getParameter(docs, i, fieldName));//搜到的热词
                hotwords.setPinyin(SolrUtils.getParameter(docs, i, "pinyin"));//全拼
                hotwords.setAbbre(SolrUtils.getParameter(docs, i, "abbre"));//缩写
                hotwords.setFrequency(Long.parseLong(SolrUtils.getParameter(docs, i, "frequency")));
                wordses.add(hotwords);
            }
        }
        maps.put("hotWords",wordses);
    }


    /*
     * 通过渠道号 获取solr的client连接
     */
    public void getSolrClient(String channal) {
        Map<String, HttpSolrClient> solrMap = factoryBean.httpSolrServer();
        solrClient = solrMap.get(channal);
    }

    /**
     * 查询 索引之前 组装必要信息
     *
     * @param request
     * @return
     */
    public SeoHotWordRepository beforeQuery(SeoRequest request) {

        IOUtills.recordKw2Log(request.getHotwords(), request.getChannel());//记录 搜索 关键字 到文件

        getSolrClient(request.getChannel());

        return this;
    }

    /**
     * 开始 查询索引
     *
     * @param request
     * @param rightMatch 是否右匹配查询
     * @return
     */
    public SeoHotWordRepository query(SeoRequest request, boolean rightMatch) {
        try {
            String words = request.getHotwords();
            String channel = request.getChannel();
            SolrQuery query = rightMatch ? this.rightMatchQuery(words, channel, request)
                    : this.fullMatchQuery(words, channel, request);
            response = solrClient.query(query);
        } catch (SolrServerException | IOException e) {
            SolrUtils.rollBack(solrClient);
            LOGGER.error("[ solr 服务器异常 : {} ]", e.getMessage());
            throw new SeoException(e.getMessage(),
                    ServerException.SEO_SERVER_URL_CONNECT_ERROR);
        }
        return this;
    }


    /**
     * 查询完索引 回收资源
     *
     * @return
     */
    public SeoHotWordRepository afterQuery() {
        //SolrUtils.whenFinished(solrClient);
        return this;
    }


    /**
     * 查询索引
     *
     * @param request
     * @return
     * @throws SolrServerException
     * @throws IOException
     */
    public SolrDocumentList querySolrDocuments(SeoRequest request) throws SolrServerException, IOException {
        SolrDocumentList targetList = new SolrDocumentList();
        //处理查询逻辑,查询热词索引
        SeoHotWordRepository repository = this.beforeQuery(request).query(request, true).afterQuery();
        QueryResponse response = repository.getResponse();
        if (null != response) {
            SolrDocumentList rightMlist = response.getResults();
            int size = rightMlist.size();
            if (size < Constants.DEFALT_ROWS) {//如果右匹配的 关键字 少于默认值 则 全匹配查询 ，查够 默认的数量
                request.setOther(String.valueOf(Constants.DEFALT_ROWS - size));
                this.query(request, false);//全匹配查询
                QueryResponse fullRespse = this.getResponse();
                targetList.addAll(response.getResults());
                targetList.addAll(fullRespse.getResults());
            } else {
                targetList.addAll(response.getResults());
            }

        }
        return targetList;
    }


    /*
     * 清洗list 以某个字段值 为唯一标识 去重
     */
    private void shuffleUniqueList(SolrDocumentList doc, String fieldName) {
        for (int i = 0; i < doc.size(); i++) {
            SolrDocument orig = doc.get(i);
            for (int j = i + 1; j < doc.size(); j++) {
                SolrDocument dest = doc.get(j);
                if (SolrUtils.solrDocEqualByField(fieldName, orig, dest)) {
                    doc.remove(j);
                }
            }
        }
    }

    /*
     * 构造提取热词 的右匹配 查询
     */
    public SolrQuery rightMatchQuery(String words, String channel, SeoRequest request) {
        SolrQuery params = new SolrQuery();

        if (null != words && !org.springframework.util.StringUtils.isEmpty(words.trim())) {
            String rows = request.getOther();//此处的 other 字段用来 搜索热词的条数，可以供调用接口着动态设置，默认为 6。
            int rowsNum = StringUtils.isBlankOrNull(rows) ? Constants.DEFALT_ROWS : Integer.parseInt(rows);

            String queryName = Constants.HW_SUGGEST;
            String facetName = SolrUtils.getNameByHWChannel(channel);

            params.setQuery(queryName + Constants.COLON + words + "*");
            params.add("start", Constants.ZERO + "");
            params.add("rows", rowsNum + "");
            params.addSort("frequency", ORDER.desc);
            params.setFacet(true)
                    .setFacetMinCount(1)
                    .setFacetLimit(Constants.DEFALT_ROWS)//段
                    .addFacetField(facetName);
        }
        return params;
    }

    /*
     * 构造提取热词 的全匹配 查询
     */
    public SolrQuery fullMatchQuery(String words, String channel, SeoRequest request) {
        String rows = request.getOther();//此处的 other 字段用来 搜索热词的条数，可以供调用接口着动态设置，默认为 6。
        int rowsNum = StringUtils.isBlankOrNull(rows) ? Constants.DEFALT_ROWS : Integer.parseInt(rows);

        SolrQuery params = new SolrQuery();
        String queryName = Constants.HW_SUGGEST;
        String facetName = SolrUtils.getNameByHWChannel(channel);
        words = words == null ? "" : words;
        params.setQuery(queryName + Constants.COLON + "*" + words + "*");

        params.add("start", Constants.ZERO + "");
        params.add("rows", rowsNum + "");
        params.addSort("frequency", ORDER.desc);
        params.setFacet(true)
                .setFacetMinCount(1)
                .setFacetLimit(Constants.DEFALT_ROWS)//段
                .addFacetField(facetName);
        return params;
    }

    /*
     * 查询商品 top 5 的热词搜索量
     */
    public SolrDocumentList queryRecommentdDocuments(Object objects) throws SolrServerException, IOException {
        SeoRequest request = (SeoRequest) objects;
        SolrDocumentList targetList = new SolrDocumentList();
        SeoHotWordRepository repository = queryTopGoodsRecommend(request).afterQuery();
        QueryResponse response = repository.getResponse();
        if (null != response) {
            SolrDocumentList rightMlist = response.getResults();
            targetList.addAll(rightMlist);

        }
        return targetList;
    }

    /*
     * 查询 商品推荐的top 热词搜索
     */
    public SeoHotWordRepository queryTopGoodsRecommend(SeoRequest request) {

        try {
            String channel = request.getChannel();
            getSolrClient(channel);
            SolrQuery query = this.topFrequecyQuery();
            response = solrClient.query(query);
        } catch (SolrServerException | IOException e) {
            SolrUtils.rollBack(solrClient);
            LOGGER.error("[ solr 服务器异常 : {} ]", e.getMessage());
            throw new SeoException(e.getMessage(),
                    ServerException.SEO_SERVER_URL_CONNECT_ERROR);
        }
        return this;

    }

    public SolrQuery topFrequecyQuery() {
        SolrQuery params = new SolrQuery();
        String queryName = Constants.HW_SUGGEST;
        params.setQuery(queryName + Constants.COLON + "*");

        params.add("start", Constants.ZERO + "");
        params.add("rows", Constants.TOP_GOODS_RCM_NUM + "");
        params.addSort("frequency", ORDER.desc);
        return params;
    }
}
