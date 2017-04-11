package com.berchina.seo.server.provider.server;

import com.berchina.seo.server.provider.server.crud.CategoryRepository;
import com.berchina.seo.server.provider.utils.LogisticsEnum;
import com.berchina.seo.server.provider.utils.StringUtil;
import com.google.common.collect.Maps;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.assertj.core.util.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @Package com.berchina.seo.server.provider.server
 * @Description: TODO ( 类目、物流处理 )
 * @Author rxbyes
 * @Date 2017 下午9:59
 * @Version V1.0
 */
@Service
public class CategoryServer {

    @Autowired
    private CategoryRepository repository;

    public Map<String ,Object> search(QueryResponse response) throws IOException, SolrServerException {
        Map<String ,Object> maps = Maps.newConcurrentMap();
        {
            maps.put("category",repository.category(this.setfacet(response.getFacetFields().get(0).getValues())));

            maps.put("logistic",setLogistics(this.setfacet(response.getFacetFields().get(1).getValues())));
        }
        return maps;
    }

    public List<String> setfacet(List<FacetField.Count> counts) {
        List<String> objects = Lists.newArrayList();
        if (StringUtil.notNull(counts))
        {
            for (FacetField.Count count : counts)
            {
                if (count.getCount() > 0)
                {
                    objects.add(count.getName());
                }
            }
        }
        return objects;
    }

    public List<Map<String,String>> setLogistics(List<String> logisticsFacet) {
        Map<String,String> map = Maps.newTreeMap();
        for (String log : logisticsFacet)
        {
            List<String> logistics = StringUtil.splitter(",",log);
            for (String var : logistics)
            {
                if (var.equals(LogisticsEnum.WDZT.getCode()))
                {
                    map.put(var,LogisticsEnum.WDZT.getName());
                }
                if (var.equals(LogisticsEnum.SHSM.getCode()))
                {
                    map.put(var,LogisticsEnum.SHSM.getName());
                }
                if(var.equals(LogisticsEnum.WLPS.getCode()))
                {
                    map.put(var,LogisticsEnum.WLPS.getName());
                }
            }
        }
        return Lists.newArrayList(map);
    }
}
