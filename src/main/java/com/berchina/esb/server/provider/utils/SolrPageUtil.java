package com.berchina.esb.server.provider.utils;

import com.berchina.esb.server.provider.client.SeoRequest;
import org.apache.solr.common.SolrDocumentList;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * @Package com.berchina.esb.server.provider.utils
 * @Description: TODO ( 搜索分页工具类 )
 * @Author xum
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


    public SolrPageUtil(int pageSize, int pageNum, int totalNum, int currentPage) {
        this.pageSize = pageSize;
        this.pageNum = pageNum;
        this.totalNum = totalNum;
        this.currentPage = currentPage;
    }


    public static void getPageInfo(Map<String, Object> seoCateMap, SeoRequest request, SolrDocumentList goodsDoc) {

        long totalNum = goodsDoc.getNumFound();

        /** 条码条数 */
        long rows = Long.valueOf(request.getRows());

        /** 总条数 */
        seoCateMap.put("totalNum", totalNum);

        /** 当前页数 */
        seoCateMap.put("page", request.getStart());

        /** 总页数 */
        long totalPage = totalNum / rows;

        /** 总页数 */
        seoCateMap.put("totalPage", totalNum % rows > 0 ? ++totalPage : totalPage);
    }


    public SolrPageUtil() {
    }

    public SolrPageUtil(LinkedList<Object> linkeList, int pageSize1) {
        this.pageSize = pageSize1;
        //计算总页数 的条数
        this.list = linkeList;
        this.totalNum = linkeList.size();
        if (this.totalNum % pageSize == 0) {
            pageNum = this.totalNum / pageSize;
        } else {
            pageNum = this.totalNum / pageSize + 1;
        }
    }

    /**
     *      * 分页
     *      * 
     *      * @param currentPage
     *      *            当前页数
     *      
     */
    public LinkedList<Object> getList(int currentPage) {
        if (currentPage > pageNum || currentPage < 1) {
            throw new RuntimeException("当前页数不能大于总页数并且不能小于1");
        } else {
            this.currentPage = currentPage;
            this.fromIndex = (this.currentPage - 1) * pageSize;
            if (this.currentPage == pageNum) {
                this.toIndex = list.size();
            } else {
                this.toIndex = this.currentPage * pageSize;
            }
            List<Object> obje = list.subList(Integer.parseInt(String.valueOf(fromIndex)), Integer.parseInt(String.valueOf(toIndex)));
            return new LinkedList<Object>(obje);
        }
    }


    public LinkedList<Object> getList() {
        return list;
    }

    public void setList(LinkedList<Object> list) {
        this.list = list;
    }

    public long getPageSize() {
        return pageSize;
    }

    public void setPageSize(long pageSize) {
        this.pageSize = pageSize;
    }

    public long getPageNum() {
        return pageNum;
    }

    public void setPageNum(long pageNum) {
        this.pageNum = pageNum;
    }

    public long getTotalNum() {
        return totalNum;
    }

    public void setTotalNum(long totalNum) {
        this.totalNum = totalNum;
        if (this.totalNum > 0) {
            if (this.totalNum % pageSize == 0) {
                this.pageNum = this.totalNum / pageSize;
            } else {
                this.pageNum = this.totalNum / pageSize + 1;
            }

        }


    }

    public long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(long currentPage) {
        this.currentPage = currentPage;
    }

    public long getFromIndex() {
        return fromIndex;
    }

    public void setFromIndex(long fromIndex) {
        this.fromIndex = fromIndex;
    }

    public long getToIndex() {
        return toIndex;
    }

    public void setToIndex(long toIndex) {
        this.toIndex = toIndex;
    }
}
