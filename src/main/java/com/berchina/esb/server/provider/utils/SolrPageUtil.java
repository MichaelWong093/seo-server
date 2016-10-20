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
    private LinkedList<Object> list; // 记录
    private long pageSize;// 每页显示的记录数
    private long pageNum;// 总页数
    private long totalNum;//总条数

    private long currentPage = 1;// 当前页
    private long fromIndex = 0;// 记录起始索引
    private long toIndex = 0;// 记录结束索引

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
    public LinkedList<Object> getList() {
        return list;
    }

    public void setList(LinkedList<Object> list) {
        this.list = list;
    }

}
