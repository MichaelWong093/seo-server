package com.berchina.seo.server.provider.server;

import com.berchina.seo.server.provider.client.SeoRequest;
import com.berchina.seo.server.provider.server.crud.SearchRepository;
import com.berchina.seo.server.provider.utils.CateUtils;
import com.berchina.seo.server.provider.utils.SolrPageUtil;
import com.berchina.seo.server.provider.utils.StringUtil;
import com.google.common.collect.Maps;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @Package com.berchina.seo.server.provider.server
 * @Description: 搜索商品服务
 * @Author rxbyes
 * @Date 2017 下午8:05
 * @Version V1.0
 */
@Service
public class SearchServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(MethodHandles.lookup().lookupClass());

    @Autowired private SearchRepository repository;

    @Autowired private CategoryServer categoryServer;

    /**
     *  筛选入口
     * @param category
     * @return
     * @throws IOException
     * @throws SolrServerException
     */
    public List<Object> change(String category) throws IOException, SolrServerException {

        if (StringUtil.notNull(category))
        {
            return categoryServer.change(category);
        }
        throw new NullPointerException("category is not empty, data structure eg：1,2,3,5");
    }

    /**
     * 商品搜索
     *
     * @param request
     * @return
     */
    public Map<String,Object> search(SeoRequest request) throws IOException, SolrServerException {

        Map<String,Object> maps = Maps.newConcurrentMap();

        QueryResponse response = this.repository.search(request);

        SolrDocumentList documents = response.getResults();

        List<Object> seoGoods = CateUtils.setGoodsDoc(documents);

        if (StringUtil.notNull(seoGoods))
        {
            SolrPageUtil.getPageInfo(maps, request, documents);

            maps.put("goods",seoGoods);
            // 查询分类，该功能只取系统类目
            Map<String,Object> map = this.categoryServer.search(response);

            Set<Map.Entry<String, Object>> set = map.entrySet();
            if (!set.isEmpty())
            {
                for (Map.Entry entry : set )
                {
                    maps.put((String) entry.getKey(),entry.getValue());
                }
            }
        }
        return maps;
    }
}
