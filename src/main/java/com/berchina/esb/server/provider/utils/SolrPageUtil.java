package com.berchina.esb.server.provider.utils;

import com.berchina.esb.server.provider.client.SeoRequest;
import org.apache.solr.common.SolrDocumentList;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @Package com.berchina.esb.server.provider.utils
 * @Description: TODO ( 搜索分页工具类 )
 * @Author renxiaobin
 * @Date 2016年9月12日 上午9:42:55
 * @Version V1.0
 */
public class SolrPageUtil {
    
    public static void getPageInfo(Map<String, Object> seoMap, SeoRequest request, SolrDocumentList goodsDoc) {

        long totalNum = goodsDoc.getNumFound();

        /** 条码条数 */
        long rows = Long.valueOf(request.getRows());

        /** 总条数 */
        seoMap.put("totalNum", totalNum);

        /** 当前页数 */
        seoMap.put("page", request.getPage().equals("0") ? "1" : request.getPage());

        /** 总页数 */
        long totalPage = totalNum / rows;

        /** 总页数 */
        seoMap.put("totalPage", totalNum % rows > 0 ? ++totalPage : totalPage);
    }

    public SolrPageUtil() {
    }

}
