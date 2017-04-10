package com.berchina.seo.server.provider.server;

import com.berchina.seo.server.provider.server.crud.CategoryRepository;
import com.berchina.seo.server.provider.utils.StringUtil;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

/**
 * @Package com.berchina.seo.server.provider.server
 * @Description: TODO (  )
 * @Author rxbyes
 * @Date 2017 下午9:59
 * @Version V1.0
 */
@Service
public class CategoryServer {

    @Autowired
    private CategoryRepository repository;

    public List<Object> search(QueryResponse response) throws IOException, SolrServerException {
        List<FacetField.Count> counts = response.getFacetFields().get(0).getValues();
        if (StringUtil.notNull(counts))
        {
            List<String> list = Lists.newArrayList();
            for (FacetField.Count count : counts)
            {
                list.add(count.getName());
            }
           return repository.search(list);
        }
        return null;
    }
}
