package com.berchina.seo.server.provider.client.base;

import java.io.Serializable;

/**
 * @Package com.berchina.seo.server.provider.client
 * @Description: TODO ( 请求基类 )
 * @Author 任小斌  renxiaobin@berchin.com
 * @Date 16/9/13 下午6:33
 * @Version V1.0
 */
public class Request implements Serializable {

    private static final long serialVersionUID = -6469424593529028045L;
    /**
     * 请求时间
     */
    private String time;

    /**
     * 业务流水号
     */
    private String serialNum;

    /**
     * 搜索渠道 0000 代表商品搜索, 0001 代表智能联想, 0002 代表商铺搜索
     */
    private String channel;

    /**
     * 请求终端, app 移动端, pc 电脑端, wc 微信端
     */
    private String terminal;

    /**
     * 服务类名称
     */
    private String instance;

    /**
     * 热词搜索
     */
    private String hotwords;

    /**
     * 当前页
     */
    private String page;

    /**
     * 总页数
     */
    private String pageSize;

    public String getPage() {
        return page;
    }

    public void setPage(String page) {
        this.page = page;
    }

    public String getPageSize() {
        return pageSize;
    }

    public void setPageSize(String pageSize) {
        this.pageSize = pageSize;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }

    public String getHotwords() {
        return hotwords;
    }

    public void setHotwords(String hotwords) {
        this.hotwords = hotwords;
    }

    public String getTerminal() {
        return terminal;
    }

    public void setTerminal(String terminal) {
        this.terminal = terminal;
    }
}
